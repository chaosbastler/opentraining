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

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.datamanagement.*;
import de.skubware.training_app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.TextView;

/**
 * This activity is the starting point for the user. The user can start creating
 * a new plan or manage the database.
 * 
 * @author Christian Skubich
 * 
 */
public class HomeActivity extends Activity {

	/**
	 * Configures the menu actions.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.home_menu, menu);

		// configure menu_button_select_exercises
		final MenuItem menu_button_select_exercises = (MenuItem) menu.findItem(R.id.menu_button_select_exercises);
		menu_button_select_exercises.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (!ExerciseType.listExerciseTypes().isEmpty())
					startActivity(new Intent(HomeActivity.this, SelectExercisesActivity.class));
				else {
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
					builder.setMessage(getString(R.string.no_exercise_in_databbase));
					AlertDialog alert = builder.create();
					alert.show();
				}
				return true;
			}
		});

		// configure menu_button_manage_database
		final MenuItem menu_button_manage_database = (MenuItem) menu.findItem(R.id.menu_button_manage_database);
		menu_button_manage_database.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(HomeActivity.this, ManageDatabaseActivity.class));

				return true;
			}
		});

		// configure menu_item_load_plan
		MenuItem menu_item_load_plan = (MenuItem) menu.findItem(R.id.menu_item_load_plan);
		menu_item_load_plan.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				boolean success = DataManager.INSTANCE.loadPlan(HomeActivity.this);
				AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
				if (success) {
					builder.setMessage(getString(R.string.success));
					builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							startActivity(new Intent(HomeActivity.this, EditWorkoutActivity.class));
						}
					});
				} else {
					builder.setMessage(getString(R.string.no_success));
					builder.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					});
				}

				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});

		// configure menu_button_settings
		final MenuItem menu_button_settings = (MenuItem) menu.findItem(R.id.menu_button_settings);
		menu_button_settings.setOnMenuItemClickListener(this.getNotSupportedYetOnMenuItemClickListener());

		// configure menu_button_help
		final MenuItem menu_button_help = (MenuItem) menu.findItem(R.id.menu_button_help);
		menu_button_help.setOnMenuItemClickListener(this.getNotSupportedYetOnMenuItemClickListener());

		return true;
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		// load exercises
		DataManager.INSTANCE.loadExercises(this);
		
		// show disclaimer
		AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
		// Linkify the message
	    final SpannableString s = new SpannableString(getString(R.string.disclaimer) + "http://www.gnu.org/licenses/gpl-3.0.html");
	    Linkify.addLinks(s, Linkify.ALL);
		
		builder.setTitle(getString(R.string.license));
		builder.setMessage(s);
		builder.setPositiveButton(getString(R.string.accept), new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.setNegativeButton(getString(R.string.not_accept), new OnClickListener(){
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				finish();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
		// Make the textview clickable. Must be called after show()
	    ((TextView)alert.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
		
	}

	/**
	 * Creates a dialog that informs the user, that this action is not supported
	 * yet.
	 * 
	 * @return The generated dialog.
	 */
	private OnMenuItemClickListener getNotSupportedYetOnMenuItemClickListener() {

		return new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
				builder.setMessage(getString(R.string.action_not_supported)).setCancelable(true).setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).setNegativeButton("Ohhh :-(", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		};

	}

}