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

package de.skubware.opentraining.activity.create_workout;

import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_exercise.CreateExerciseActivity;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

/**
 * An activity representing a list of ExerciseTypes. This activity has different
 * presentations for handset and tablet-size devices. On handsets, the activity
 * presents a list of items, which when touched, lead to a
 * {@link ExerciseTypeDetailActivity} representing item details. On tablets, the
 * activity presents the list of items and item details side-by-side using two
 * vertical panes.
 * <p>
 * The activity makes heavy use of fragments. The list of items is a
 * {@link ExerciseTypeListFragment} and the item details (if present) is a
 * {@link ExerciseTypeDetailFragment}.
 * <p>
 * This activity also implements the required
 * {@link ExerciseTypeListFragment.Callbacks} interface to listen for item
 * selections.
 */
public class ExerciseTypeListActivity extends ActionBarActivity implements ExerciseTypeListFragment.Callbacks,
		ExerciseTypeDetailFragment.Callbacks {

	/** Tag for logging */
	public static final String TAG = ExerciseTypeListActivity.class.getName();

	/** Reference to the search view (for searching exercises) of this activity. */
	private SearchView mSearchView;

	/**
	 * Whether or not the activity is in two-pane mode, i.e. running on a tablet
	 * device.
	 */
	private boolean mTwoPane;

	// some constants for arguments
	static final int RESULT_WORKOUT = 23;
	public static final String ARG_WORKOUT = "workout";

	/** Current {@link Workout} */
	private Workout mWorkout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercisetype_list);
		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		Bundle args = getIntent().getExtras();
		if (args != null && args.containsKey(ARG_WORKOUT)) {
			mWorkout = (Workout) args.getSerializable(ARG_WORKOUT);
		}

		if (findViewById(R.id.exercisetype_detail_container) != null) {
			// The detail container view will be present only in the
			// large-screen layouts (res/values-large and
			// res/values-sw600dp). If this view is present, then the
			// activity should be in two-pane mode.
			mTwoPane = true;

			// In two-pane mode, list items should be given the
			// 'activated' state when touched.
			((ExerciseTypeListFragment) getSupportFragmentManager().findFragmentById(R.id.exercisetype_list)).setActivateOnItemClick(true);
		}

	}

	/**
	 * Restores the state of this Activity, e.g. after screen orientation
	 * changed.
	 */
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mWorkout = (Workout) savedInstanceState.getSerializable(ARG_WORKOUT);
	}

	/** Saves the state of this Activity, e.g. when screen orientation changed. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ARG_WORKOUT, mWorkout);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		SharedPreferences.Editor sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this).edit();
		
		switch(item.getItemId()){
			case android.R.id.home:
				if (mWorkout != null)
					showDialog();
				else
					finish();
				break;
			case R.id.menu_item_show_workout:
				showDialog();
				break;
			case R.id.menu_item_filter_settings:
				DialogFilterMusclesAndEquipment dialog = new DialogFilterMusclesAndEquipment(this);
				dialog.show();	
				break;
			case  R.id.menu_item_create_exercise:
				startActivity(new Intent(ExerciseTypeListActivity.this, CreateExerciseActivity.class));
				break;
			case R.id.menu_item_show_default_exercises:
				item.setChecked(!item.isChecked());
				sharedPrefs.putBoolean(ExerciseTypeListFragment.PREF_KEY_SHOW_DEFAULT_EXERCISES, item.isChecked());
				break;
			case R.id.menu_item_show_synced_exercises:
				item.setChecked(!item.isChecked());
				sharedPrefs.putBoolean(ExerciseTypeListFragment.PREF_KEY_SHOW_SYNCED_EXERCISES, item.isChecked());
				break;
			case R.id.menu_item_show_custom_exercises:
				item.setChecked(!item.isChecked());
				sharedPrefs.putBoolean(ExerciseTypeListFragment.PREF_KEY_SHOW_CUSTOM_EXERCISES, item.isChecked());
				break;
		}
		
		sharedPrefs.commit();

		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mWorkout != null)
				showDialog();
			else
				finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/** Shows DialogWorkoutOverviewFragment if mWorkout is not null. */
	void showDialog() {

		if (mWorkout == null) {
			Toast.makeText(this, getString(R.string.workout_empty), Toast.LENGTH_SHORT).show();
			return;
		}

		// DialogFragment.show() will take care of adding the fragment
		// in a transaction. We also want to remove any currently showing
		// dialog, so make our own transaction and take care of that here.
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		DialogFragment newFragment = DialogWorkoutOverviewFragment.newInstance(mWorkout);
		newFragment.show(ft, "dialog");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.exercise_list_menu, menu);

		MenuItem searchItem = menu.findItem(R.id.action_search);

		if (mSearchView == null)
			Log.e(TAG, "MenuItem searchItem is null.");
		mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
		if (mSearchView == null)
			Log.e(TAG, "SearchView is null");
		setupSearchView(mSearchView);

		// show (or don't show) the menu_items for
		// showing default/synced/custom exercises
		IDataProvider dataProvider = new DataProvider(this);
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean hasCustomExercises = false;
		boolean hasSyncedExercises = false;

		for (ExerciseType ex : dataProvider.getExercises()) {
			switch (ex.getExerciseSource()) {
			case CUSTOM:
				hasCustomExercises = true;
				break;
			case SYNCED:
				hasSyncedExercises = true;
				break;
			case DEFAULT:
				break;
			}
		}

		MenuItem menu_item_show_default_exercises = menu.findItem(R.id.menu_item_show_default_exercises);
		menu_item_show_default_exercises.setVisible(true); // default exercises
															// should always be
															// there
		menu_item_show_default_exercises.setChecked(sharedPrefs.getBoolean(ExerciseTypeListFragment.PREF_KEY_SHOW_DEFAULT_EXERCISES, true));

		MenuItem menu_item_show_synced_exercises = menu.findItem(R.id.menu_item_show_synced_exercises);
		menu_item_show_synced_exercises.setVisible(hasSyncedExercises);
		menu_item_show_synced_exercises.setChecked(sharedPrefs.getBoolean(ExerciseTypeListFragment.PREF_KEY_SHOW_SYNCED_EXERCISES, true));

		MenuItem menu_item_show_custom_exercises = menu.findItem(R.id.menu_item_show_custom_exercises);
		menu_item_show_custom_exercises.setVisible(hasCustomExercises);
		menu_item_show_custom_exercises.setChecked(sharedPrefs.getBoolean(ExerciseTypeListFragment.PREF_KEY_SHOW_CUSTOM_EXERCISES, true));

		return super.onCreateOptionsMenu(menu);
	}

	/**
	 * Callback method from {@link ExerciseTypeListFragment.Callbacks}
	 * indicating that the item with the given ID was selected.
	 */
	@Override
	public void onItemSelected(ExerciseType ex) {

		if (mTwoPane) {
			// In two-pane mode, show the detail view in this activity by
			// adding or replacing the detail fragment using a
			// fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable(ExerciseTypeDetailFragment.ARG_EXERCISE, ex);
			arguments.putSerializable(ExerciseTypeDetailFragment.ARG_WORKOUT, mWorkout);

			ExerciseTypeDetailFragment fragment = new ExerciseTypeDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().replace(R.id.exercisetype_detail_container, fragment).commit();

		} else {
			// In single-pane mode, simply start the detail activity
			// for the selected item ID.
			Intent detailIntent = new Intent(this, ExerciseTypeDetailActivity.class);
			detailIntent.putExtra(ExerciseTypeDetailFragment.ARG_EXERCISE, ex);
			detailIntent.putExtra(ExerciseTypeDetailFragment.ARG_WORKOUT, mWorkout);

			startActivityForResult(detailIntent, ExerciseTypeListActivity.RESULT_WORKOUT);
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.v(TAG, "onActivityResult()");
		if (requestCode == RESULT_WORKOUT) {
			if (resultCode == RESULT_OK) {
				// Workout has been changed, so update data
				mWorkout = (Workout) data.getSerializableExtra(ExerciseTypeListActivity.ARG_WORKOUT);
			}else if(resultCode == ExerciseTypeDetailFragment.RESULT_EXERCISE_CHANGED){
				// an exercise has been deleted, reload exercises
				ExerciseType deltedEx = (ExerciseType) data.getExtras().getSerializable(ExerciseTypeDetailFragment.ARG_DELETED_EXERCISE);

				this.onExerciseDeleted(deltedEx);
			}
		}
	}

	@Override
	public void onWorkoutChanged(Workout w) {
		mWorkout = w;
		// notify ExerciseTypeListAdapter
		((ExerciseTypeListAdapter) ((ExerciseTypeListFragment) getSupportFragmentManager().findFragmentById(R.id.exercisetype_list))
				.getListAdapter()).notifyDataSetChanged();
	}
	
	@Override
	public void onExerciseDeleted(ExerciseType deletedExercise){
		// notify ExerciseTypeListFragment to update the list with exercises
		((ExerciseTypeListFragment) getSupportFragmentManager().findFragmentById(R.id.exercisetype_list)).onExerciseDeleted(deletedExercise);
	}

	/**
	 * Getter for {@link #mWorkout}. Used by {@code ExerciseTypeListAdapter} to
	 * disable already selected exercises.
	 */
	protected Workout getWorkout() {
		return mWorkout;
	}

	private void setupSearchView(SearchView searchItem) {

		OnQueryTextListener listener = (ExerciseTypeListFragment) getSupportFragmentManager().findFragmentById(R.id.exercisetype_list);

		mSearchView.setIconified(true);
		mSearchView.setQuery("", false);
		mSearchView.setOnQueryTextListener(listener);
	}

	protected boolean isAlwaysExpanded() {
		return false;
	}

}
