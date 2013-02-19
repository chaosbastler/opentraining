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
import java.util.List;

import com.actionbarsherlock.app.SherlockDialogFragment;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.TrainingSubEntry;
import de.skubware.opentraining.basic.Workout;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Dialog Fragment that adds or edits an {@link TrainingEntry}/ of an
 * {@link FitnessExercise}.
 * 
 * @author Christian Skubich
 * 
 */
public class DialogFragmentAddEntry extends SherlockDialogFragment {

	/** Currently displayed {@link Workout}. */
	private FitnessExercise mFex;
	
	/** Currently edited {@link TrainingEntry}*/
	private TrainingEntry mLatestTrainingEntry;
	
	/** Currently edited {@link TrainingSubEntry}*/
	private TrainingSubEntry mSubEntry;
	
	public static String ARG_ID_EXERCISE = "fex";
	public static String ARG_ID_TRAINING_SUB_ENTRY = "subentry";
	
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
	static DialogFragmentAddEntry newInstance(FitnessExercise fEx, TrainingSubEntry subEntry) {
		DialogFragmentAddEntry f = new DialogFragmentAddEntry();

		Bundle args = new Bundle();
		args.putSerializable(ARG_ID_EXERCISE, fEx);
		args.putSerializable(ARG_ID_TRAINING_SUB_ENTRY, subEntry);
		f.setArguments(args);

		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFex = (FitnessExercise) getArguments().getSerializable(ARG_ID_EXERCISE);
		mSubEntry = (TrainingSubEntry) getArguments().getSerializable(ARG_ID_TRAINING_SUB_ENTRY);
		
		
		List<TrainingEntry> entryList = mFex.getTrainingEntryList();
		TrainingEntry latestEntry = entryList.get(entryList.size()-1);
		mLatestTrainingEntry = latestEntry;
		
		
		if(mSubEntry==null){
			mSubEntry = mLatestTrainingEntry.add("");
		}
		
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.fragment_dialog_add_entry, null);
		final EditText edittext_training_entry = (EditText) v.findViewById(R.id.edittext_training_entry);
		
		if(this.mSubEntry != null){
			edittext_training_entry.setText(this.mSubEntry.getContent());
		}

		return new AlertDialog.Builder(getActivity()).setTitle(DateFormat.getInstance().format(mLatestTrainingEntry.getDate())).setView(v).setCancelable(true)
				.setPositiveButton(getString(R.string.save_entry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String content = edittext_training_entry.getText().toString();
						
						if(content.equals("")){
							mLatestTrainingEntry.remove(mSubEntry);
						}else{
							mSubEntry.setContent(content);
						}
						
						FExDetailFragment fragment = (FExDetailFragment) getFragmentManager().findFragmentById(R.id.exercise_detail_container);
						fragment.onEntryEdited(mFex);
						
						hideKeyboard(edittext_training_entry);
						dialog.dismiss();
					}
				}).setNegativeButton(getString(R.string.discard_entry), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String content = edittext_training_entry.getText().toString();

						if(content.equals("")){
							mLatestTrainingEntry.remove(mSubEntry);
						}
						
						hideKeyboard(edittext_training_entry);
						dialog.dismiss();
					}
				}).create();
	}
	
	/**
	 * Hides the keyboard.
	 * 
	 * @param edittext_training_entry The EditText.
	 */
	private void hideKeyboard(EditText edittext_training_entry) {
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(edittext_training_entry.getWindowToken(), 0);
	}

}
