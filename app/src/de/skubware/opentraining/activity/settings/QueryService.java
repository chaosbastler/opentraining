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


import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;


public class QueryService extends IntentService {
	public static final int STATUS_RUNNING = 1;
	public static final int STATUS_FINISHED = 2;
	public static final int STATUS_ERROR = 3;
	
	private static final String TAG = "QueryService";

	
	public QueryService(String name) {
		super(name);
	}

	protected void onHandleIntent(Intent intent) {
        final ResultReceiver receiver = intent.getParcelableExtra("receiver");
        String command = intent.getStringExtra("command");
        Bundle b = new Bundle();
        if(command.equals("query")) {
            receiver.send(STATUS_RUNNING, Bundle.EMPTY);
            try {
                // get some data or something           
            	//TODO add real data
                b.putParcelableArrayList("results", null);
                receiver.send(STATUS_FINISHED, b);
            } catch(Exception e) {
                b.putString(Intent.EXTRA_TEXT, e.toString());
                receiver.send(STATUS_ERROR, b);
            }    
        }
        this.stopSelf();
    }
}