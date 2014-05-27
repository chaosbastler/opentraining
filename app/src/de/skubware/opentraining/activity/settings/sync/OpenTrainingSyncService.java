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

import java.io.IOException;
import java.util.ArrayList;
import org.json.JSONException;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;

/**
 * A service for syncing OpenTraining with wger.
 *
 */
public class OpenTrainingSyncService extends IntentService {
	/** Indicates that the query is running */
	public static final int STATUS_RUNNING_DOWNLOAD_EXERCISES = 1;
	/** Indicates that the query is running */
	public static final int STATUS_RUNNING_DOWNLOAD_LANGUAGE_FILES = 2;
	/** Indicates that the query is running */
	public static final int STATUS_RUNNING_DOWNLOAD_MUSCLE_FILES = 3;	
	/** Indicates that the query is running */
	public static final int STATUS_RUNNING_DOWNLOAD_EQUIPMENT_FILES = 4;
	/** Indicates that the query is running */
	public static final int STATUS_RUNNING_DOWNLOAD_LICENSE_FILES = 5;	
	/** Indicates that the query is running and exercises are parsed */
	public static final int STATUS_RUNNING_CHECKING_EXERCISES = 8;
	/** Indicates that the query is running and images are downloaded */
	public static final int STATUS_RUNNING_DOWNLOADING_IMAGES = 9;	
	/** Indicates that the query is finished*/
	public static final int STATUS_FINISHED = 10;
	/** Indicates that the query could not be executed properly */
	public static final int STATUS_ERROR = 66;
	
	/** Key for intent extra (version of Open Training) */
	public static final String EXTRA_VERSION_CODE = "version";
	/** Key for intent extra (host) */
	public static final String EXTRA_HOST = "host";
	
	/** The path for getting the exercises as JSON */
	public static final String EXERCISE_REQUEST_PATH = "/api/v1/exercise/?status__in=2,4,5&limit=0";
	/** The path for getting the languages as JSON */
	public static final String LANGUAGE_REQUEST_PATH = "/api/v1/language/";
	/** The path for getting the muscles as JSON */
	public static final String MUSCLE_REQUEST_PATH = "/api/v1/muscle/";
	/** The path for getting the equipment as JSON */
	public static final String EQUIPMENT_REQUEST_PATH = "/api/v1/equipment/";
	/** The path for getting the licenses as JSON */
	public static final String LICENSE_REQUEST_PATH = "/api/v1/license/";	
	
	
	/** The used {@link RestClient}. */
	private RestClient mClient;
	
	/** Class that receives the results of this service */
    private ResultReceiver mReceiver;
    
	private String host;
	private int version;
	private int port;

	/** Tag for logging */
	private static final String TAG = "OpenTrainingSyncService";

	
	
	public OpenTrainingSyncService() {
		super(TAG);
		Log.d(TAG, "OpenTrainingSyncService created");
		
	}

	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent()");

		version = intent.getIntExtra(EXTRA_VERSION_CODE, -1);
		host = intent.getStringExtra(EXTRA_HOST);

		
		mReceiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if(command.equals("query")) {
            try {
            	
            	// set up REST-Client
            	mClient = new RestClient(host, port, "https", version);
            	
            	// download and parse the exercises
        		ArrayList<ExerciseType> allExercises = downloadAndParseExercises();

        		// add data to bundle
        		b.putSerializable("all_exercises", allExercises);


                mReceiver.send(STATUS_FINISHED, b);
            } catch(Exception e) {
            	Log.e(TAG, "Error, could not get exercises from server: " + e.toString(), e);
                b.putString(Intent.EXTRA_TEXT, e.toString());
                mReceiver.send(STATUS_ERROR, b);
            }    
        }
        this.stopSelf();
    }
	
	
	/**
	 * Downloads the JSON-files from wger and parses them.
	 * 
	 * @return the {@link WgerJSONParser} 
	 * 
	 * @throws IOException
	 * @throws JSONException
	 */
	private ArrayList<ExerciseType> downloadAndParseExercises() throws IOException, JSONException{
		Log.d(TAG, "getExercisesAsJSON()");
		IDataProvider dataProvider = new DataProvider(this.getApplicationContext());

		// get exercises from server
		mReceiver.send(STATUS_RUNNING_DOWNLOAD_EXERCISES, Bundle.EMPTY);
		String exercisesAsJSON = mClient.get(EXERCISE_REQUEST_PATH);
		Log.v(TAG, "exercisesAsJSON: " + exercisesAsJSON);
		
		// get languages from server
		mReceiver.send(STATUS_RUNNING_DOWNLOAD_LANGUAGE_FILES, Bundle.EMPTY);
		String languagesAsJSON = mClient.get(LANGUAGE_REQUEST_PATH);
		Log.v(TAG, "languagesAsJSON: " + languagesAsJSON);

		// get muscles from server
		mReceiver.send(STATUS_RUNNING_DOWNLOAD_MUSCLE_FILES, Bundle.EMPTY);
		String musclesAsJSON = mClient.get(MUSCLE_REQUEST_PATH);
		Log.v(TAG, "musclesAsJSON: " + musclesAsJSON);

		// get licenses from server
		mReceiver.send(STATUS_RUNNING_DOWNLOAD_LICENSE_FILES, Bundle.EMPTY);
		String licenseAsJSON = mClient.get(LICENSE_REQUEST_PATH);
		Log.v(TAG, "licenseAsJSON: " + licenseAsJSON);
		
		
		// get equipment from server
		mReceiver.send(STATUS_RUNNING_DOWNLOAD_EQUIPMENT_FILES, Bundle.EMPTY);
		String equipmentAsJSON = mClient.get(EQUIPMENT_REQUEST_PATH);
		Log.v(TAG, "equipmentAsJSON: " + equipmentAsJSON);


		// parse exercises, languages and muscles
		mReceiver.send(STATUS_RUNNING_CHECKING_EXERCISES, Bundle.EMPTY);
		WgerJSONParser wgerParser = new WgerJSONParser(exercisesAsJSON, languagesAsJSON, musclesAsJSON, equipmentAsJSON, licenseAsJSON, dataProvider);
		
		ArrayList<ExerciseType.Builder> exerciseBuilderList = wgerParser.getNewExercisesBuilder();

		// get images from server
		mReceiver.send(STATUS_RUNNING_DOWNLOADING_IMAGES, Bundle.EMPTY);
		WgerImageDownloader imageDownloader = new WgerImageDownloader(licenseAsJSON, getApplicationContext(), mClient);
		ArrayList<ExerciseType> newExerciseList = imageDownloader.downloadImages(exerciseBuilderList);

		
    	return newExerciseList;
	}


	


}