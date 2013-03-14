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

package de.skubware.opentraining.activity;

import java.util.Calendar;
import java.util.List;
import com.actionbarsherlock.app.SherlockDialogFragment;

import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.start_training.FExListActivity;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;

/**
 * Dialog Fragment for choosing a {@link Workout} before starting training.
 * 
 */
public class SelectWorkoutDialogFragment extends SherlockDialogFragment {
	/** Tag for logging */
	public static final String TAG = "SelectWorkoutFragment";

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
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.fragment_dialog_choose_workout, null);
		final RadioButton radioButtonStartNewTraining = (RadioButton) v.findViewById(R.id.radiobutton_start_new_training);

		// get Workouts
		IDataProvider dataProvider = new DataProvider(getActivity());
		final List<Workout> workoutList = dataProvider.getWorkouts();

		AlertDialog.Builder builder_workoutchooser = new AlertDialog.Builder(getActivity());
		builder_workoutchooser.setTitle(getString(R.string.choose_workout));

		final ArrayAdapter<Workout> adapter = new ArrayAdapter<Workout>(getActivity(), android.R.layout.select_dialog_singlechoice,
				workoutList);

		return builder_workoutchooser.setView(v).setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// close dialog and show TrainingEntryDialog
				dialog.dismiss();
				Workout mWorkout = adapter.getItem(item);

				// add TrainingEntry(==start new training) if user choose this
				// or it is necessary because there are no old training entries
				if (radioButtonStartNewTraining.isChecked() || mWorkout.getFitnessExercises().get(0).getTrainingEntryList().isEmpty()) {
					mWorkout.addTrainingEntry(Calendar.getInstance().getTime());
				}

				// add arguments to intent
				Intent intent = new Intent(getActivity(), FExListActivity.class);
				intent.putExtra(FExListActivity.ARG_WORKOUT, mWorkout);
				// start activity
				getActivity().startActivity(intent);

			}

		}).create();

	}

}
