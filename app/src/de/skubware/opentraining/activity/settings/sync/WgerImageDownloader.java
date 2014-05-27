/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
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
import android.util.SparseArray;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.basic.License.LicenseType;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A class to encapsulate downloading missing images from wger.
 * 
 */
public class WgerImageDownloader {
	/** Tag for logging */
	private static final String TAG = "WgerImageDownloader";

	private String mLicenseJSONString;
	private Context mContext;
	private RestClient mClient;

	public WgerImageDownloader(String licenseJSONString, Context context, RestClient client) {
		mLicenseJSONString = licenseJSONString;
		mContext = context;
		mClient = client;
	}

	/**
	 * Download the missing images from wger. If an image already exists Open
	 * Training assumes that the exercise is duplicate and will remove it from
	 * the download list.
	 */
	public ArrayList<ExerciseType> downloadImages(List<ExerciseType.Builder> exerciseBuilderList) throws IOException, JSONException {
		ArrayList<ExerciseType> newExerciseList = new ArrayList<ExerciseType>();

		// add label to be able to break/continue from inner loop
		outerloop:
		for (ExerciseType.Builder exBuilder : exerciseBuilderList) {
			List<File> newImagePathList = new ArrayList<File>();
			Map<File, License> newImageLicenseMap = new HashMap<File, License>();

			ExerciseType ex = exBuilder.build();
			for (File img : ex.getImagePaths()) {
				String imageAsJSON = mClient.get(img.getPath() + "/");
				
				// get image name
				JSONObject imageJSONObject = new JSONObject(imageAsJSON);
				String imageDownloadPath = imageJSONObject.getString("image");
				
				
				// parse JSON and get license
				SparseArray<LicenseType> licenseSparseArray = WgerJSONParser.parseLicenses(mLicenseJSONString);

				int licenseNumber = WgerJSONParser.getLastNumberOfJson(imageJSONObject.getString("license"));
				LicenseType licenseType = licenseSparseArray.get(licenseNumber);
				String author = imageJSONObject.getString("license_author");
				Log.v(TAG, "license=" + licenseType.toString() + " license_author=" + author);
				
				License license = new License(licenseType, author);
				//String licensePath = imageJSONObject.getString("license");

				
				// skip exercise (and image download) if there's already one with the same name
				DataHelper dataHelper = new DataHelper(mContext);
				String imageName = (new File(imageDownloadPath)).getName();
				if(dataHelper.drawableExist(imageName)){
					Log.d(TAG, "There's already an image with the same name as: " + imageDownloadPath + ". The exercise: " + ex.getLocalizedName() + " is propably duplicate, it will not be added.");
					continue outerloop;
				}else{
					// only download image if its name is unique
					imageName = downloadImageToSyncedImagesFolder(imageDownloadPath); // imageName may change!
				}
				
				// add image name + license to list/map
				File imageFile = new File(imageName);
				newImagePathList.add(imageFile);
				newImageLicenseMap.put(imageFile, license);
			}
			// set collected values for builder, add new object to exercise list
			exBuilder.imagePath(newImagePathList);
			exBuilder.imageLicenseMap(newImageLicenseMap);

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
