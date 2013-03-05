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

package de.skubware.opentraining;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.MenuItem;

import de.skubware.opentraining.activity.create_workout.ExerciseTypeListActivity;
import de.skubware.opentraining.basic.Workout;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * An activity representing a list of Workouts. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link WorkoutDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link WorkoutListFragment} and the item details (if present) is a
 * {@link WorkoutDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link WorkoutListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class WorkoutListActivity extends SherlockFragmentActivity implements WorkoutListFragment.Callbacks {	
	/** Tag for logging */
	public static final String TAG = "WorkoutListActivity";
	

	/** Constant for result */
	static final int RESULT_WORKOUT = 404;

	/** Constant for argument */
	public static String ARG_WORKOUT = "workout";

	
	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_workout_list);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (findViewById(R.id.workout_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((WorkoutListFragment) getSupportFragmentManager().findFragmentById(R.id.workout_list)).setActivateOnItemClick(true);
		}

		// TODO: If exposing deep links into your app, handle intents here.
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * Callback method from {@link WorkoutListFragment.Callbacks} indicating
	 * that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(Workout workout) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable(WorkoutDetailFragment.ARG_WORKOUT, workout);
			WorkoutDetailFragment fragment = new WorkoutDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.workout_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, WorkoutDetailActivity.class);
			detailIntent.putExtra(WorkoutDetailFragment.ARG_WORKOUT, workout);
			startActivityForResult(detailIntent, RESULT_WORKOUT);
		}
	}
	

	/**
	 * Handles changed {@link Workout}s(e.g. name was changed in {@link WorkoutDetailActivity}).
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult()");
		if (requestCode == RESULT_WORKOUT) {
			if (resultCode == RESULT_OK) {
				Workout mWorkout = (Workout) data.getSerializableExtra(WorkoutListActivity.ARG_WORKOUT);
				this.onWorkoutChanged(mWorkout);
			}
		}
	}
	
	/**
	 * Called when a {@link Workout} (that is in the list of currently shown
	 * Workouts) has changed. This will update the ListAdapter and thus the GUI.
	 * 
	 * @param changedWorkout
	 *            The Workout that has changed.
	 */
	public void onWorkoutChanged(Workout changedWorkout){
		((WorkoutListFragment) getSupportFragmentManager().findFragmentById(R.id.workout_list)).onWorkoutChanged(changedWorkout);
	}
}

