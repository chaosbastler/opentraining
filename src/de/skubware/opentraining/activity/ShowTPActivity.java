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

package de.skubware.opentraining.activity;

import java.io.File;

import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.opentraining.exporter.HTMLExporter;
import de.skubware.opentraining.exporter.WorkoutExporter;

import de.skubware.training_app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.Toast;
import android.webkit.WebView;

/**
 * Shows the generated training plan. To load some Fonts an internet connection
 * may be required.
 * 
 * @author Christian Skubich
 * 
 */
public class ShowTPActivity extends Activity {
	
	private File exportedFile;

	/**
	 * Configures the menu actions.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_tp_activity_menu, menu);
		

		// configure export
		final MenuItem menu_item_send_plan = (MenuItem) menu.findItem(R.id.menu_item_send_plan);
		menu_item_send_plan.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				
				//TODO E-Mail export does not work yet
				Toast.makeText(ShowTPActivity.this, "Workout erstellt: " + exportedFile.toString(), Toast.LENGTH_LONG).show();

				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/plain");
				intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(exportedFile.toString()));
				startActivity(Intent.createChooser(intent, getString(R.string.send_file) + " ..."));
				return true;
			}
		});
		return true;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_tp);
		WebView webview = (WebView) findViewById(R.id.tpWebView);

		// warn user if there is no internet connection
		if (!isOnline()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(ShowTPActivity.this);
			builder.setMessage( getString(R.string.no_internet_connection_plan_not_shown)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					dialog.cancel();
				}
			});
			AlertDialog alert = builder.create();
			alert.show();
		}

		webview.getSettings().setBuiltInZoomControls(true);
		
		// TODO instead of 5 enter real columncount
		final WorkoutExporter exporter = new HTMLExporter(5, ShowTPActivity.this, webview, DataManager.INSTANCE.getCurrentWorkout());


		exportedFile = exporter.exportWorkoutToFile(DataManager.INSTANCE.getCurrentWorkout());

	}

	/**
	 * Checks the network status.
	 * 
	 * @return True, if internet connection is available, false otherwise.
	 */
	public boolean isOnline() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}

		return false;
	}

}
