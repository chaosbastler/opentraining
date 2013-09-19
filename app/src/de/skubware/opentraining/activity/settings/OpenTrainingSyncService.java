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
import java.util.List;

import de.skubware.opentraining.basic.ExerciseType;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;


public class OpenTrainingSyncService extends IntentService {
	/** Indicates that the query is running */
	public static final int STATUS_RUNNING = 1;
	/** Indicates that the query is finished*/
	public static final int STATUS_FINISHED = 2;
	/** Indicates that the query could not be executed properly */
	public static final int STATUS_ERROR = 3;
	
	
	public static final String EXTRA_VERSION_CODE = "version";
	public static final String EXTRA_HOST = "host";
	
	public static final int DEFAULT_PORT = 80;
	
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
		port = 443;

		
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
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }    
        }
        this.stopSelf();
    }
	
	private String getExercisesAsJSON() throws IOException{
		Log.d(TAG, "getExercisesAsJSON()");
    	return mClient.get("/api/v1/exercise/");
	}
	
	private List<ExerciseType> parseJSONExercises(){
		return null;
	}

}