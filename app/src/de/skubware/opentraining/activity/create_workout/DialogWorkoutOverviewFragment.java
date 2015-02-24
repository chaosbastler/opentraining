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

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Dialog Fragment that shows dialog when a {@link Workout} should be saved.
 * 
 * 
 */
public class DialogWorkoutOverviewFragment extends DialogFragment {
	/** Tag for logging */
	public static final String TAG = "DialogWorkoutOverviewFragment";
	
	/** Argument ID */
	private static String ARG_ID_WORKOUT = "workout";
	
	
	/** Currently displayed {@link Workout}. */
	Workout mWorkout;

	/** EditText for the name of the {@link Workout}*/
	private EditText mEditTextWorkoutName;

	/** ListView with the {@link FitnessExercise}s */
	private ListView mListView;

	/**
	 * Create a new instance of DialogWorkoutOverviewFragment.
	 */
	static DialogWorkoutOverviewFragment newInstance(Workout workout) {		
		DialogWorkoutOverviewFragment f = new DialogWorkoutOverviewFragment();

		Bundle args = new Bundle();
		args.putSerializable(ARG_ID_WORKOUT, workout);
		f.setArguments(args);
		
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWorkout = (Workout) getArguments().getSerializable(ARG_ID_WORKOUT);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.fragment_dialog_workout_overview, null);
		
		mEditTextWorkoutName = (EditText) v.findViewById(R.id.edittext_workout_name);
		mEditTextWorkoutName.setText(mWorkout.getName());


		mListView = (ListView) v.findViewById(R.id.listview);
		mListView.setAdapter(new ArrayAdapter<FitnessExercise>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1,
				mWorkout.getFitnessExercises()));
		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				final FitnessExercise fEx = (FitnessExercise) parent.getAdapter().getItem(position);

				new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.are_you_sure))
						.setMessage(getString(R.string.really_remove_exercise).replace("EXERCISE_NAME", fEx.toString()))
						.setPositiveButton(getString(android.R.string.ok), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								removeExerciseFromWorkout(fEx);
								dialog.dismiss();
							}

						}).setNegativeButton(getString(R.string.cancel), new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						}).create().show();
			}
		});


		
		return new AlertDialog.Builder(getActivity()).setTitle(mWorkout.getName()).setView(v).setCancelable(true)
                .setPositiveButton(getString(R.string.save_workout), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // check if name is empty
                        final String workoutName = mEditTextWorkoutName.getText().toString();
                        if (workoutName.equals("") || workoutName.replaceAll(" ", "").equals("")){
                            Toast.makeText(getActivity(), getString(R.string.workout_name_cannot_be_empty), Toast.LENGTH_LONG).show();
                            return;
                        }

                        // check if file already exists
                        if (fileAlreadyExists(workoutName)) {
                            showOverrideDialog();
                            return;
                        }


                        saveWorkout(getActivity());
                    }
                }).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishActivities(getActivity());
                    }
                }).create();
    }
	

	
	/**
	 * Removes the exercise from the Workout and updates GUI & activities.
	 */
	@SuppressWarnings("rawtypes")
	private void removeExerciseFromWorkout(FitnessExercise fEx) {
		mWorkout.removeFitnessExercise(fEx);
		((ArrayAdapter) mListView.getAdapter()).notifyDataSetChanged();
		((ExerciseTypeListActivity) getActivity()).onWorkoutChanged(mWorkout);

	}
	
	


	private void saveWorkout(Activity activity){
		if(mWorkout.getFitnessExercises().isEmpty()){
			Log.w(TAG, "User tried to save an empty Workout. Will skip saving.");
			finishActivities(activity);
			return;
		}
		
		final String workoutName = mEditTextWorkoutName.getText().toString();
		
		mWorkout.setName(workoutName);

		// save Workout before exiting
		IDataProvider dataProvider = new DataProvider(activity);
		dataProvider.saveWorkout(mWorkout);

		finishActivities(activity);
	}
	
		
	/**
	 * On small screens(only details are shown) the detail activity will be
	 * closed.
	 */
	private void continueAddingExercises() {
		if (getActivity() instanceof ExerciseTypeDetailActivity) {
			// close activity that contained the fragment with
			// details
			// to return the result to the list activity
			getActivity().finish();
		}
	}
	
	
	/**
	 * Finishes the Activities ExerciseTypeDetailActivity and
	 * ExerciseTypeListActivity .
	 */
	private void finishActivities(Activity activity) {
		if (activity instanceof ExerciseTypeDetailActivity) {
			// finish ExerciseTypeDetailActivity AND
			// ExerciseTypeListActivity
			activity.finishActivityFromChild(getActivity(), ExerciseTypeListActivity.RESULT_WORKOUT);
			activity.finish();
		} else {
			// finish ExerciseTypeListActivity
			activity.finish();
		}
	}


	/**
	 * Shows a Dialog that asks the user if he really wants to override the
	 * existing {@link Workout}.
	 */
	private void showOverrideDialog(){
		final Activity activity = getActivity();
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(R.string.workout_already_exists);
		builder.setPositiveButton(R.string.override, new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				saveWorkout(activity);
			}
		});
		builder.setNegativeButton(R.string.cancel, new OnClickListener(){
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				continueAddingExercises();
			}
		});
		builder.show();
	}

	
	/**
	 * Checks if a {@link Workout} file with the same name already exists.
	 * 
	 * @param name
	 *            The name to check
	 * 
	 * @return true if there is already such a file
	 */
	private boolean fileAlreadyExists(String name) {
		// list files in workout directory that end with ".xml"
		String files[] = getActivity().getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".xml"))
					return true;
				else
					return false;
			}
		});
		Set<String> workout_names = new HashSet<String>();
		for (String s : files) {
			workout_names.add(s.split(".xml")[0]);
		}

		return workout_names.contains(name);
	}



}
