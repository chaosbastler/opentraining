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

import java.util.HashMap;
import org.acra.ACRA;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.acra.ACRAFeedbackMailer;
import de.skubware.opentraining.activity.acra.RequestExerciseUpdate;
import de.skubware.opentraining.activity.acra.RequestExerciseUpdate.ExerciseUpdateReason;
import de.skubware.opentraining.basic.ExerciseType;

public class SendExerciseFeedbackDialogFragment  extends DialogFragment{

	private final String TAG = "SendExerciseFeedbackDialog";

	// key for bundle (save/restore instance state)
	private final String KEY_EXERCISE = "key_exercise";
	private final String KEY_USER_MESSAGE = "key_user_message";
	private final String KEY_EXERCISE_UPDATE_REASON = "key_exercise_update_reason";

	private ExerciseType mExercise;
	
	private Spinner mReasonSpinner;
	private EditText mEditText;
	
	private String mUserMessage = "";
	private int mReasonSelected = -1;

	/**
	 * Create a new instance of SelectWorkoutFragment.
	 */
	static SendExerciseFeedbackDialogFragment newInstance(ExerciseType exercise) {
		SendExerciseFeedbackDialogFragment s = new SendExerciseFeedbackDialogFragment(exercise);
		return s;
	}
	
	public SendExerciseFeedbackDialogFragment(ExerciseType exercise){
		mExercise = exercise;
	}
	
	/** 
	 * Empty constructor, required for DialogFragment.
	 * Argument should be passed via Bundle
	 * */
	public SendExerciseFeedbackDialogFragment(){
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			mExercise = (ExerciseType) savedInstanceState.getSerializable(KEY_EXERCISE);
			mUserMessage = savedInstanceState.getString(KEY_USER_MESSAGE);
			mReasonSelected = savedInstanceState.getInt(KEY_EXERCISE_UPDATE_REASON);
		}	
	}

	@Override
	public void onSaveInstanceState(Bundle bundle){
		bundle.putSerializable(KEY_EXERCISE, mExercise);
		
		if(mEditText != null && mReasonSpinner != null){
			mUserMessage = mEditText.getEditableText().toString();
			mReasonSelected = mReasonSpinner.getSelectedItemPosition();
		
			bundle.putString(KEY_USER_MESSAGE, mUserMessage);
			bundle.putInt(KEY_EXERCISE_UPDATE_REASON, mReasonSelected);
		}	
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		LayoutInflater inflater = getActivity().getLayoutInflater();

		final View v = inflater.inflate(R.layout.dialog_send_exercise_feedback, null);
		builder.setView(v);

		// edit text
		mEditText = (EditText) v.findViewById(R.id.edittext_user_suggestion);
		mEditText.setText(mUserMessage);
		
		// title
		builder.setTitle(getActivity().getString(R.string.send_feedback_for_exercise, mExercise.getLocalizedName()));

		// spinner
		mReasonSpinner = (Spinner) v.findViewById(R.id.exercise_update_reason_spinner);

		ExerciseUpdateReason.translateEnums(getActivity());
		ArrayAdapter<ExerciseUpdateReason> mSpinnerAdapter = new ArrayAdapter<ExerciseUpdateReason>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, ExerciseUpdateReason.values());
		
		mReasonSpinner.setAdapter(mSpinnerAdapter);
		
		// restore old selction
		if(mReasonSelected >= 0){
			mReasonSpinner.setSelection(mReasonSelected);
		}
		
		// positive button
		builder.setPositiveButton(getActivity().getString(android.R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				ExerciseUpdateReason reason = (ExerciseUpdateReason) mReasonSpinner.getSelectedItem();
				String userMsg = mEditText.getEditableText().toString();

				
		        // send feedback 
		        ACRA.getErrorReporter().setReportSender(new ACRAFeedbackMailer());
		        // silentException prevents that the dialog for crash reports pops up
				ACRA.getErrorReporter().handleSilentException(new RequestExerciseUpdate(mExercise, reason, userMsg));

				
				dialog.dismiss();
			}
		});
		
		// positive button
		builder.setNegativeButton(getActivity().getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
				

		
		return builder.create();

	}

}
