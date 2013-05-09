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


import de.skubware.opentraining.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class TrainingEntryListAdapter extends BaseAdapter{

	    private Activity activity;
	    private static LayoutInflater inflater=null;
	 
	    public TrainingEntryListAdapter(Activity a) {
	        activity = a;
	        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    }
	 
	    public int getCount() {
	        return 4;
	    }
	 
	    public Object getItem(int position) {
	        return position;
	    }
	 
	    public long getItemId(int position) {
	        return position;
	    }
	 
	    public View getView(int position, View convertView, ViewGroup parent) {
	        View vi=convertView;

	    	// last element is an empty row
	    	if(position==getCount()-1){
		        if(convertView==null)
		            vi = inflater.inflate(R.layout.list_row_empty, null);
		        return vi;
	    	}
	    	
	        if(convertView==null)
	            vi = inflater.inflate(R.layout.list_row, null);
	 
	        /*TextView title = (TextView)vi.findViewById(R.id.title); // title
	        TextView artist = (TextView)vi.findViewById(R.id.artist); // artist name
	        TextView duration = (TextView)vi.findViewById(R.id.duration); // duration
	        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
	 
	        HashMap<String, String> song = new HashMap<String, String>();
	        song = data.get(position);
	 
	        // Setting all values in listview
	        title.setText(song.get(CustomizedListView.KEY_TITLE));
	        artist.setText(song.get(CustomizedListView.KEY_ARTIST));
	        duration.setText(song.get(CustomizedListView.KEY_DURATION));
	        imageLoader.DisplayImage(song.get(CustomizedListView.KEY_THUMB_URL), thumb_image);*/
	        return vi;
	    }

}
