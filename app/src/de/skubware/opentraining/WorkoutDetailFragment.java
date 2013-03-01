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

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * A fragment representing a single Workout detail screen. This fragment is
 * either contained in a {@link WorkoutListActivity} in two-pane mode (on
 * tablets) or a {@link WorkoutDetailActivity} on handsets.
 */
public class WorkoutDetailFragment extends SherlockFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_WORKOUT = "workout";

	/**
	 * The {@link Workout} this fragment is presenting.
	 */
	private Workout mWorkout;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public WorkoutDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);

		if (getArguments().containsKey(ARG_WORKOUT)) {
			mWorkout = (Workout) getArguments().getSerializable(ARG_WORKOUT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_workout_detail, container, false);

		// Show the dummy content as text in a TextView.
		if (mWorkout != null) {
			((TextView) rootView.findViewById(R.id.textview_workout_name)).setText(mWorkout.getName());
			
			ListView listview_exercises = (ListView) rootView.findViewById(R.id.listview_exercises);
			FitnessExercise[] arr = mWorkout.getFitnessExercises().toArray(new FitnessExercise[ mWorkout.getFitnessExercises().size()]);
			ArrayAdapter<FitnessExercise> adapter = new ArrayAdapter<FitnessExercise>(getActivity(), android.R.layout.simple_list_item_2,
					android.R.id.text1, arr);

			listview_exercises.setAdapter(adapter);
		}

		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workout_detail_menu, menu);

		// configure menu_item_rename_workout
		MenuItem menu_item_rename_workout = (MenuItem) menu.findItem(R.id.menu_item_rename_workout);
		menu_item_rename_workout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				throw new IllegalStateException("Not implemented yet.");
				//return true;
			}
		});
		
		// configure menu_item_delete_workout
		MenuItem menu_item_delete_workout = (MenuItem) menu.findItem(R.id.menu_item_delete_workout);
		menu_item_rename_workout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				throw new IllegalStateException("Not implemented yet.");
				//return true;
			}
		});
	}
}
