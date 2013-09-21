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
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
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

import java.util.List;

import de.skubware.opentraining.R;

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

		// Add 'general' preferences.
		addPreferencesFromResource(R.xml.pref_general);

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
		
		// Bind the summaries of EditText preferences to
		// their values. When their values change, their summaries are updated
		// to reflect the new value, per the Android Design guidelines.
		bindPreferenceSummaryToValue(findPreference("default_workout_name"));
		bindPreferenceSummaryToValue(findPreference("exercise_sync_url"));

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
	 * This fragment shows general preferences only. It is used when the
	 * activity is showing a two-pane settings UI.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public static class GeneralPreferenceFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.pref_general);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their values. When their values change, their summaries are
			// updated to reflect the new value, per the Android Design
			// guidelines.
			bindPreferenceSummaryToValue(findPreference("default_workout_name"));
		}
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
			
			
			bindPreferenceSummaryToValue(findPreference("exercise_sync_url"));


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

	@Override
    public void onReceiveResult(int resultCode, Bundle resultData) {
        switch (resultCode) {
        case OpenTrainingSyncService.STATUS_RUNNING:
            Toast.makeText(this, "onReceiveResult: Running!", Toast.LENGTH_SHORT).show();
            //show progress
            break;
        case OpenTrainingSyncService.STATUS_FINISHED:
            String exercises = resultData.getString("exercises");
            Toast.makeText(this, "onReceiveResult: Finished!", Toast.LENGTH_LONG).show();
            // do something interesting
            // hide progress
            break;
        case OpenTrainingSyncService.STATUS_ERROR:
            Toast.makeText(this, "onReceiveResult: Error!", Toast.LENGTH_LONG).show();
            Log.e(TAG, resultData.getString(Intent.EXTRA_TEXT));
            // handle the error;
            break;
        }     
    }
	
	

	public void startExerciseDownload(){
		Log.d(TAG, "startExerciseDownload()");
		
		// get version from context
		int version = -1;
		try {
			version = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			Log.wtf(TAG, "Could not get VersionCode.", e);
		}

		// get host from preferences
		SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
		if(!settings.contains("exercise_sync_url"))
			Log.e(TAG, "Could not find preference 'string exercise_sync_url'");
		String host = settings.getString("exercise_sync_url", getApplicationContext().getString(R.string.pref_default_exercise_sync_url));
				
		
		
		
		
		
	    final Intent intent = new Intent(Intent.ACTION_SYNC, null, this, OpenTrainingSyncService.class);
	    intent.putExtra("receiver", mReceiver);
	    intent.putExtra("command", "query");
	    intent.putExtra("host", host);
	    intent.putExtra("version", version);
	    intent.putExtra("port", 80);
	    
	    startService(intent);

	}


}
