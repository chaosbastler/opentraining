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

import java.util.*;

import de.skubware.opentraining.activity.CreateExerciseActivity;
import de.skubware.opentraining.activity.EditWorkoutActivity;
import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import android.widget.Toast;

/**
 * Fragment that shows the list of the ExerciseType. Has an own options menu.
 * Some code may be auto-generated from eclipse template for fragments.
 * 
 */
public class ExerciseListFragment extends ListFragment {
	/** Tag for logging */
	private static final String TAG = "ExerciseDetailFragment";

	/** Map to store, which muscles should be shown */
	Map<Muscle, Boolean> muscleMap = new HashMap<Muscle, Boolean>();

	/** */
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
		// fill muscle map
		for (Muscle m : Muscle.values()) {
			muscleMap.put(m, true);
		}

		setListAdapter(new ArrayAdapter<ExerciseType>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, exList));
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
					startActivity(new Intent(ExerciseListFragment.this.getActivity(), EditWorkoutActivity.class));
					getActivity().finish();
				}

				return true;
			}

		});

		// configure menu_item_create_new_exercise
		final MenuItem menu_item_create_new_exercise = (MenuItem) menu.findItem(R.id.menu_item_create_new_exercise);
		menu_item_create_new_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(ExerciseListFragment.this.getActivity(), CreateExerciseActivity.class));
				return true;
			}
		});

		// configure menu_item_select_muscles
		MenuItem menu_item_select_muscles = (MenuItem) menu.findItem(R.id.menu_item_select_muscles);
		menu_item_select_muscles.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem menuitem) {
				final CharSequence[] items = new CharSequence[Muscle.values().length];
				final boolean[] states = new boolean[Muscle.values().length];
				int i = 0;
				for (Muscle m : Muscle.values()) {
					items[i] = m.toString();
					states[i] = muscleMap.get(m);
					i++;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseListFragment.this.getActivity());
				builder.setTitle(getString(R.string.select_muscles));
				builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialogInterface, int item, boolean state) {
						muscleMap.put(Muscle.getByName(items[item].toString()), state);

						if (!muscleMap.values().contains(Boolean.TRUE)) {
							Toast.makeText(ExerciseListFragment.this.getActivity(), getString(R.string.please_select_muscle), Toast.LENGTH_LONG).show();
						}
					}
				});
				builder.setNeutralButton(getString(R.string.select_all), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						int i = 0;
						for (Muscle m : Muscle.values()) {
							items[i] = m.toString();
							states[i] = true;
							muscleMap.put(m, true);
							i++;
						}
						updateExList();
						dialog.dismiss();
					}
				});
				builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!muscleMap.values().contains(Boolean.TRUE)) {
							muscleMap.put(Muscle.values()[0], true);
							Toast.makeText(ExerciseListFragment.this.getActivity(), Muscle.values()[0].toString() + " " + getString(R.string.was_choosen), Toast.LENGTH_LONG)
									.show();

						}
						updateExList();
					}
				});
				builder.setIcon(R.drawable.icon_attention);
				builder.create().show();

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
		
		ArrayList<ExerciseType> exList = new ArrayList<ExerciseType>();
		for (ExerciseType exType : ExerciseType.listExerciseTypes()) {
			boolean shouldAdd = false;
			for (Muscle m : exType.getActivatedMuscles()) {
				if (muscleMap.get(m)) {
					shouldAdd = true;
					break;
				}
			}
			if (shouldAdd || exType.getActivatedMuscles().isEmpty())
				exList.add(exType);
		}
		//TODO filter sports equipment

		ListAdapter adapter = new ArrayAdapter<ExerciseType>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, exList);
		this.setListAdapter(adapter);
	}
}
