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

package de.skubware.opentraining.activity;

import java.util.Calendar;
import java.util.List;

import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.start_training.FExListActivity;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;
import android.widget.Button;

/**
 * Dialog Fragment for choosing a {@link Workout} before starting training.
 * 
 */
public class SelectWorkoutDialogFragment extends DialogFragment {
	/** Tag for logging */
	public static final String TAG = "SelectWorkoutFragment";
	
	/** Currently selected Workout */
	private Workout mWorkout;
	
	private AlertDialog mCreatedDialog;

	/**
	 * Create a new instance of SelectWorkoutFragment.
	 */
	static SelectWorkoutDialogFragment newInstance() {
		SelectWorkoutDialogFragment f = new SelectWorkoutDialogFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		// get Workouts
		IDataProvider dataProvider = new DataProvider(getActivity());
		final List<Workout> workoutList = dataProvider.getWorkouts();

		AlertDialog.Builder builder_workoutchooser = new AlertDialog.Builder(getActivity());
		builder_workoutchooser.setTitle(getString(R.string.choose_workout));

		final ArrayAdapter<Workout> adapter = new ArrayAdapter<Workout>(getActivity(), android.R.layout.select_dialog_singlechoice,
				workoutList);


		mCreatedDialog =  builder_workoutchooser.setSingleChoiceItems(adapter, 0, new OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				mWorkout = adapter.getItem(which);

				enableButton();
				// disable button for loading old training if there is at least one exercise that has no history
				// this can happen when:
				// - there has never been any training before
				// - an exercise has been added (workout has been edited)
				for(FitnessExercise fEx:mWorkout.getFitnessExercises()){
					if(fEx.getTrainingEntryList().isEmpty()) {
						disableButton();
						break;
					}	
				}

				
			}
			
		}).setPositiveButton(getString(R.string.start_new_training), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startTraining(true);
			}
		}).setNegativeButton(getString(R.string.load_old_training), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				startTraining(false);
			}
		}).create();
		

		return mCreatedDialog;

	}
	
	@Override
	public void onStart(){
		super.onStart();
		
		// disable button if necessary
		IDataProvider dataProvider = new DataProvider(getActivity());
		final List<Workout> workoutList = dataProvider.getWorkouts();
		
		mWorkout = workoutList.get(0);
		if (mWorkout.getFitnessExercises().get(0).getTrainingEntryList().isEmpty()) {
			disableButton();
		}
		
	}

	private void startTraining(boolean startNewTraining) {
		this.dismiss();
		
		// add TrainingEntry(==start new training) if user choose this
		// or it is necessary because there are no old training entries
		if (startNewTraining || mWorkout.getFitnessExercises().get(0).getTrainingEntryList().isEmpty()) {
			mWorkout.addTrainingEntry(Calendar.getInstance().getTime());
			
			// add the FSets the user probably wants to do
			// but set them to notDone
			for(FitnessExercise fEx:mWorkout.getFitnessExercises()){
				TrainingEntry latestEntry = fEx.getTrainingEntryList().get(fEx.getTrainingEntryList().size() -1);
				if(fEx.getFSetList() != null && !fEx.getFSetList().isEmpty()){
					for(FSet set: fEx.getFSetList()){
						FSet newSet = (FSet) set.clone();
						latestEntry.add(newSet);
						latestEntry.setHasBeenDone(newSet, false);
					}
				}else{
					if(fEx.getTrainingEntryList().size() != 1){
						TrainingEntry prevEntry = fEx.getTrainingEntryList().get(fEx.getTrainingEntryList().size() -2);
						for(FSet set:prevEntry.getFSetList()){
							FSet newSet = (FSet) set.clone();
							latestEntry.add(newSet);
							latestEntry.setHasBeenDone(newSet, false);
						}
					}
				}
			}
		}

		// add arguments to intent
		Intent intent = new Intent(getActivity(), FExListActivity.class);
		intent.putExtra(FExListActivity.ARG_WORKOUT, mWorkout);
		// start activity
		getActivity().startActivity(intent);
		
	}
	
	
	private void disableButton() {
		Button button = mCreatedDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		button.setEnabled(false);		
	}
	
	private void enableButton() {
		Button button = mCreatedDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		button.setEnabled(true);		
	}
}
