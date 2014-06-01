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
import android.content.Context;
import android.content.DialogInterface;
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

public class SendExerciseFeedbackDialog extends AlertDialog {

	private final String TAG = "SendExerciseFeedbackDialog";

	/** Reference to current context */
	private Context mContext;

	private ExerciseType mExercise;
	
	public SendExerciseFeedbackDialog(Context context, ExerciseType exercise) {
		super(context);
		mContext = context;
		mExercise = exercise;

		// CheckBox
		LayoutInflater inflater = this.getLayoutInflater();

		final View v = inflater.inflate(R.layout.dialog_send_exercise_feedback, null);
		setView(v);

		// title
		this.setTitle(mContext.getString(R.string.send_feedback_for_exercise, exercise.getLocalizedName()));

		// spinner
		ArrayAdapter<ExerciseUpdateReason> mSpinnerAdapter = new ArrayAdapter<ExerciseUpdateReason>(mContext, android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, ExerciseUpdateReason.values());
		
		final Spinner reasonSpinner = (Spinner) v.findViewById(R.id.exercise_update_reason_spinner);
		reasonSpinner.setAdapter(mSpinnerAdapter);

		// positive button
		this.setButton(BUTTON_POSITIVE, mContext.getString(android.R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				ExerciseUpdateReason reason = (ExerciseUpdateReason) reasonSpinner.getSelectedItem();
				EditText edittext_user_suggestion = (EditText) v.findViewById(R.id.edittext_user_suggestion);
				String userMsg = edittext_user_suggestion.getEditableText().toString();

				
		        // send feedback 
		        HashMap<String,String> ACRAData = new HashMap<String,String>();
		        ACRAData.put("Feedback source ", TAG);
		        ACRA.getErrorReporter().setReportSender(new ACRAFeedbackMailer(ACRAData));
		        // silentException prevents that the dialog for crash reports pops up
				ACRA.getErrorReporter().handleSilentException(new RequestExerciseUpdate(mExercise, reason, userMsg));

				
				dialog.dismiss();
			}
		});
		
		// positive button
		this.setButton(BUTTON_NEGATIVE, mContext.getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

	}

}
