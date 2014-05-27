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


package de.skubware.opentraining.activity.start_training;

import java.util.Timer;
import java.util.TimerTask;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
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
	private static final int RECOVERY_TIMER_NOTIFICATION_ID = 0;

	private Context mContext;
	
	private NotificationCompat.Builder mNotificationBuilder;
	private NotificationManager mNotifyManager;
	private Timer mSetTimer = new Timer();;

	private boolean mVibrationEnabled = true;
	private boolean mNotificationSoundEnabled = true;
	
	
	private enum RecoveryTimerKind{
		SET_RECOVER_TIMER,
		EXERCISE_RECOVERY_TIMER;
		
		// not using getter/setter to avoid the boilerplate code
		// as this is a private enum this should not be a problem
		public int timerDurationInSec = 0;
		public String tickerRunning = "Erholungsphase ...";
		public String contentTitleRunning = "Erholungsphase ...";
		public String tickerFinished = "Ende Erholungsphase";
		public String contentTitleFinished = "Erholungsphase beendet";
	};

	
	/**
	 * Starts the set recovery timer, the app will display a notification.
	 * Deletes/stops already running recovery timers.
	 * 
	 * @param context Current application context.
	 */
	public synchronized void startSetRecoveryTimer(Context context){
		if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("training_timer_enabled", true)){
			Log.v(TAG, "Will not start training timer, as it has been disabled.");
			return;
		}
		
		setUp(context);

		// make sure to stop an already running recovery timer
		stopRecoveryTimer();
		startTimer(RecoveryTimerKind.SET_RECOVER_TIMER);
	}
	
	/**
	 * Starts the exercise recovery timer, the app will display a notification.
	 * Deletes/stops already running recovery timers.
	 * 
	 * @param context Current application context.
	 */
	public synchronized void startExerciseRecoveryTimer(Context context){
		if(!PreferenceManager.getDefaultSharedPreferences(context).getBoolean("training_timer_enabled", true)){
			Log.v(TAG, "Will not start training timer, as it has been disabled.");
			return;
		}
		
		setUp(context);

		// make sure to stop an already running set timer
		stopRecoveryTimer();
		startTimer(RecoveryTimerKind.EXERCISE_RECOVERY_TIMER);

	}
	
	/** Updates context and user settings. */
	private void setUp(Context context){
		mContext = context;
		mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
		
		// update user settings/preferences
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		mVibrationEnabled = prefs.getBoolean("training_timer_vibration_enabled", true);
		mNotificationSoundEnabled = prefs.getBoolean("training_timer_sound_enabled", true);

		RecoveryTimerKind.EXERCISE_RECOVERY_TIMER.timerDurationInSec = Integer.valueOf(prefs.getString("training_timer_exercise_recovery_time", "180"));
		RecoveryTimerKind.EXERCISE_RECOVERY_TIMER.contentTitleFinished = mContext.getString(R.string.exercise_recover_timer_content_title_finished);
		RecoveryTimerKind.EXERCISE_RECOVERY_TIMER.contentTitleRunning = mContext.getString(R.string.exercise_recover_timer_content_title_running);
		RecoveryTimerKind.EXERCISE_RECOVERY_TIMER.tickerFinished = mContext.getString(R.string.exercise_recover_timer_ticker_finished);
		RecoveryTimerKind.EXERCISE_RECOVERY_TIMER.tickerRunning = mContext.getString(R.string.exercise_recover_timer_ticker_running);
		
		RecoveryTimerKind.SET_RECOVER_TIMER.timerDurationInSec = Integer.valueOf(prefs.getString("training_timer_set_recovery_time", "30"));
		RecoveryTimerKind.SET_RECOVER_TIMER.contentTitleFinished = mContext.getString(R.string.set_recover_timer_content_title_finished);
		RecoveryTimerKind.SET_RECOVER_TIMER.contentTitleRunning = mContext.getString(R.string.set_recover_timer_content_title_running);
		RecoveryTimerKind.SET_RECOVER_TIMER.tickerFinished = mContext.getString(R.string.set_recover_timer_ticker_finished);
		RecoveryTimerKind.SET_RECOVER_TIMER.tickerRunning = mContext.getString(R.string.set_recover_timer_ticker_running);
	}
	
	
	private synchronized void startTimer(final RecoveryTimerKind timerKind) {		
		// create notification
		mNotificationBuilder = new NotificationCompat.Builder(mContext);
		mNotificationBuilder.setContentTitle(timerKind.contentTitleRunning)
		.setSmallIcon(R.drawable.icon_dumbbell_small)
		.setLargeIcon(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.icon_dumbbell));

		final boolean doVibrate = mVibrationEnabled;
		final boolean doBeep = mNotificationSoundEnabled;
		int maxInSec = timerKind.timerDurationInSec;
		final int max = 10 * maxInSec; 
		mSetTimer = new Timer();

		TimerTask task = new TimerTask() {
			private int counter = 0;

			@Override
			public void run() {
				// update progress if still running
				if (counter < max) {
					mNotificationBuilder.setTicker(timerKind.tickerRunning);
					mNotificationBuilder.setProgress(max, counter, false);
					mNotificationBuilder.setUsesChronometer(true);
					// Displays the progress bar for the first time.
					mNotificationBuilder.setContentIntent(PendingIntent.getActivity(mContext.getApplicationContext(), 0, new Intent(), 0));
					mNotifyManager.notify(0, mNotificationBuilder.build());

					counter++;
				} else {
					mNotificationBuilder.setProgress(max, max, false);
					//mNotificationBuilder.setNumber(max);

					// change message if progress is finished
					mNotificationBuilder.setContentTitle(timerKind.contentTitleFinished);
					mNotificationBuilder.setTicker(timerKind.tickerFinished);

					// let phone vibrate (unless user disabled this feature)
					if (doVibrate) {
						long[] vibrationPattern = { 0, 300 };
						mNotificationBuilder.setVibrate(vibrationPattern);
					}
					// make a notification sound (unless user disabled this feature)
					if (doBeep) {
						Uri defaultSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
						mNotificationBuilder.setSound(defaultSound);
					}
					mNotificationBuilder.setAutoCancel(true);
					
					mNotificationBuilder.setContentIntent(PendingIntent.getActivity(mContext.getApplicationContext(), 0, new Intent(), 0));


					mNotifyManager.notify(RECOVERY_TIMER_NOTIFICATION_ID, mNotificationBuilder.build());
					mSetTimer.cancel();

				}

			}
		};

		long period = 100;
		long delay = 0;
		mSetTimer.scheduleAtFixedRate(task, delay, period);
	}
	
	
	/**
	 * Stops an already running recovery timer (SET_RECOVER_TIMER,
	 * as well as EXERCISE_RECOVERY_TIMER).
	 * 
	 * Will do nothing if there's no set recovery timer running.
	 */
	public synchronized void stopRecoveryTimer(Context context){
		setUp(context);
		stopRecoveryTimer();
	}
	
	/** @see #stopRecoveryTimer(Context) */
	private synchronized void stopRecoveryTimer(){		
		mSetTimer.cancel();
		mNotifyManager.cancel(RECOVERY_TIMER_NOTIFICATION_ID);
	}
		
}
