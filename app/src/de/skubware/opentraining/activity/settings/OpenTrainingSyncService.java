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

package de.skubware.opentraining.activity.settings;

import java.io.IOException;
import org.json.JSONException;

import de.skubware.opentraining.activity.settings.sync.WgerJSONParser;
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
	public static final int STATUS_RUNNING = 1;
	/** Indicates that the query is finished*/
	public static final int STATUS_FINISHED = 2;
	/** Indicates that the query could not be executed properly */
	public static final int STATUS_ERROR = 3;
	
	/** Key for intent extra (version of Open Training) */
	public static final String EXTRA_VERSION_CODE = "version";
	/** Key for intent extra (host) */
	public static final String EXTRA_HOST = "host";
	
	/** The path for getting the exercises as JSON */
	public static final String EXERCISE_REQUEST_PATH = "/api/v1/exercise/?limit=1000";
	/** The path for getting the languages as JSON */
	public static final String LANGUAGE_REQUEST_PATH = "/api/v1/language/";
	/** The path for getting the muscles as JSON */
	public static final String MUSCLE_REQUEST_PATH = "/api/v1/muscle/";
	
	/** The used {@link RestClient}. */
	private RestClient mClient;
	
	
	private String host;
	private int version;
	private int port;

	/** Tag for logging */
	private static final String TAG = "OpenTrainingSyncService";

	
	
	public OpenTrainingSyncService() {
		super(TAG);
		Log.d(TAG, "OpenTrainingSyncService created");
		

		//TODO get Port from preferences
	}

	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent()");

		version = intent.getIntExtra(EXTRA_VERSION_CODE, -1);
		host = intent.getStringExtra(EXTRA_HOST);

		
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if(command.equals("query")) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
            	
            	// set up REST-Client
            	mClient = new RestClient(host, port, "https", version);

            	
            	String exercises = getExercisesAsJSON();
                b.putString("exercises", exercises);

                receiver.send(STATUS_FINISHED, b);
            } catch(Exception e) {
            	Log.e(TAG, "Error, could not get exercises from server: " + e.toString(), e);
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }    
        }
        this.stopSelf();
    }
	
	private String getExercisesAsJSON() throws IOException, JSONException{
		Log.d(TAG, "getExercisesAsJSON()");
		IDataProvider dataProvider = new DataProvider(this.getApplicationContext());
		
		// get exercises from server
		String exercisesAsJSON = mClient.get(EXERCISE_REQUEST_PATH);
		Log.v(TAG, "exercisesAsJSON: " + exercisesAsJSON);
		
		// get languages from server
		String languagesAsJSON = mClient.get(LANGUAGE_REQUEST_PATH);
		Log.v(TAG, "languagesAsJSON: " + languagesAsJSON);

		// get muscles from server
		String musclesAsJSON = mClient.get(MUSCLE_REQUEST_PATH);
		Log.v(TAG, "musclesAsJSON: " + musclesAsJSON);

		WgerJSONParser wgerParser = new WgerJSONParser(exercisesAsJSON, languagesAsJSON, musclesAsJSON , dataProvider);
		wgerParser.getNewExerciseList();
		
		
    	return exercisesAsJSON;
	}

	


}