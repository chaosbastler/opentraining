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

package de.skubware.opentraining.activity;

import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.R;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import at.technikum.mti.fancycoverflow.FancyCoverFlow.LayoutParams;
import at.technikum.mti.fancycoverflow.FancyCoverFlowAdapter;

public class NavigationGalleryAdapter extends FancyCoverFlowAdapter {

	private List<String> mNameList = new ArrayList<String>();
	private Context mContext;

	private static String TAG = "NavigationGalleryAdapter";
	
	public NavigationGalleryAdapter(Context context) {
		mContext = context;
		
		mNameList.add(mContext.getString(R.string.start_training));
		mNameList.add(mContext.getString(R.string.create_workout));
		mNameList.add(mContext.getString(R.string.manage_workouts));
		mNameList.add(mContext.getString(R.string.settings));
	}

	@Override
	public int getCount() {
		return mNameList.size();
	}

	@Override
	public Object getItem(int i) {
		return mNameList.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getCoverFlowItem(int i, View reuseableView, ViewGroup viewGroup) {
		TextView textView = new TextView(viewGroup.getContext());
		textView.setText(mNameList.get(i));
		textView.setTextColor(Color.DKGRAY);
		textView.setTextSize(android.util.TypedValue.COMPLEX_UNIT_DIP, 25.0f);
		textView.setLayoutParams(getLayoutParams());

		return textView;
	}

	@SuppressWarnings("deprecation")
	private FancyCoverFlow.LayoutParams getLayoutParams() {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		int displayWidth = display.getWidth(); 	// using deprecated methods as new methods require  api v13
		int displayHeight = display.getHeight();


		int width = LayoutParams.WRAP_CONTENT;
		int height = LayoutParams.MATCH_PARENT;

		Configuration config = mContext.getResources().getConfiguration();
		if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  
		    Configuration.SCREENLAYOUT_SIZE_XLARGE	||
		    (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  
		    Configuration.SCREENLAYOUT_SIZE_LARGE) {
		    // xlarge screens: at least 960dp x 720dp
			Log.v(TAG, "XLARGE or LARGE screen");
			width = (displayWidth * 1/4);
			height = (displayHeight * 1/4);
			
		}else if((config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  
		    Configuration.SCREENLAYOUT_SIZE_NORMAL || 
		    (config.screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) ==  
			    Configuration.SCREENLAYOUT_SIZE_SMALL) {
		    // small screens: at least 426dp x 320dp
			Log.v(TAG, "NORMAL or SMALL screen");
			width = (displayWidth * 1/2);
			height = (displayHeight * 1/4);
		}
		

		return new LayoutParams(width, height);
	}

}
