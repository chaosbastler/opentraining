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

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.test.ServiceTestCase;
import de.skubware.opentraining.activity.settings.OpenTrainingSyncResultReceiver;
import de.skubware.opentraining.activity.settings.OpenTrainingSyncService;

/**
 * Tests downloading exercises from wger.
 *
 */
public class DownloadExercisesTest extends ServiceTestCase<OpenTrainingSyncService>  implements OpenTrainingSyncResultReceiver.Receiver{

	/** Tag for logging */
	public static final String TAG = "DownloadExercisesTest";

	boolean finished = false;
	
	public DownloadExercisesTest() {
		super(OpenTrainingSyncService.class);
	}

	
	static final String WGER_URL = "http://wger.de";


	public void testDownload() throws InterruptedException{
		OpenTrainingSyncResultReceiver mReceiver = new OpenTrainingSyncResultReceiver(new Handler());
	    mReceiver.setReceiver(this);
		
		
		final Intent intent = new Intent(Intent.ACTION_SYNC, null, getContext(), OpenTrainingSyncService.class);
		intent.putExtra("receiver", mReceiver);
		intent.putExtra("command", "query");
		startService(intent);

		Thread.sleep(5000); 

		assertTrue(finished);
		
	}


	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		//TODO check result
		finished = true;
	}

}
