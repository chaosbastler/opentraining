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

package de.skubware.opentraining.activity.manage_workouts;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashSet;
import java.util.Set;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Dialog Fragment that handles renaming a Workout.
 * 
 * The user can enter a new name for the Workout or cancel this dialog. If there
 * is already a Workout with the same name, an error message(toast) will be
 * shown.
 * 
 * @author Christian Skubich
 * 
 */
public class RenameWorkoutDialogFragment extends DialogFragment {
	/** Tag for logging */
	private static final String TAG = "RenameWorkoutDialogFragment";

	/** Currently displayed {@link Workout}. */
	Workout mWorkout;

	/** ID for argument */
	private static String ARG_ID_WORKOUT = "workout";

	/**
	 * Create a new instance of RenameWorkoutDialogFragment.
	 */
	static RenameWorkoutDialogFragment newInstance(Workout workout) {
		RenameWorkoutDialogFragment f = new RenameWorkoutDialogFragment();

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
		final View v = inflater.inflate(R.layout.fragment_dialog_rename_workout, null);

		// show old name
		final EditText edittext_workout_name = showOldName(v);

		return new AlertDialog.Builder(getActivity()).setTitle(mWorkout.getName()).setView(v).setCancelable(true)
				.setPositiveButton(getString(R.string.save), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String enterendName = edittext_workout_name.getText().toString();

						// check if name is valid(not empty, not used)
						if (checkValidity(enterendName)) return;

						// delete old Workout
						IDataProvider dataProvider = deleteOldWorkout(enterendName);

						// save new Workout
						if (saveNewWorkout(dataProvider)) return;

						// finally update GUI
						updateGUI(enterendName);

						// update Workout in Activity
						updateWorkoutInActivity();

						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
	}

	private void updateWorkoutInActivity() {
		if (getActivity() instanceof WorkoutListActivity) {
            ((WorkoutListActivity) getActivity()).onWorkoutChanged(mWorkout);
        } else {
            // was launched by WorkoutDetailActivity
            // so set result to update WorkoutListActivity later
            Intent i = new Intent();
            i.putExtra(WorkoutListActivity.ARG_WORKOUT, mWorkout);
            getActivity().setResult(Activity.RESULT_OK, i);
        }
	}

	private void updateGUI(String enterendName) {
		TextView textview_workout_name = (TextView) getActivity().findViewById(R.id.textview_workout_name);
		textview_workout_name.setText(enterendName);
	}

	private boolean saveNewWorkout(IDataProvider dataProvider) {
		boolean success = dataProvider.saveWorkout(mWorkout);
		if (!success) {
            Log.wtf(TAG, "Error during saving workout. Old workout was lost. This should never happen.");
            Toast.makeText(getActivity(), getString(R.string.error_during_saving), Toast.LENGTH_LONG).show();
			return true;
        }
		return false;
	}

	@NonNull
	private IDataProvider deleteOldWorkout(String enterendName) {
		IDataProvider dataProvider = new DataProvider(getActivity());
		dataProvider.deleteWorkout(mWorkout);

		mWorkout.setName(enterendName);
		return dataProvider;
	}

	private boolean checkValidity(String enterendName) {
		if (enterendName.equals("")) {
            Toast.makeText(getActivity(), getString(R.string.workout_name_cannot_be_empty), Toast.LENGTH_LONG).show();
			return true;
        }

		if (fileAlreadyExists(enterendName)) {
            Toast.makeText(getActivity(), getString(R.string.workout_already_exists), Toast.LENGTH_LONG).show();
			return true;
        }
		return false;
	}

	@NonNull
	private EditText showOldName(View v) {
		final EditText edittext_workout_name = (EditText) v.findViewById(R.id.edittext_workout_name);
		edittext_workout_name.setText(mWorkout.getName());
		return edittext_workout_name;
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

}
