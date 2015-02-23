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
import java.text.SimpleDateFormat;
import java.util.ArrayList;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;


/**
 * Dialog Fragment that shows a table with the {@link TrainingEntry}s of an
 * exercise.
 * 
 */
public class DialogFragmentHistory extends DialogFragment {
	/** Tag for logging */
	public static final String TAG = "DialogFragmentHistory";

	/** ID for argument ({@link FitnessExercise}) */
	public static String ARG_ID_EXERCISE = "fex";

	/** Currently displayed {@link FitnessExercise}. */
	private FitnessExercise mFex;

	/**
	 * Create a new instance of DialogFragmentHistory.
	 * 
	 * @param fEx
	 *            The {@link FitnessExercise} that should be displayed

	 * @return The DialogFragment
	 */
	public static DialogFragmentHistory newInstance(FitnessExercise fEx) {
		DialogFragmentHistory f = new DialogFragmentHistory();

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



    class RepetitionsWrapper{

    }



	@SuppressLint("SimpleDateFormat")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

        if( mFex.getTrainingEntryList().isEmpty() ){
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




        LayoutInflater inflater = LayoutInflater.from(getActivity());
		final View v = inflater.inflate(R.layout.dialog_training_history_layout, null);


        LineChart mLineChart = (LineChart) v.findViewById(R.id.chart);


        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();

        ArrayList<Entry> durationList = new ArrayList<Entry>();
        ArrayList<Entry> weightList = new ArrayList<Entry>();
        ArrayList<Entry> repList = new ArrayList<Entry>();

        ArrayList<String> xVals = new ArrayList<String>();

        int setParameterNumber = 0;
        for(TrainingEntry entry:mFex.getTrainingEntryList()){
            int setNumber = 0;
            for(FSet fset:entry.getFSetList()){

                // skip sets that haven't been done
                if(!entry.hasBeenDone(fset))
                    continue;


                // x value: date
                DateFormat dateformat = new SimpleDateFormat( "dd.MM" );
                xVals.add(dateformat.format(entry.getDate()) + " ("  + setNumber + ")");

                // y values: weight, rep, duration
                for(SetParameter parameter:fset.getSetParameters()){
                    Entry e = new Entry(parameter.getValue(), setParameterNumber);

                    if(parameter instanceof SetParameter.Duration){
                        durationList.add(e);
                    }else if(parameter instanceof SetParameter.Repetition){
                        repList.add(e);
                    }else if(parameter instanceof SetParameter.Weight){
                        e = new Entry(parameter.getValue()/1000, setParameterNumber);
                        weightList.add(e);
                    }else{
                        Log.e(TAG, "Unknown Parameter Type!");
                    }
                }

                setParameterNumber++;
                setNumber++;
            }

        }


        LineDataSet dataSetWeight = new LineDataSet(weightList, getString(R.string.weight));
        dataSetWeight.setColors(new int[] { android.R.color.holo_blue_light }, getActivity());
        LineDataSet dataSetRep = new LineDataSet(repList, getString(R.string.repetitions));
        dataSetRep.setColors(new int[] { android.R.color.holo_red_light }, getActivity());
        LineDataSet dataSetDur = new LineDataSet(durationList, getString(R.string.duration));
        dataSetDur.setColors(new int[] { android.R.color.holo_green_light }, getActivity());

        dataSets.add(dataSetWeight);
        dataSets.add(dataSetRep);
        dataSets.add(dataSetDur);


        LineData data = new LineData(xVals, dataSets);
        mLineChart.setData(data);
        mLineChart.setDescription(getString(R.string.history));


		return new AlertDialog.Builder(getActivity())
				.setView(v)
				.setCancelable(false)
				.create();
	}

}
