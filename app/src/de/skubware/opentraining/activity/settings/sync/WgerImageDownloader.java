/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Christian Skubich
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package de.skubware.opentraining.activity.settings.sync;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A class to encapsulate downloading missing images from wger.
 * 
 */
public class WgerImageDownloader {
	/** Tag for logging */
	private static final String TAG = "WgerImageDownloader";

	private Context mContext;
	private RestClient mClient;

	public WgerImageDownloader(Context context, RestClient client) {
		mContext = context;
		mClient = client;
	}

	public ArrayList<ExerciseType> downloadImages(List<ExerciseType.Builder> exerciseBuilderList) throws IOException, JSONException {
		ArrayList<ExerciseType> newExerciseList = new ArrayList<ExerciseType>();

		for (ExerciseType.Builder exBuilder : exerciseBuilderList) {
			List<File> newImagePathList = new ArrayList<File>();
			Map<File, String> newImageLicenseMap = new HashMap<File, String>();

			for (File img : exBuilder.build().getImagePaths()) {
				String imageAsJSON = mClient.get(img.getPath() + "/");
				// parse JSON and get name
				JSONObject imageJSONObject = new JSONObject(imageAsJSON);
				String imageDownloadPath = imageJSONObject.getString("image");
				
				
				// parse JSON and get license
				int licenseID = imageJSONObject.getInt("license");
				String license = "Unknown";
				if (licenseID == 1) {
					license = "CC-BY-SA 3.0";
				}
				// missing in api: get license like:
				// String license = mClient.get(licenseResource);
				String author = imageJSONObject.getString("license_author");
				String licenseText = "License: " + license + ", Author: " + author;

				
				// skip image if there's already one with the same name
				DataHelper dataHelper = new DataHelper(mContext);
				String imageName = (new File(imageDownloadPath)).getName();
				if(dataHelper.drawableExist(imageName)){
					Log.w(TAG, "There's already an image with the same name as: " + imageDownloadPath + ". Download will be skipped.");
				}else{
					// only download image if its name is unique
					imageName = downloadImageToSyncedImagesFolder(imageDownloadPath); // imageName may change!
				}
				
				// add image name + license to list/map
				File imageFile = new File(imageName);
				newImagePathList.add(imageFile);
				newImageLicenseMap.put(imageFile, licenseText);
			}
			// set collected values for builder, add new object to exercise list
			exBuilder.imagePath(newImagePathList);
			exBuilder.imageLicenseText(newImageLicenseMap);

			newExerciseList.add(exBuilder.build());

		}
		return newExerciseList;
	}

	/**
	 * 
	 * @param urlToDownload
	 *            the url
	 * @return the name of the downloaded image. This name can be different from
	 *         the original name if there's already a file with the same name.
	 */
	private String downloadImageToSyncedImagesFolder(String urlToDownload) {
		try {
			URL url = new URL(urlToDownload);
			URLConnection connection = url.openConnection();
			connection.connect();
			// this will be useful so that you can show a typical 0-100%
			// progress bar
			int fileLength = connection.getContentLength();

			// download the file
			InputStream input = new BufferedInputStream(url.openStream());

			// create output folder
			String outputFolder = mContext.getFilesDir().toString() + "/" + IDataProvider.SYNCED_IMAGES_FOLDER;
			(new File(outputFolder)).mkdir();
			String imageName = new File(urlToDownload).getName();
			String outputPath = outputFolder + "/" + imageName;

			// skip files that already exist
			if ((new File(outputPath)).exists()) {
				Log.e(TAG, "already such a file: " + outputPath);
				Log.e(TAG, "Will SKIP this file.");
				return imageName;
			}
			OutputStream output = new FileOutputStream(outputPath);

			byte data[] = new byte[1024];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				total += count;
				// publishing the progress....
				Bundle resultData = new Bundle();
				resultData.putInt("progress", (int) (total * 100 / fileLength));
				// receiver.send(UPDATE_PROGRESS, resultData);
				output.write(data, 0, count);
			}

			output.flush();
			output.close();
			input.close();

			return imageName;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

}
