package de.skubware.opentraining.activity.start_training;

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


import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;

/**
 * An activity representing a list of Exercise. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link FExDetailActivity} representing item details. On tablets, the activity
 * presents the list of items and item details side-by-side using two vertical
 * panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link FExListFragment} and the item details (if present) is a
 * {@link FExDetailFragment}.
 * <p>
 * This activity also implements the required {@link FExListFragment.Callbacks}
 * interface to listen for item selections.
 */
public class FExListActivity extends ActionBarActivity implements FExListFragment.Callbacks {

	/** Tag for logging */
	public static final String TAG = FExListActivity.class.getName();

	/** Constants for argument */
	public static final String ARG_WORKOUT = "workout";

	static final int RESULT_WORKOUT = 23;

	private Workout mWorkout;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fex_list);
		
		
		if(savedInstanceState != null){
			mWorkout = (Workout) savedInstanceState.getSerializable(ARG_WORKOUT);
		}else{
			// retrieve argument and pass it to fragment
			mWorkout = (Workout) getIntent().getExtras().getSerializable(ARG_WORKOUT);
		}
		
		FExListFragment fragment = (FExListFragment) getSupportFragmentManager().findFragmentById(R.id.exercise_list);
		fragment.setWorkout(mWorkout);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (findViewById(R.id.exercise_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((FExListFragment) getSupportFragmentManager().findFragmentById(R.id.exercise_list)).setActivateOnItemClick(true);

		}

	}

	/** Saves the state of this Activity, e.g. when screen orientation changed. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ARG_WORKOUT, mWorkout);
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
	 * Callback method from {@link FExListFragment.Callbacks} indicating that
	 * the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(FitnessExercise ex) {
		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable(FExDetailFragment.ARG_FEX, ex);
			arguments.putSerializable(FExDetailFragment.ARG_WORKOUT, mWorkout);
			FExDetailFragment fragment = new FExDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.exercise_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, FExDetailActivity.class);
			detailIntent.putExtra(FExDetailFragment.ARG_FEX, ex);
			detailIntent.putExtra(FExDetailFragment.ARG_WORKOUT, mWorkout);
			startActivityForResult(detailIntent, FExListActivity.RESULT_WORKOUT);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult()");
		if (requestCode == RESULT_WORKOUT) {
			if (resultCode == RESULT_OK) {
				// Workout has been changed, so update data
				mWorkout = (Workout) data.getSerializableExtra(FExListActivity.ARG_WORKOUT);
				Log.v(TAG, "updating Workout of FExListActivity:\n" + mWorkout.toDebugString());

				// update in fragment too
				FExListFragment fragment = (FExListFragment) getSupportFragmentManager().findFragmentById(R.id.exercise_list);
				if (fragment != null) {
					Log.d(TAG, "updating FExListFragment");
					// either notify list fragment if it's there (on tablets)
					fragment.setWorkout(mWorkout);
				} else {
					Log.e(TAG, "Did not find FExListFragment");
				}
			}
		}
	}
}
