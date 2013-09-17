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

package de.skubware.opentraining.test.sync;

import java.net.URL;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.AndroidTestCase;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.basic.FSet.SetParameter.*;

/**
 * Tests downloading exercises from wger.
 *
 */
public class DownloadExercisesTest extends AndroidTestCase {
	/** Tag for logging */
	public static final String TAG = "DownloadExercisesTest";

	final String WGER_URL = "http://wger.de";
	
	final String EXERCISE_NAME_1 = "Curl";
	final String EXERCISE_NAME_2 = "Crunch";
	final String EXERCISE_NAME_3 = "Benchpress";
	
	public void testAvailability() {
		boolean online = false;
		ConnectivityManager cm = (ConnectivityManager) getContext()
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			online = true;
		}
		
		assertTrue("No internet connection available, tests can't run.", online);
	}

	public void testDownload(){


	}

}
