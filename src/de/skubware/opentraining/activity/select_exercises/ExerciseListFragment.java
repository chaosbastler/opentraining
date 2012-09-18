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

package de.skubware.opentraining.activity.select_exercises;

import java.util.*;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.CreateExerciseActivity;
import de.skubware.opentraining.activity.preferences.PreferencesActivity;
import de.skubware.opentraining.activity.preferences.PreferencesMusclesFragment;
import de.skubware.opentraining.activity.preferences.PreferencesNotImplementedFragment;
import de.skubware.opentraining.activity.show_workout.ShowWorkoutActivity;
import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

/**
 * Fragment that shows the list of the ExerciseType. Has an own options menu.
 * Some code may be auto-generated from eclipse template for fragments.
 * 
 */
public class ExerciseListFragment extends ListFragment {
	/** Tag for logging */
	private static final String TAG = "ExerciseDetailFragment";

	/** List with the ExerciseTypes that are shown at the moment */
	private List<ExerciseType> exList;

	// auto-generated stuff
	private static final String STATE_ACTIVATED_POSITION = "activated_position";
	private Callbacks mCallbacks = sDummyCallbacks;
	private int mActivatedPosition = ListView.INVALID_POSITION;

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// init exList
		this.exList = new ArrayList<ExerciseType>(ExerciseType.listExerciseTypes());

		this.setHasOptionsMenu(true);

		setListAdapter(new ArrayAdapter<ExerciseType>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, exList));
		this.updateExList();
	}

	// BEGIN auto-generated stuff

	public interface Callbacks {
		public void onItemSelected(String id);
	}

	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(String id) {
		}
	};

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.containsKey(STATE_ACTIVATED_POSITION)) {
			setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);
		mCallbacks.onItemSelected(exList.get(position).getName());
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			outState.putInt(STATE_ACTIVATED_POSITION, mActivatedPosition);
		}
	}

	public void setActivateOnItemClick(boolean activateOnItemClick) {
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	public void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}
		mActivatedPosition = position;
	}

	// END auto-generated stuff
	

	/**
	 * On resume the exercise list must be updated.
	 */
	@Override
	public void onResume(){
		super.onResume();
		Log.d(TAG, "Resuming.");
		this.updateExList();
		// Alternative solution:
		//SharedPreferences.OnSharedPreferenceChangeListener spChanged = new SharedPreferences.OnSharedPreferenceChangeListener() {}
	}

	

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_list_menu, menu);

		// configure menu_item_next
		final MenuItem menu_item_next = (MenuItem) menu.findItem(R.id.menu_item_next);
		menu_item_next.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (DataManager.INSTANCE.getCurrentWorkout() == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseListFragment.this.getActivity());
					builder.setMessage(getString(R.string.no_exercises_choosen)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					return true;
				} else {
					startActivity(new Intent(ExerciseListFragment.this.getActivity(), ShowWorkoutActivity.class));
					getActivity().finish();
				}

				return true;
			}

		});

		// configure menu_item_create_new_exercise
		final MenuItem menu_item_create_new_exercise = (MenuItem) menu.findItem(R.id.menu_item_create_new_exercise);
		menu_item_create_new_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				Intent intent = new Intent(getActivity(), PreferencesActivity.class);
				intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferencesNotImplementedFragment.class.getName());
				startActivity(intent);				
				return true;
			}
		});

		// configure menu_item_select_muscles
		MenuItem menu_item_select_muscles = (MenuItem) menu.findItem(R.id.menu_item_select_muscles);
		menu_item_select_muscles.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				Intent intent = new Intent(getActivity(), PreferencesActivity.class);
				intent.putExtra(PreferenceActivity.EXTRA_SHOW_FRAGMENT, PreferencesMusclesFragment.class.getName());
				startActivity(intent);
				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);

	}

	
	/**
	 * Updates the list of exercises.
	 * Only exercises that fit to the chosen {@link Muscle}s and {@link SportsEquipment} will be shown.
	 */
	private void updateExList() {
		Log.i(TAG, "Updating exercise list.");
		
		
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
        
        List<Muscle> wantedMuscles = new ArrayList<Muscle>();
		for(Muscle m:Muscle.values()){
			if(sharedPref.getBoolean(m.toString(), true))
				wantedMuscles.add(m);
		}
        
        List<SportsEquipment> usableEquipment = new ArrayList<SportsEquipment>();
		for(SportsEquipment eq:SportsEquipment.values()){
			if(sharedPref.getBoolean(eq.toString(), true))
				usableEquipment.add(eq);
		}


		
		
		this.exList = new ArrayList<ExerciseType>();
		for (ExerciseType exType : ExerciseType.listExerciseTypes()) {
			boolean shouldAdd = false;
			

			for (Muscle m : exType.getActivatedMuscles()) {
				if(wantedMuscles.contains(m)){
					shouldAdd = true;
					for(SportsEquipment eq:exType.getRequiredEquipment()){
						shouldAdd = shouldAdd && usableEquipment.contains(eq);
					}
				}
			}
			if(exType.getActivatedMuscles().isEmpty()){
				shouldAdd = true;
				for(SportsEquipment eq:exType.getRequiredEquipment()){
					shouldAdd = shouldAdd && usableEquipment.contains(eq);
				}
			}

			
			if(shouldAdd)
				exList.add(exType);
		}
		//TODO filter sports equipment

		ListAdapter adapter = new ArrayAdapter<ExerciseType>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, exList);
		this.setListAdapter(adapter);
	}
}
