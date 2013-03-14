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

package de.skubware.opentraining.activity.manage_workouts;

import java.io.File;
import java.net.MalformedURLException;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.ShareActionProvider;

import de.skubware.opentraining.R;
import de.skubware.opentraining.R.id;
import de.skubware.opentraining.R.layout;
import de.skubware.opentraining.R.menu;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.exporter.HTMLExporter;
import de.skubware.opentraining.exporter.WorkoutExporter;

import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import android.content.Intent;

/**
 * Activity that displays the exported {@link Workout} HTML in a {@link WebView}
 */
public class ShowWorkoutHTMLActivity extends SherlockActivity {
	/** Tag for logging */
	public static final String TAG = "ShowWorkoutHTMLActivity";

	/** ID for argument */
	public static final String ARG_WORKOUT = "workout";

	/** Currently displayed {@link Workout} */
	private Workout mWorkout;

	/** The exported file. */
	private File mExportedFile;
	
	/** String representation of the exported {@link Workout} */
	private String mExportedString;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_workout_html);

		// get workout from arguments
		mWorkout = (Workout) getIntent().getExtras().getSerializable(ARG_WORKOUT);
		
		// generate HTML
		WorkoutExporter mExporter = new HTMLExporter(this);
		mExportedFile = mExporter.exportWorkoutToFile(mWorkout);
		mExportedString = mExporter.exportWorkoutToString(mWorkout);

		// exit if export failed
		if (mExportedFile == null) {
			Toast.makeText(this, "Export failed", Toast.LENGTH_LONG).show();
			finish();
		}

		// now show html
		WebView mWebView = (WebView) this.findViewById(R.id.webView);
		try {
			mWebView.loadUrl(mExportedFile.toURI().toURL().toString());
		} catch (MalformedURLException e) {
			Log.e(TAG, "Loading plan to WebView failed.", e);
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.show_workout_html, menu);

		// Set file with share history to the provider and set the share intent
		MenuItem actionItem = menu.findItem(R.id.share);
		ShareActionProvider actionProvider = (ShareActionProvider) actionItem.getActionProvider();
		actionProvider.setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);

		actionProvider.setShareIntent(createShareIntent());

		return true;
	}

	/**
	 * Creates a sharing {@link Intent}.
	 * 
	 * @return The sharing intent.
	 */
	private Intent createShareIntent() {

		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		shareIntent.setType("text/plain");

		shareIntent.putExtra(Intent.EXTRA_TEXT, mExportedString);

		return shareIntent;
	}

}
