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

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A list fragment representing a list of Workouts. This fragment also supports
 * tablet devices by allowing list items to be given an 'activated' state upon
 * selection. This helps indicate which item is currently being viewed in a
 * {@link WorkoutDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class WorkoutListFragment extends ListFragment {

	/** Tag for logging */
	public static final String TAG = "WorkoutListFragment";

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;

	/** Currently displayed {@link Workout}s */
	private List<Workout> mWorkoutList;

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when a {@link Workout} has been selected.
		 */
		public void onItemSelected(Workout workout);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(Workout id) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public WorkoutListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		IDataProvider dataProvider = new DataProvider(getActivity());
		mWorkoutList = new ArrayList<Workout>(dataProvider.getWorkouts());

		setListAdapter(new ArrayAdapter<Workout>(getActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1,
				mWorkoutList));

		// notify user if there are no workouts
		if (mWorkoutList.isEmpty()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			builder.setMessage(getString(R.string.no_workout));
			builder.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					getActivity().finish();
				}
			});
			builder.create().show();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
		
		IDataProvider dataProvider = new DataProvider(getActivity());
		mWorkoutList = new ArrayList<Workout>(dataProvider.getWorkouts());

		setListAdapter(new ArrayAdapter<Workout>(getActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1,
				mWorkoutList));
	}
	
	/** @see WorkoutListActivity#onWorkoutChanged(Workout) */
	@SuppressWarnings("unchecked")
	public void onWorkoutChanged(Workout changedWorkout) {
		Workout oldWorkout = null;

		// try to find an old Workout with the same name(will fail, if the name
		// was changed)
		for (Workout w : mWorkoutList) {
			if (w.getName().equals(changedWorkout.getName())) {
				oldWorkout = w;
				break;
			}
		}

		if (oldWorkout == null) {
			// if only the name was changed, the exercises still have to be the
			// same
			for (Workout w : mWorkoutList) {
				if (w.getFitnessExercises().equals(changedWorkout.getFitnessExercises())) {
					oldWorkout = w;
					break;
				}
			}

		}

		if (oldWorkout == null) {
			// no changed workout, but new one
			mWorkoutList.add(changedWorkout);
			return;
		}

		Log.d(TAG, "Workout has changed. Old Workout: " + oldWorkout.toDebugString() + "\n New Workout: " + changedWorkout.toDebugString());

		// replace Workout with changed one
		int oldIdx = mWorkoutList.indexOf(oldWorkout);
		mWorkoutList.remove(oldWorkout);
		mWorkoutList.add(oldIdx, changedWorkout);

		// finally update GUI
		((ArrayAdapter<Workout>) this.getListAdapter()).notifyDataSetChanged();

	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized activated item position.
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		mCallbacks.onItemSelected(((Workout) listView.getAdapter().getItem(position)));
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}
	
	
}
