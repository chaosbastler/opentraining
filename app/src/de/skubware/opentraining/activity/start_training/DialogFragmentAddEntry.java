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

package de.skubware.opentraining.activity.start_training;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FSet.SetParameter;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.Workout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Spinner;

/**
 * Dialog Fragment that adds or edits an {@link TrainingEntry}/ of an
 * {@link FitnessExercise}.
 * 
 * @author Christian Skubich
 * 
 */
public class DialogFragmentAddEntry extends SherlockDialogFragment {
	/** Tag for logging */
	public static final String TAG = "FExDetailFragment";
	
	/** Currently displayed {@link Workout}. */
	private FitnessExercise mFex;

	/** Currently edited {@link TrainingEntry} */
	private TrainingEntry mLatestTrainingEntry;

	/** Currently edited {@link FSet} */
	private FSet mFSet;

	public static String ARG_ID_EXERCISE = "fex";
	public static String ARG_ID_FSET = "subentry";
	
	
	Spinner spinner_duration;
	Spinner spinner_duration_unit;
	Spinner spinner_repetitions;
	Spinner spinner_weight;
	CheckBox checkbox_duration;
	CheckBox checkbox_weight;
	CheckBox checkbox_repetitions;

	public interface Callbacks {
		/**
		 * Callback for when an item has been changed.
		 */
		public void onEntryEdited(FitnessExercise fitnessExercise);
	}

	/**
	 * Create a new instance of MyDialogFragment, providing "num" as an
	 * argument.
	 */
	static DialogFragmentAddEntry newInstance(FitnessExercise fEx, FSet set) {
		DialogFragmentAddEntry f = new DialogFragmentAddEntry();

		Bundle args = new Bundle();
		args.putSerializable(ARG_ID_EXERCISE, fEx);
		args.putSerializable(ARG_ID_FSET, set);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFex = (FitnessExercise) getArguments().getSerializable(ARG_ID_EXERCISE);
		mFSet = (FSet) getArguments().getSerializable(ARG_ID_FSET);

		List<TrainingEntry> entryList = mFex.getTrainingEntryList();
		TrainingEntry latestEntry = entryList.get(entryList.size() - 1);
		mLatestTrainingEntry = latestEntry;

		// if (mFSet == null) {
		// mFSet = mLatestTrainingEntry.add("");
		// }

	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.fragment_dialog_add_entry, null);
		
		spinner_duration = (Spinner) v.findViewById(R.id.spinner_duration);
		spinner_duration_unit = (Spinner) v.findViewById(R.id.spinner_time_unit);
		spinner_repetitions = (Spinner) v.findViewById(R.id.spinner_repetitions);
		spinner_weight = (Spinner) v.findViewById(R.id.spinner_weight);
		checkbox_duration = (CheckBox) v.findViewById(R.id.checkbox_duration);
		checkbox_weight = (CheckBox) v.findViewById(R.id.checkbox_weight);
		checkbox_repetitions = (CheckBox) v.findViewById(R.id.checkbox_repetitions);
		
		
		fillSpinners();


		return new AlertDialog.Builder(getActivity()).setTitle(DateFormat.getInstance().format(mLatestTrainingEntry.getDate())).setView(v)
				.setCancelable(true).setPositiveButton(getString(R.string.save_entry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<SetParameter> setParameters = new ArrayList<SetParameter>();
						if (checkbox_duration.isChecked()) {
							int val = Integer.parseInt((String) spinner_duration.getSelectedItem());
							
							switch (spinner_duration_unit.getSelectedItemPosition()) {
							case 0: // seconds
								break;
							case 1:// minutes
								val = 60 * val; // no break!
							case 2: // hours
								val = 60 * val;
								break;
							default:
								Log.e(TAG, "Unknown choice.");
							}
							
							SetParameter.Duration duration = new SetParameter.Duration(val);
							setParameters.add(duration);
						}
						if(checkbox_weight.isChecked()){
							String str = ((String) spinner_weight.getSelectedItem());
							str = str.substring(0, str.length() - 3);
							str = str.replaceAll(",", ".");
							float floatingNumber = Float.parseFloat(str);
							floatingNumber *= 1000; // convert kg to g
							int val = (int) floatingNumber;
							SetParameter.Weight weight = new SetParameter.Weight(val);
							setParameters.add(weight);
						}
						if(checkbox_repetitions.isChecked()){
							int val = (Integer) spinner_repetitions.getSelectedItemPosition() + 1;
							SetParameter.Repetition repetition = new SetParameter.Repetition(val);
							setParameters.add(repetition);
						}
						if(setParameters.isEmpty()){
							dialog.dismiss();
							return;
						}

						mFSet = new FSet(setParameters.toArray(new SetParameter[setParameters.size()]));
						mLatestTrainingEntry.add(mFSet);

						FExDetailFragment fragment = (FExDetailFragment) getFragmentManager().findFragmentById(
								R.id.exercise_detail_container);
						fragment.onEntryEdited(mFex);

						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.discard_entry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				}).create();
	}

	private void fillSpinners() {

		List<String> durationList = new ArrayList<String>();
		for (Integer i = 1; i <= 120; i++) {
			durationList.add(i.toString());
		}

		ArrayAdapter<String> durationAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, durationList);
		durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_duration.setAdapter(durationAdapter);

		List<String> repetitionList = new ArrayList<String>();
		for (int i = 1; i <= 120; i++) {
			repetitionList.add(i + " x");
		}

		ArrayAdapter<String> repetitionAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,
				repetitionList);
		repetitionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_repetitions.setAdapter(repetitionAdapter);

		// TODO add upper border limit to a settings menu
		List<String> weightList = new ArrayList<String>();
		for (int i = 250; i <= 1000; i += 250) {
			weightList.add(i / 1000 + "," + i % 1000 + " kg");
		}

		for (int i = 1500; i <= 5000; i += 500) {
			weightList.add(i / 1000 + "," + i % 1000 + " kg");
		}

		for (int i = 6000; i <= 200000; i += 1000) {
			weightList.add(i / 1000 + "," + i % 1000 + " kg");
		}


		ArrayAdapter<String> weightAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, weightList);
		weightAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_weight.setAdapter(weightAdapter);


		checkbox_duration.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean val) {
				spinner_duration.setEnabled(val);
				spinner_duration_unit.setEnabled(val);
			}
		});
		checkbox_weight.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean val) {
				spinner_weight.setEnabled(val);
			}
		});
		checkbox_repetitions.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton button, boolean val) {
				spinner_repetitions.setEnabled(val);
			}
		});

		checkbox_duration.setChecked(true);
		checkbox_weight.setChecked(true);
		checkbox_repetitions.setChecked(true);

	}


}
