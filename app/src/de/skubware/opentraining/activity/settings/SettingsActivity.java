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

package de.skubware.opentraining.activity.settings;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.ChangeLogDialog;
import de.skubware.opentraining.activity.settings.sync.OpenTrainingSyncResultReceiver;
import de.skubware.opentraining.activity.settings.sync.OpenTrainingSyncService;
import de.skubware.opentraining.activity.settings.sync.SyncFinishedDialog;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.db.Cache;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends PreferenceActivity  implements OpenTrainingSyncResultReceiver.Receiver {
	/**
	 * Determines whether to always show the simplified settings UI, where
	 * settings are presented in a single list. When false, settings are shown
	 * as a master/detail two-pane view on tablets. When true, a single pane is
	 * shown on tablets.
	 */
	private static final boolean ALWAYS_SIMPLE_PREFS = false;

	/** Tag for logging */
	public static final String TAG = "SettingsActivity";
	
	/** Handles syncing with wger */
    public OpenTrainingSyncResultReceiver mReceiver;

    /** Shows sync progress */
	ProgressDialog mProgressDialog;


    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        mReceiver = new OpenTrainingSyncResultReceiver(new Handler());
        mReceiver.setReceiver(this);
    }

    
    
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		setupSimplePreferencesScreen();
	}

	/**
	 * Shows the simplified settings UI if the device configuration if the
	 * device configuration dictates that a simplified, single-pane UI should be
	 * shown.
	 */
	@SuppressWarnings("deprecation")
	private void setupSimplePreferencesScreen() {
		if (!isSimplePreferences(this)) {
			return;
		}

		// In the simplified UI, fragments are not used at all and we instead
		// use the older PreferenceActivity APIs.

		
		// Add 'training-timer' preferences, and a corresponding header.
		addPreferencesFromResource(R.xml.pref_trainingtimer);
		
		
		// Bind the summaries of EditText preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("training_timer_exercise_recovery_time"));
		bindPreferenceSummaryToValue(findPreference("training_timer_set_recovery_time"));


		// Add 'licenses' preferences, and a corresponding header.
		PreferenceCategory fakeHeader = new PreferenceCategory(this);
		fakeHeader.setTitle(R.string.pref_header_licenses);
		getPreferenceScreen().addPreference(fakeHeader);
		addPreferencesFromResource(R.xml.pref_licenses);
		
		// add dialog for showing license info
		Preference open_source = this.findPreference("open_source");
		open_source.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				new LicenseDialog(SettingsActivity.this);
				return false;
			}
		});
		
		
		// Add 'exercise download' preferences
		PreferenceCategory fakeHeaderExerciseDownload = new PreferenceCategory(this);
		fakeHeaderExerciseDownload.setTitle(R.string.pref_header_sync_settings);
		getPreferenceScreen().addPreference(fakeHeaderExerciseDownload);
		addPreferencesFromResource(R.xml.pref_sync);

		
		// add sync action
		Preference start_sync = this.findPreference("start_sync");
		start_sync.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				startExerciseDownload();
				return false;
			}
		});
		
		
		// add dialog with information about syncing with wger
		Preference about_wger_sync = this.findPreference("about_wger_sync");
		about_wger_sync.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				showInfoAboutSyncing();					
				return false;
			}
		});
		
		// add delete-synced-exercises button
		Preference wipe_synced_exercises = this.findPreference("wipe_synced_exercises");
		wipe_synced_exercises.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				deleteSyncedExercises();					
				return false;
			}
		});
		
		
		// Add 'miscellaneous' preferences, and a corresponding header.
		PreferenceCategory changelogFakeHeader = new PreferenceCategory(this);
		changelogFakeHeader.setTitle(R.string.miscellaneous);
		getPreferenceScreen().addPreference(changelogFakeHeader);
		addPreferencesFromResource(R.xml.pref_miscellaneous);

		// add changelog button
		Preference changelog = this.findPreference("view_changelog");
		changelog.setOnPreferenceClickListener(new OnPreferenceClickListener(){
			@Override
			public boolean onPreferenceClick(Preference arg0) {
				//Launch change log dialog
				ChangeLogDialog  changelogDialog = new ChangeLogDialog(SettingsActivity.this);
				changelogDialog.show();  
				return false;
			}
		});
		

		
		// Bind the summaries of EditText preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("default_workout_name"));
		bindPreferenceSummaryToValue(findPreference("exercise_sync_url"));
	}
	
	/** 
	 *  Bugfix for newer Android Versions. Due to a security 
	 *  issue (http://securityintelligence.com/new-vulnerability-android-framework-fragment-injection#.U-jNndame5k)
	 *  this method HAS to be overwritten. Otherwise the app will crash (reproduced on Nexus 10 4.4.2 simulator). 
	 */
	@Override
	protected boolean isValidFragment(String fragmentName){
		List<String> validFragmentNames = new ArrayList<String>();
		
		validFragmentNames.add(LicensePreferenceFragment.class.getName());
		validFragmentNames.add(TrainingTimerPreferenceFragment.class.getName());
		validFragmentNames.add(SyncPreferenceFragment.class.getName());
		validFragmentNames.add(MiscellaneousPreferenceFragment.class.getName());

		if(validFragmentNames.contains(fragmentName))
	    	return true;
	  
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean onIsMultiPane() {
		return isXLargeTablet(this) && !isSimplePreferences(this);
	}

	/**
	 * Helper method to determine if the device has an extra-large screen. For
	 * example, 10" tablets are extra-large.
	 */
	private static boolean isXLargeTablet(Context context) {
		return (context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
	}

	/**
	 * Determines whether the simplified settings UI should be shown. This is
	 * true if this is forced via {@link #ALWAYS_SIMPLE_PREFS}, or the device
	 * doesn't have newer APIs like {@link PreferenceFragment}, or the device
	 * doesn't have an extra-large screen. In these cases, a single-pane
	 * "simplified" settings UI should be shown.
	 */
	private static boolean isSimplePreferences(Context context) {
		return ALWAYS_SIMPLE_PREFS || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB || !isXLargeTablet(context);
	}

	/** {@inheritDoc} */
	@Override
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public void onBuildHeaders(List<Header> target) {
		if (!isSimplePreferences(this)) {
			loadHeadersFromResource(R.xml.pref_headers, target);
		}
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			preference.setSummary(value.toString());
			return true;
		}
	};

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
				PreferenceManager.getDefaultSharedPreferences(preference.getContext()).getString(preference.getKey(), ""));
	}



	/**
	 * This fragment shows license preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class LicensePreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_licenses);
			
			// add dialog 
			Preference open_source = this.findPreference("open_source");
			open_source.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					new LicenseDialog(getActivity());
					return false;
				}
			});

		}
	}
	
	/**
	 * This fragment shows sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class SyncPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_sync);
			
			// add sync action 
			Preference start_sync = this.findPreference("start_sync");
			start_sync.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					((SettingsActivity) getActivity()).startExerciseDownload();
					return false;
				}
			});
			
			// add dialog with information about syncing with wger
			Preference about_wger_sync = this.findPreference("about_wger_sync");
			about_wger_sync.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					((SettingsActivity) getActivity()).showInfoAboutSyncing();					
					return false;
				}
			});
			
			// add delete-synced-exercises button
			Preference wipe_synced_exercises = this.findPreference("wipe_synced_exercises");
			wipe_synced_exercises.setOnPreferenceClickListener(new OnPreferenceClickListener(){
				@Override
				public boolean onPreferenceClick(Preference arg0) {
					((SettingsActivity) getActivity()).deleteSyncedExercises();					
					return false;
				}
			});
			
			bindPreferenceSummaryToValue(findPreference("exercise_sync_url"));


		}
	}
	
	
	/**
	 * This fragment shows sync preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class MiscellaneousPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_miscellaneous);
			
			
			// add changelog button
			Preference changelog = this.findPreference("view_changelog");
			changelog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
						@Override
						public boolean onPreferenceClick(Preference arg0) {
							// Launch change log dialog
							ChangeLogDialog changelogDialog = new ChangeLogDialog(
									getActivity());
							changelogDialog.show();
							return false;
						}
					});

		bindPreferenceSummaryToValue(findPreference("default_workout_name"));


		}
	}
	
	/**
	 * This fragment shows training-timer preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class TrainingTimerPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_trainingtimer);
			
			bindPreferenceSummaryToValue(findPreference("training_timer_exercise_recovery_time"));
			bindPreferenceSummaryToValue(findPreference("training_timer_set_recovery_time"));

		}
	}
	
	
	
	/**
	 * Shows a dialog with information about syncing with wger.
	 */
	private void showInfoAboutSyncing() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.about_wger_sync));
		builder.setMessage(getString(R.string.about_wger_sync_fulltext));
		builder.create().show();
	}

	// Handling exercise download comes here
	// will be moved to an own class somewhen

	

	@Override
    public void onPause() {
    	super.onPause();
        mReceiver.setReceiver(null); // clear receiver so no leaks.
    }

	@SuppressWarnings("unchecked")
	@Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
        case OpenTrainingSyncService.STATUS_RUNNING_DOWNLOAD_EXERCISES:
        	Log.v(TAG, "Sync status: STATUS_RUNNING_DOWNLOAD_EXERCISES");		
            break;
        case OpenTrainingSyncService.STATUS_RUNNING_DOWNLOAD_LANGUAGE_FILES:
        	Log.v(TAG, "Sync status: STATUS_RUNNING_DOWNLOAD_LANGUAGE_FILES");
    		mProgressDialog.setMessage(getString(R.string.downloading_language_files));
            break;      
        case OpenTrainingSyncService.STATUS_RUNNING_DOWNLOAD_MUSCLE_FILES:
        	Log.v(TAG, "Sync status: STATUS_RUNNING_DOWNLOAD_MUSCLE_FILES");
    		mProgressDialog.setMessage(getString(R.string.downloading_muscle_files));
            break;                  
        case OpenTrainingSyncService.STATUS_RUNNING_CHECKING_EXERCISES:
        	Log.v(TAG, "Sync status: STATUS_RUNNING_CHECKING_EXERCISES");
    		mProgressDialog.setMessage(getString(R.string.verifying_exercises));
            break;
        case OpenTrainingSyncService.STATUS_RUNNING_DOWNLOADING_IMAGES:
        	Log.v(TAG, "Sync status: STATUS_RUNNING_DOWNLOADING_IMAGES");
    		mProgressDialog.setMessage(getString(R.string.downloading_images));
            break;    
        case OpenTrainingSyncService.STATUS_FINISHED:
        	Log.v(TAG, "Sync status: STATUS_FINISHED");
            
            ArrayList<ExerciseType> allExercises = (ArrayList<ExerciseType>) resultData.getSerializable("all_exercises");
            
            // make sure to dismiss progress dialog
			mProgressDialog.dismiss();
			
        	if(allExercises.size() <= 0){
        		// show notification: no new exercises
            	AlertDialog.Builder finishedDialogBuilder = new AlertDialog.Builder(this);
            	finishedDialogBuilder.setTitle(getString(R.string.sync_finished));
            	finishedDialogBuilder.setMessage(getString(R.string.sync_finished_no_new_exercises));
            	finishedDialogBuilder.create().show();
        	}else{
        		// let user choose which exercises should be saved
    			AlertDialog.Builder dialogBuilder = new SyncFinishedDialog(this, allExercises);
    			dialogBuilder.create().show();
    	    }

        		
            break;
        case OpenTrainingSyncService.STATUS_ERROR:
        	Log.v(TAG, "Sync status: STATUS_ERROR");

            // make sure to dismiss progress dialog
			mProgressDialog.dismiss();
			// show error dialog
			final String errorMsg = resultData.getString(Intent.EXTRA_TEXT);
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setTitle(getString(R.string.sync_error));
			alertDialogBuilder.setMessage(getString(R.string.sync_error_msg));
			alertDialogBuilder.setNeutralButton(
					getString(R.string.show_more_information_about_error),
					new OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							(new AlertDialog.Builder(SettingsActivity.this))
									.setTitle(
											SettingsActivity.this
													.getString(R.string.sync_error))
									.setMessage(errorMsg).create().show();
						}
					});
			alertDialogBuilder.create().show();
			break;
        }     
    }
	
	

	public void startExerciseDownload() {
		Log.d(TAG, "startExerciseDownload()");

		// check for Internet connection
		if (!isOnline()) {
			// show toast an cancel syncing
			Toast.makeText(this, getString(R.string.no_internet_connection),
					Toast.LENGTH_LONG).show();
			return;
		}

		// get version from context
		int version = -1;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.wtf(TAG, "Could not get VersionCode.", e);
		}

		// get host from preferences
		SharedPreferences settings = PreferenceManager
				.getDefaultSharedPreferences(this);
		if (!settings.contains("exercise_sync_url"))
			Log.e(TAG, "Could not find preference 'string exercise_sync_url'");
		String host = settings.getString(
				"exercise_sync_url",
				getApplicationContext().getString(
						R.string.pref_default_exercise_sync_url));

		// declare the dialog as a member field of your activity

		// instantiate it within the onCreate method
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setTitle(getString(R.string.sync_in_progess));
		mProgressDialog.setMessage(getString(R.string.downloading_exercises));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				// stop sync service on cancel
				stopService(new Intent(SettingsActivity.this,
						OpenTrainingSyncService.class));
				Toast.makeText(
						SettingsActivity.this,
						SettingsActivity.this.getString(R.string.sync_canceled),
						Toast.LENGTH_LONG).show();
			}
		});
		mProgressDialog.show();

		final Intent intent = new Intent(Intent.ACTION_SYNC, null, this,
				OpenTrainingSyncService.class);
		intent.putExtra("receiver", mReceiver);
		intent.putExtra("command", "query");
		intent.putExtra("host", host);
		intent.putExtra("version", version);

		startService(intent);
	}
	
	/**
	 * Deletes all files in {@link IDataProvider#SYNCED_EXERCISE_FOLDER}.
	 */
	private void deleteSyncedExercises() {
		File syncedExerciseFolder = new File(getFilesDir().toString() + "/"
				+ IDataProvider.SYNCED_EXERCISE_FOLDER);
		File syncedImagesFolder = new File(getFilesDir().toString() + "/"
				+ IDataProvider.SYNCED_IMAGES_FOLDER);
		
		// delete exercises
		File[] exerciseFiles = syncedExerciseFolder.listFiles();
		if (exerciseFiles != null) {
			for (File f : exerciseFiles) {
				f.delete();
			}
		}
		// delete images
		File[] imageFiles = syncedImagesFolder.listFiles();
		if (imageFiles != null) {
			for (File f : imageFiles) {
				f.delete();
			}
		}
		

		Cache.INSTANCE.updateExerciseCache(this);
		
		Toast.makeText(this, getString(R.string.synced_files_deleted_msg), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * Checks if phone is connected to a WIFI network (or 3G, ...).
	 * Does not check if there's a working Internet connection!
	 * 
	 * @return true if phone is online
	 */
	private boolean isOnline() {
	    ConnectivityManager cm =
	        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo netInfo = cm.getActiveNetworkInfo();
	    if (netInfo != null && netInfo.isConnectedOrConnecting()) {
	        return true;
	    }
	    return false;
	}


}
