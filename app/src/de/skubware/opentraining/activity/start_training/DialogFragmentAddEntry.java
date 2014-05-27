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

package de.skubware.opentraining.activity.start_training;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FSet.SetParameter;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;

/**
 * Dialog Fragment that adds or edits an {@link TrainingEntry}/ of an
 * {@link FitnessExercise}.
 * 
 */
public class DialogFragmentAddEntry extends DialogFragment {
	/** Tag for logging */
	public static final String TAG = "DialogFragmentAddEntry";

	/** ID for argument ({@link FitnessExercise}) */
	public static String ARG_ID_EXERCISE = "fex";

	/** ID for optional argument ({@link FSet}) */
	public static String ARG_ID_FSET = "fset";

	/** ID for argument (position of the FSet in the list) */
	public static String ARG_ID_FSET_POSITION = "fset_position";
	
	/** ID for optional argument ({@link TrainingEntry}) */
	public static String ARG_ID_TRAINING_ENTRY = "trainingentry";

	/** Currently displayed {@link FitnessExercise}. */
	private FitnessExercise mFex;

	/** Currently edited {@link TrainingEntry} */
	private TrainingEntry mTrainingEntry;

	/** Currently edited {@link FSet} */
	private FSet mFSet;
	
	/** The position of {@link #mFSet} in {@link #mTrainingEntry} */
	private int mFSetPosition;

	private Spinner spinner_duration;
	private Spinner spinner_duration_unit;
	private Spinner spinner_repetitions;
	private Spinner spinner_weight;
	private CheckBox checkbox_duration;
	private CheckBox checkbox_weight;
	private CheckBox checkbox_repetitions;
	
	enum EntryAction{
		CREATING_ENTRY, EDITING_ENTRY;
	}
	private EntryAction mState;

	public interface Callbacks {
		/**
		 * Callback for when an item has been changed.
		 */
		public void onEntryEdited(FitnessExercise fitnessExercise);
	}

	/**
	 * Create a new instance of DialogFragmentAddEntry.
	 * 
	 * @param fEx
	 *            The {@link FitnessExercise} that should be edited
	 * @param set
	 *            Optional parameter, the {@link FSet} that should be edited. If
	 *            this is null a new FSet will be added to the
	 *            {@link TrainingEntry}
 	 * @param setPosition
	 *			  The position of FSet in the TrainingEntry. Will only be used, if the set is edited
	 * @param trainingEntry
	 *            Optional parameter, the {@link TrainingEntry} that should be
	 *            edited. If this is null the latest TrainingEntry will be
	 *            chosen.
	 * 
	 * @return The DialogFragment
	 */
	static DialogFragmentAddEntry newInstance(FitnessExercise fEx, FSet set, int setPosition, TrainingEntry trainingEntry) {
		DialogFragmentAddEntry f = new DialogFragmentAddEntry();

		Bundle args = new Bundle();
		args.putSerializable(ARG_ID_EXERCISE, fEx);
		args.putSerializable(ARG_ID_FSET, set);
		args.putInt(ARG_ID_FSET_POSITION, setPosition);
		args.putSerializable(ARG_ID_TRAINING_ENTRY, trainingEntry);

		f.setArguments(args);

		return f;
	}

	/** @see #newInstance(FitnessExercise, FSet, TrainingEntry) */
	static DialogFragmentAddEntry newInstance(FitnessExercise fEx, FSet set, int setPosition) {
		return newInstance(fEx, set, setPosition, null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFex = (FitnessExercise) getArguments().getSerializable(ARG_ID_EXERCISE);
		mFSet = (FSet) getArguments().getSerializable(ARG_ID_FSET);
		mFSetPosition = getArguments().getInt(ARG_ID_FSET_POSITION);
		mTrainingEntry = (TrainingEntry) getArguments().getSerializable(ARG_ID_TRAINING_ENTRY);

		// select latest TrainingEntry if argument was null
		if(mTrainingEntry == null){
			List<TrainingEntry> entryList = mFex.getTrainingEntryList();
			TrainingEntry latestEntry = entryList.get(entryList.size() - 1);
			mTrainingEntry = latestEntry;
			mState = EntryAction.CREATING_ENTRY;
		}
		
		if(mFSet==null){
			Log.v(TAG, "Argument for mFSet was null.");
			mState = EntryAction.CREATING_ENTRY;
		}else{
			Log.v(TAG, "Argument for mFSet was: " + mTrainingEntry.toDebugString());
			mState = EntryAction.EDITING_ENTRY;
		}

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

		setSpinners();

		
		// show different text, if an entry is edited(not created)
		TextView tw = (TextView)v.findViewById(R.id.textview_entryaction);
		if(mState == EntryAction.EDITING_ENTRY){
			tw.setText(getActivity().getString(R.string.edit_entry));
		}else{
			tw.setText(getActivity().getString(R.string.add_entry));
		}

		
		return new AlertDialog.Builder(getActivity()).setTitle(DateFormat.getInstance().format(mTrainingEntry.getDate())).setView(v)
				.setCancelable(true).setPositiveButton(getString(R.string.save_entry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						List<SetParameter> setParameters = new ArrayList<SetParameter>();
						if (checkbox_duration.isChecked()) {
							SetParameter.Duration duration = new SetParameter.Duration(getDurationValue());
							setParameters.add(duration);
						}
						if (checkbox_weight.isChecked()) {
							SetParameter.Weight weight = new SetParameter.Weight(getWeightValue());
							setParameters.add(weight);
						}
						if (checkbox_repetitions.isChecked()) {
							SetParameter.Repetition repetition = new SetParameter.Repetition(getRepetitionValue());
							setParameters.add(repetition);
						}

						// if no SetParameter has been chosen, the FSet
						// must not be created or even has to be deleted
						if (setParameters.isEmpty()) {
							if (mFSet != null) {
								// delete existing FSet
								boolean success = mTrainingEntry.getFSetList().remove(mFSet);
								if(!success)
									Log.e(TAG, "Could not delete FSet:\n " + mFSet.toString() + "\n in TrainingEntry:\n " + mTrainingEntry.toDebugString());
							} else {
								// do nothing
								dialog.dismiss();
								return;
							}
						}else{
							// if SetParameters have been chosen, either the old
							// FSet has to be updated or a new FSet has to be
							// added
							if (mFSet == null) {
								// add new FSet
								mFSet = new FSet(setParameters.toArray(new SetParameter[setParameters.size()]));
								mTrainingEntry.add(mFSet);
							} else {
								// replace old FSet
								mFSet = new FSet(setParameters.toArray(new SetParameter[setParameters.size()]));
								mTrainingEntry.getFSetList().set(mFSetPosition, mFSet);
							}
							mTrainingEntry.setHasBeenDone(mFSet, false);

						}


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

	private int getDurationValue() {
		int val = Integer.parseInt((String) spinner_duration.getSelectedItem());

		switch (spinner_duration_unit.getSelectedItemPosition()) {
		case 0: // seconds
			break;
		case 2: // hours
			val = 60 * val; // no break!
		case 1: // minutes
			val = 60 * val;
			break;
		default:
			Log.e(TAG, "Unknown choice.");
		}
		return val;
	}

	private void setDurationValue(int value) {
		spinner_duration_unit.setSelection(0);

		if (value > 3600) {
			value = value / 3600;
			spinner_duration_unit.setSelection(2);
		}
		if (value > 60) {
			value = value / 60;
			spinner_duration_unit.setSelection(1);
		}

		spinner_duration.setSelection(value - 1);
	}

	private int getWeightValue() {
		String str = ((String) spinner_weight.getSelectedItem());
		str = str.substring(0, str.length() - 3);
		str = str.replaceAll(",", ".");
		float floatingNumber = Float.parseFloat(str);
		floatingNumber *= 1000; // convert kg to g
		int val = (int) floatingNumber;
		return val;
	}

	private void setWeightValue(int value) {

		// this is not a very elegant solution but it should work
		ListAdapter adapter = (ListAdapter) spinner_weight.getAdapter();
		for (int i = 0; i < adapter.getCount() - 1; i++) {
			String str = ((String) spinner_weight.getItemAtPosition(i));
			str = str.substring(0, str.length() - 3);
			str = str.replaceAll(",", ".");
			float floatingNumber = Float.parseFloat(str);
			floatingNumber *= 1000; // convert kg to g
			int val = (int) floatingNumber;

			if (val == value) {
				spinner_weight.setSelection(i);
				return;
			}
		}

		Log.e(TAG, "Could not set weight value: " + value);

	}

	private int getRepetitionValue() {
		return (Integer) spinner_repetitions.getSelectedItemPosition() + 1;
	}

	private void setRepetitionValue(int value) {
		spinner_repetitions.setSelection(value - 1);
	}

	/**
	 * Fills the spinners with entries, e.g. with "1 kg"(weight_spinner) or just
	 * numbers.
	 */
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
		
		for (int i = 205000; i <= 400000; i += 5000) {
			weightList.add(i / 1000 + "," + i % 1000 + " kg");
		}

		for (int i = 410000; i <= 500000; i += 10000) {
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

	/**
	 * If mFSet is not null it's settings will be loaded. Otherwise the last
	 * used settings will be loaded.
	 */
	private void setSpinners() {
		FSet setToSet = mFSet;
		// only load last settings if no argument for mFSet has been passed(that
		// means mFSet is null)
		if (setToSet == null) {
			Log.v(TAG, "Trying to find old TrainingEntry for loading spinner settings.");

			List<TrainingEntry> entryList = mFex.getTrainingEntryList();
			TrainingEntry previousEntry;
			for (int i = entryList.size() - 1; (i >= 0) && (setToSet == null); i--) {
				previousEntry = entryList.get(i);
				List<FSet> fsetList = previousEntry.getFSetList();
				if (!fsetList.isEmpty()) {
					setToSet = fsetList.get(fsetList.size() - 1);
				}
			}

		}

		if (setToSet == null) {
			Log.w(TAG, "Did not find any old TrainingEntry for loading spinner settings.");
			return;
		}

		checkbox_duration.setChecked(false);
		checkbox_weight.setChecked(false);
		checkbox_repetitions.setChecked(false);

		for (SetParameter param : setToSet.getSetParameters()) {
			int value = param.getValue();

			if (param instanceof SetParameter.Duration) {
				checkbox_duration.setChecked(true);
				setDurationValue(value);
			}
			if (param instanceof SetParameter.Repetition) {
				checkbox_repetitions.setChecked(true);
				setRepetitionValue(value);
			}
			if (param instanceof SetParameter.Weight) {
				checkbox_weight.setChecked(true);
				setWeightValue(value);
			}
		}

	}

}
