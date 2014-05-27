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

import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * An ArrayAdapter used in {@link ExerciseTypeListFragment}. Had to use a custom
 * adapter to be able to disable certain items of the ListView (exercises that
 * already have been added to the Workout should be disabled, so that the user
 * can see which exercises have been selected).
 * 
 */
public class ExerciseTypeListAdapter extends ArrayAdapter<ExerciseType>{
	private Workout mWorkout;
	private List<ExerciseType> mExercisesInWorkoutList = new ArrayList<ExerciseType>();
	
	public ExerciseTypeListAdapter(Activity context, int resource, int textViewResourceId,
			List<ExerciseType> exerciseList) {
		super(context, resource, textViewResourceId, exerciseList);
		updateWorkout();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		// the view has to be disabled too
		// just disabling the item is not enough
		v.setEnabled(isEnabled(position));
		return v;
	}

	@Override
	public boolean areAllItemsEnabled() {
		// this is a workaround; otherwise
		// Android will not draw the divider lines for disabled items
		return true;
	}

	
	
	@Override
	public boolean isEnabled(int position) {
		// return false, if already added to workout
		if(mExercisesInWorkoutList.contains(getItem(position)))
			return false;
		return true;	
	}

	
	public void updateWorkout() {
		mWorkout = ((ExerciseTypeListActivity) getContext()).getWorkout();
		
		mExercisesInWorkoutList = new ArrayList<ExerciseType>();
		
		if(mWorkout != null){
			for(FitnessExercise fEx: mWorkout.getFitnessExercises()){
				mExercisesInWorkoutList.add(fEx.getExType());
			}
		}
	}
	
	@Override
	public void notifyDataSetChanged(){
		updateWorkout();
		super.notifyDataSetChanged();
	}
	

}
