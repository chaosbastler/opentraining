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

import java.util.ArrayList;
import java.util.List;


import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataHelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class FExListAdapter extends BaseAdapter {

	private FragmentActivity mActivity;
	private static LayoutInflater mInflater = null;

	private List<FitnessExercise> mFitnessExerciseList;
	private List<TrainingEntry> mTrainingEntryList;


	public FExListAdapter(FragmentActivity activity, Workout workout) {
		mActivity = activity;
		mInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mTrainingEntryList  = new ArrayList<TrainingEntry>();

		if(workout!=null){
			mFitnessExerciseList = workout.getFitnessExercises();
			for(FitnessExercise fEx:mFitnessExerciseList){
				mTrainingEntryList.add(fEx.getLastTrainingEntry());
			}
		}
		else{
			mFitnessExerciseList = new ArrayList<FitnessExercise>();
		}

			
	}

	public int getCount() {
		return mFitnessExerciseList.size();
	}

	public Object getItem(int position) {
		return mFitnessExerciseList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		FitnessExercise fEx = (FitnessExercise) getItem(position);
		
		if(fEx.isTrainingEntryFinished(fEx.getLastTrainingEntry())){
			vi = mInflater.inflate(R.layout.list_row_fex_done, null);
		}else{
			vi = mInflater.inflate(R.layout.list_row_fex, null);	
			int unfinished = calculateUnfinishedTrainingEntries(fEx);
			
			TextView textview_remaining_sets =(TextView) vi.findViewById(R.id.textview_remaining_sets);
			textview_remaining_sets.setText(mActivity.getString(R.string.remaining_sets) + " "+ unfinished);
		}
		
		final ImageView imageview_ex_image = (ImageView) vi.findViewById(R.id.imageview_ex_image);
		DataHelper data = new DataHelper(mActivity);
		// Images
		if (!fEx.getImagePaths().isEmpty()) {		
			String icon = fEx.getImagePaths().get(0).toString();
			icon = icon.replace(".", "_icon.");
			Drawable iconDrawable = data.getDrawable(icon);
			if(iconDrawable == null){
				iconDrawable = data.getDrawable(fEx.getImagePaths().get(0).toString());
			}
			imageview_ex_image.setImageDrawable(iconDrawable);
		} else {
			// set default image if no image can be found
			imageview_ex_image.setImageResource(R.drawable.ic_launcher);
		}
		
		
		TextView textview_exercise_name = (TextView) vi.findViewById(R.id.textview_exercise_name);
		
		String ex_name = fEx.getLocalizedName();
		textview_exercise_name.setText(ex_name, BufferType.SPANNABLE);


		return vi;
	}

	private int calculateUnfinishedTrainingEntries(FitnessExercise fEx){
		int unfinished = 0;
		TrainingEntry lastEntry = fEx.getLastTrainingEntry();
		for(FSet set:lastEntry.getFSetList()){
			if(!lastEntry.hasBeenDone(set))
				unfinished++;
		}
		return unfinished;
	}

}
