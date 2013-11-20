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


package de.skubware.opentraining.activity.start_training;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.skubware.opentraining.R;

/**
 * Singleton class for managing the recovery-time-notification.
 * Using the singleton pattern for making it easier to control the notification within the whole application.
 *
 */
public enum RecoveryTimerManager {
	INSTANCE;
	
	private static final String TAG = "RecoveryTimerManager";
	
	private Context mContext;
	
	public void start(Context context){
		mContext = context;
	}
	
	public synchronized void startSetRecoveryTimer(Context context){
		mContext = context;
		startTimer();
	}
	
	public synchronized void startExerciseRecoveryTimer(Context context){
		mContext = context;
	}
	
	
	
	private void startTimer() {
		// create notification
		final NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(mContext);
		mNotificationBuilder.setContentTitle("Erholungsphase ...")
		.setSmallIcon(R.drawable.icon_dumbbell_small)
		.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.icon_dumbbell))
		.build();

		final NotificationManager mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

		// TODO read values from prefs
		// SharedPreferences prefs =
		// PreferenceManager.getDefaultSharedPreferences(this);
		final boolean doVibrate = true;
		final boolean doBeep = true;
		int maxInSec = 30;
		final int max = 10 * maxInSec; // = prefs.getInt("training_timer_duration", 60)
		final Timer timer = new Timer();

		TimerTask task = new TimerTask() {
			private int counter = 0;

			@Override
			public void run() {
				// update progress if still running
				if (counter < max) {
					mNotificationBuilder.setTicker("Erholungsphase");
					mNotificationBuilder.setProgress(max, counter, false);
					//mNotificationBuilder.setNumber(counter);
					mNotificationBuilder.setUsesChronometer(true);
					// Displays the progress bar for the first time.
					mNotifyManager.notify(0, mNotificationBuilder.build());

					counter++;
				} else {
					mNotificationBuilder.setProgress(max, max, false);
					//mNotificationBuilder.setNumber(max);

					// change message if progress is finished
					mNotificationBuilder.setContentTitle("Erholungsphase beendet");
					mNotificationBuilder.setTicker("Ende Erholungsphase");

					// let phone vibrate (unless user disabled this feature)
					if (doVibrate) {
						long[] vibrationPattern = { 0, 300 };
						mNotificationBuilder.setVibrate(vibrationPattern);
					}
					if (doBeep) {
						Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						mNotificationBuilder.setSound(defaultSound);
					}
					mNotificationBuilder.setAutoCancel(true);
					mNotifyManager.notify(0, mNotificationBuilder.build());
					timer.cancel();

				}

			}
		};

		long period = 100;
		long delay = 0;
		timer.scheduleAtFixedRate(task, delay, period);
	}
	
}
