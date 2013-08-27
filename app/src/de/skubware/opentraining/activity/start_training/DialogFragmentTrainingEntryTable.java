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
import java.text.SimpleDateFormat;
import java.util.Iterator;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FSet.SetParameter;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;



/**
 * Dialog Fragment that shows a table with the {@link TrainingEntry}s of an
 * exercise.
 * 
 */
public class DialogFragmentTrainingEntryTable extends DialogFragment {
	/** Tag for logging */
	public static final String TAG = "DialogFragmentTrainingEntryTable";

	/** ID for argument ({@link FitnessExercise}) */
	public static String ARG_ID_EXERCISE = "fex";

	/** Currently displayed {@link FitnessExercise}. */
	private FitnessExercise mFex;

	/**
	 * Create a new instance of DialogFragmentTrainingEntryTable.
	 * 
	 * @param fEx
	 *            The {@link FitnessExercise} that should be displayed

	 * @return The DialogFragment
	 */
	public static DialogFragmentTrainingEntryTable newInstance(FitnessExercise fEx) {
		DialogFragmentTrainingEntryTable f = new DialogFragmentTrainingEntryTable();

		Bundle args = new Bundle();
		args.putSerializable(ARG_ID_EXERCISE, fEx);

		f.setArguments(args);

		return f;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFex = (FitnessExercise) getArguments()
				.getSerializable(ARG_ID_EXERCISE);
	}

	@SuppressLint("SimpleDateFormat")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_training_entry_table, null);
		
		TableLayout table = (TableLayout) v.findViewById(R.id.table_training_entry);
		DateFormat dateformat = new SimpleDateFormat( "dd.MM" );
		boolean odd = false;

		Iterator<TrainingEntry> it = mFex.getTrainingEntryList().iterator();
		int entry_count = 0;
		
		while(it.hasNext()){
			TrainingEntry entry = it.next();
			
			
			for(FSet set:entry.getFSetList()){
				TableRow row;
				if(odd){
					row = (TableRow) inflater.inflate(R.layout.row_type_1, null);
				}else{
					row = (TableRow) inflater.inflate(R.layout.row_type_2, null);
				}
				odd = !odd;
				
				TextView text_view_date = (TextView) row.findViewById(R.id.text_view_date);
				text_view_date.setText(dateformat.format(entry.getDate()));
				
				for(SetParameter parameter:set.getSetParameters()){
					TextView text_view_duration = (TextView) row.findViewById(R.id.text_view_duration);
					TextView text_view_rep = (TextView) row.findViewById(R.id.text_view_rep);
					TextView text_view_weigh = (TextView) row.findViewById(R.id.text_view_weigh);

					if(parameter instanceof SetParameter.Duration){
						text_view_duration.setText(parameter.toString());
					}

					if(parameter instanceof SetParameter.Repetition){
						text_view_rep.setText(parameter.toString());
					}
					
					if(parameter instanceof SetParameter.Weight){
						text_view_weigh.setText(parameter.toString());
					}
				}

				entry_count++;
				
				table.addView(row);
			}
			
			// only append diver row(=just a black row) if there are more rows
			if(it.hasNext()){
				TableRow row_empty = (TableRow) inflater.inflate(R.layout.row_type_empty_row, null);
				table.addView(row_empty);
			}
		}
		
		if(entry_count ==0 ){
			return new AlertDialog.Builder(getActivity())
			.setMessage(getString(R.string.no_other_training_entries))
			.setPositiveButton(getString(android.R.string.ok), new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}				
			})
			.setCancelable(true)
			.create();
		}
		
		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setCancelable(false)
				.create();
	}

}
