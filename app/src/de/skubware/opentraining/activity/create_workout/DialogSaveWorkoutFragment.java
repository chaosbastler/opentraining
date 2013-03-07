/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Christian Skubich
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

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Dialog Fragment that shows the {@link FitnessExercise}s of the current
 * {@link Workout}.
 * 
 * @author Christian Skubich
 * 
 */
public class DialogSaveWorkoutFragment extends SherlockDialogFragment {

	/** Currently displayed {@link Workout}. */
	Workout mWorkout;

	private static String ARG_ID_WORKOUT = "workout";

	/**
	 * Create a new instance of DialogSaveWorkoutFragment.
	 */
	static DialogSaveWorkoutFragment newInstance(Workout workout) {
		DialogSaveWorkoutFragment f = new DialogSaveWorkoutFragment();

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
		final View v = inflater.inflate(R.layout.fragment_dialog_save_workout, null);

		// add suggestions to list adapter
		String[] arr = new String[8];
		arr[0] = getString(R.string.monday);
		arr[1] = getString(R.string.tuesday);
		arr[2] = getString(R.string.wednesday);
		arr[3] = getString(R.string.thursday);
		arr[4] = getString(R.string.friday);
		arr[5] = getString(R.string.saturday);
		arr[6] = getString(R.string.sunday);
		arr[7] = getString(R.string.workout);

		// check for names that already have been used

		for (int i = 0; i < arr.length; i++) {
			for (int k = 1;; k++) {
				if (!fileAlreadyExists(arr[i])) {
					break;
				}
				// only add number to the workout name if there is not such a
				// file yet, else increase counter
				if (fileAlreadyExists(arr[i]) && !fileAlreadyExists(arr[i] + " " + k)) {
					arr[i] = arr[i] + " " + k;
					break;
				}
			}

		}

		ListView listview = (ListView) v.findViewById(R.id.listview);
		final EditText edittext_workout_name = (EditText) v.findViewById(R.id.edittext_workout_name);
		listview.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, arr));
		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {
				String suggestion = (String) parent.getItemAtPosition(position);
				edittext_workout_name.setText(suggestion);
			}
		});

		return new AlertDialog.Builder(getActivity()).setTitle(mWorkout.getName()).setView(v).setCancelable(true)
				.setPositiveButton(getString(R.string.save_workout), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// check if file already exists
						if (fileAlreadyExists(edittext_workout_name.getText().toString())) {
							Toast.makeText(getActivity(), getString(R.string.workout_already_exists), Toast.LENGTH_LONG).show();
							return;
						}
						// check if name is empty
						String workoutName = edittext_workout_name.getText().toString();
						if (workoutName.equals("") || workoutName.replaceAll(" ", "").equals(""))
							Toast.makeText(getActivity(), getString(R.string.workout_name_cannot_be_empty), Toast.LENGTH_LONG).show();

						mWorkout.setName(workoutName);

						// save Workout before exiting
						IDataProvider dataProvider = new DataProvider(getActivity());
						dataProvider.saveWorkout(mWorkout);

						finishActivities();
					}
				}).setNeutralButton(getString(R.string.add_more_exercises), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (getActivity() instanceof ExerciseTypeDetailActivity) {
							// close activity that contained the fragment with
							// details
							// to return the result to the list activity
							getActivity().finish();
						}
					}
				}).setNegativeButton(getString(R.string.discard), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finishActivities();
					}
				}).create();
	}

	/**
	 * Checks if a Workout file with the same name already exists.
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

	/**
	 * Finishes the Activities ExerciseTypeDetailActivity and
	 * ExerciseTypeListActivity .
	 */
	private void finishActivities() {
		if (getActivity() instanceof ExerciseTypeDetailActivity) {
			// finish ExerciseTypeDetailActivity AND
			// ExerciseTypeListActivity
			getActivity().finishActivityFromChild(getActivity(), ExerciseTypeListActivity.RESULT_WORKOUT);
			getActivity().finish();
		} else {
			// finish ExerciseTypeListActivity
			getActivity().finish();
		}
	}

}
