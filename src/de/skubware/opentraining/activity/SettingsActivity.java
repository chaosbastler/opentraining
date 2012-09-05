/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
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

package de.skubware.opentraining.activity;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.opentraining.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * The user can download new exercises with this activity. Download will be done
 * in a background thread, UI will show the download progress.
 * 
 * I am not really sure if I covered all possible problems that might occur when
 * starting multiple threads.
 * 
 * @author Christian Skubich
 * 
 */
public class SettingsActivity extends Activity {
	/** Tag for logging */
	private static final String TAG = "ManageDatabaseActivity";
	
	/** Number of the file that's downloaded at the moment */
	private int current;
	/** Total number of files that should be downloaded */
	private int total;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);

		Button button_download = (Button) findViewById(R.id.button_download);
		button_download.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// warn user if there is no internet connection
				if (!isOnline()) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
					builder.setMessage(getString(R.string.no_internet_connection)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					return;
				}

				// when button was clicked -> start download in new thread
				new Thread(new Runnable() {
					public void run() {
						current = 0;
						total = 0;

						// delete the old folder
						delete(DataManager.getAppFolder());
						// start downloading new exercises
						EditText edittext_url = (EditText) findViewById(R.id.edittext_url);
						download(edittext_url.getText().toString());

					}
				}).start();

			}

		});

		Button button_check_database = (Button) findViewById(R.id.button_check_database);
		button_check_database.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				checkDatabase();
			}
		});

	}

	/**
	 * 
	 */
	private void checkDatabase() {
		DataManager.INSTANCE.loadExercises(this);
		int exCnt = ExerciseType.listExerciseTypes().size();
		appendMsg(exCnt + " " + getString(R.string.valid_exercises));

		int totalEx = DataManager.getExerciseXMLFolder().listFiles().length;
		appendMsg(totalEx - exCnt + " " + getString(R.string.not_valid_exercises));

		int imgCnt = 0;
		for (ExerciseType ex : ExerciseType.listExerciseTypes()) {
			imgCnt += ex.getImagePaths().size();
		}
		appendMsg(imgCnt + " " + getString(R.string.images));

	}

	/**
	 * Checks the network status. Note: duplicate code, see ShowTPActivity.java
	 * 
	 * @return True, if internet connection is available, false otherwise.
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}

	/**
	 * Deletes a directory
	 * 
	 * @param dir
	 *            The directory to delete.
	 * 
	 * @return True, if deletion was successful, false otherwise.
	 */
	private boolean delete(File dir) {
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File aktFile : files) {
				delete(aktFile);
			}
		}
		return dir.delete();
	}

	/**
	 * Downloads a single file.
	 * 
	 * 
	 * @param urlString
	 *            The URL of the file that should be downloaded.
	 * 
	 * @return True if no problem did occur.
	 */
	public boolean download(String urlString) {

		URL url = null;
		InputStream is = null;
		DataInputStream dis;
		String line;
		List<String> lines = new ArrayList<String>();

		try {
			url = new URL(urlString);
			is = url.openStream(); // throws an IOException
			dis = new DataInputStream(new BufferedInputStream(is));

			while ((line = dis.readLine()) != null) {
				lines.add(line);
				total++;
			}
		} catch (MalformedURLException mue) {
			mue.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
				// nothing to see here
			}
		}
		
		// 'calculate' the name of the folder of the exercises
		String urlBase = urlString.substring(0 , urlString.lastIndexOf('/') );

		

		boolean succ = true;
		for (String name : lines) {
			File dest;
			if (name.endsWith(".xml"))
				dest = DataManager.getExerciseXMLFolder();
			else
				dest = DataManager.getImageFolder();



			succ = download(dest, urlBase +"/" + name) & succ;

		}
		
		// finally reload database
		DataManager.INSTANCE.loadExercises(SettingsActivity.this);

		return succ;

	}

	/**
	 * Downloads a list of files.
	 * 
	 * How the list with files should look like: - just a list of the names of
	 * the files - every image is in the same folder as the list of the files -
	 * the files have to be separated by new lines For an example simply have a
	 * look at @value{EXERCISE_SOURCE}
	 * 
	 * @param destination
	 *            The destination folder.
	 * @param urlString
	 *            The URL of the list of files.
	 * 
	 * @return True if no problem did occur.
	 */
	public boolean download(File destination, String urlString) {

		try {
			// set the download URL, a url that points to a file on the internet
			// this is the file to be downloaded
			URL url = new URL(urlString);

			// create the new connection
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

			// set up some things on the connection
			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(true);

			// and connect!
			urlConnection.connect();

			// create a new file, specifying the path, and the filename
			// which we want to save the file as.
			String[] split = urlString.split("/");
			String fileName = split[split.length - 1];
			Log.i(TAG, fileName);

			File file = new File(destination, fileName);

			// this will be used to write the downloaded data into the file we
			// created
			FileOutputStream fileOutput = new FileOutputStream(file);

			// this will be used in reading the data from the internet
			InputStream inputStream = urlConnection.getInputStream();

			// this is the total size of the file
			// int totalSize = urlConnection.getContentLength();
			// variable to store total downloaded bytes
			// int downloadedSize = 0;

			// create a buffer...
			byte[] buffer = new byte[1024];
			int bufferLength = 0; // used to store a temporary size of the
									// buffer

			// now, read through the input buffer and write the contents to the
			// file
			while ((bufferLength = inputStream.read(buffer)) > 0) {
				// add the data in the buffer to the file in the file output
				// stream (the file on the sd card
				fileOutput.write(buffer, 0, bufferLength);
				// add up the size so we know how much is downloaded
				// downloadedSize += bufferLength;
				// here a progressbar might be updated

			}
			// close the output stream when done
			fileOutput.close();

			current++;

			// Updates for UI
			runOnUiThread(new Runnable() {
				public void run() {
					String msg = "Download " + current + " of " + total + " finished";
					appendMsg(msg);
				}
			});

			// catch some possible errors...
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private void appendMsg(String msg) {
		EditText edittext_information = (EditText) findViewById(R.id.edittext_information);
		edittext_information.append(msg + "\n");
	}

}
