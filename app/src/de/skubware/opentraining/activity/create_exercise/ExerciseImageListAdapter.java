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

package de.skubware.opentraining.activity.create_exercise;

import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;



public class ExerciseImageListAdapter extends BaseAdapter{
	/** Tag for logging*/
	private final String TAG = "ExerciseImageListAdapter";

	private Context mContext;
	private static LayoutInflater mInflater = null;

	private List<ImageData> mImageList = new ArrayList<ImageData>();
	
	/** Container-class for the data that belongs to an image */
	static class ImageData implements Parcelable{
		
		Bitmap bitmap;
		String name;
		License imageLicense;
		
		public ImageData(){
		}
		
		public ImageData(Parcel in){
			this.bitmap = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
			this.name = in.readString();
			this.imageLicense = (License) in.readSerializable();
		}
		
		@Override
		public int describeContents() {
			return 0;
		}
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeParcelable(bitmap, 0);
			dest.writeString(name);
			dest.writeSerializable(imageLicense);
		}
		
		static final Parcelable.Creator<ImageData> CREATOR
        = new Parcelable.Creator<ImageData>() {
			
			@Override
			public ImageData createFromParcel(Parcel in) {
				return new ImageData(in);
			}

			@Override
			public ImageData[] newArray(int size) {
				return new ImageData[size];
			}
};
		
	}
	
	// ViewHolder, caches imageView
	static class ViewHolderItem {
	    ImageView imageViewItem;
	}

	public ExerciseImageListAdapter(Context context, List<ImageData> imageList) {
		mContext = context;
		mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		mImageList = imageList;
	}

	public int getCount() {
		return mImageList.size();
	}

	@Override
	public Object getItem(int position) {
		return mImageList.get(position);
	}
	
	public Bitmap getBitmap(int position){
		return ((ImageData) getItem(position)).bitmap;
	}

	public long getItemId(int position) {
		return position;
	}
	
	public String getImageName(int position){
		return (String) mImageList.get(position).name;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		
		ViewHolderItem viewHolder;
		
		if(convertView==null){
	        // inflate the layout
	        convertView = mInflater.inflate(de.skubware.opentraining.R.layout.exercise_image_list_row, null);

	         
	        // set up the ViewHolder
	        viewHolder = new ViewHolderItem();
	        viewHolder.imageViewItem = (ImageView) convertView.findViewById(R.id.exercise_image);
	         
	        // store the holder with the view.
	        convertView.setTag(viewHolder);
	         
	    }else{
	        // avoided calling findViewById() on resource each time, use the viewHolder
	        viewHolder = (ViewHolderItem) convertView.getTag();
	    }
	     
	    Bitmap objectItem = this.getBitmap(position);
	     
	    // assign values if the object is not null
	    if(objectItem != null) {
	        // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
	        viewHolder.imageViewItem.setImageBitmap(getBitmap(position));
	        viewHolder.imageViewItem.setTag(getImageName(position));
	        viewHolder.imageViewItem.setOnClickListener(new OnClickListener(){
	        	@Override
	        	public void onClick(View v) {
	        		new EditImageMetadataDialog((Activity) mContext, position, ExerciseImageListAdapter.this);
	        	}
	        });
	        	        
	    }else{
	    	Log.e(TAG, "No bitmap found for position: " + position);
	    }
	     
	    return convertView;
	}

    	
	
	public void remove(int position){
		// delete image
		ImageData img = mImageList.get(position);
		IDataProvider dataProvider = new DataProvider(mContext);
		dataProvider.deleteCustomImage(img.name, true); // if it is not a user-generated image this call will be ignored
		
		// remove from list adapter
		mImageList.remove(position);
		notifyDataSetChanged();
	}

	public void moveItem(int oldPosition, int newPosition){
		if(oldPosition <0 || newPosition <0 || oldPosition > getCount()-1 || newPosition > getCount()-1 ){
			Log.e(TAG, "moveItem cannot be done, as >= position is invalid. oldPosition=" + oldPosition + " , newPostion=" + newPosition + " , getCounter()=" + getCount());
			return;
		}
		
		if(newPosition == oldPosition)
			return;
		
		ImageData movedItem = mImageList.remove(oldPosition);
		// list size changed, so adapt newPosition  if necessary
		//if(newPosition>oldPosition) newPosition--;
		mImageList.add(newPosition, movedItem);

		
		notifyDataSetChanged();
		
	}
	
}
