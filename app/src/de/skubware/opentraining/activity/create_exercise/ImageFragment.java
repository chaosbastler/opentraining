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

package de.skubware.opentraining.activity.create_exercise;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.start_training.SwipeDismissListViewTouchListener;

public class ImageFragment extends Fragment{
	/** Tag for logging*/
	private final String TAG = "ImageFragment";
	
	/** The ImageView with the exercise image */
	private ImageView mImageView;
	
	/** Uri of the image that is returned by the Intent */
	private Uri mTempImageUri = null;

	private ListView mImageListView;
	private ExerciseImageListAdapter mListAdapter;
	private List<Bitmap> mImageList= new ArrayList<Bitmap>();
	
	public ImageFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_create_exercise_image_fragment, container, false);

		mImageView = (ImageView) layout.findViewById(R.id.imageview_exercise_image);
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto(v);					
			}
		});


		mImageListView = (ListView) layout.findViewById(R.id.listview_exercise_images);
		//TODO mListAdapter = new ExerciseImageListAdapter(getActivity(), mNameImageMap);
		mImageListView.setAdapter(mListAdapter);
		
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				mImageListView,
				new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							mListAdapter.remove(position);
						}
						mListAdapter.notifyDataSetChanged();
					}
				});
		mImageListView.setOnTouchListener(touchListener);	
		
		
		return layout;
	}
	

	public void takePhoto(View view) {
	    Intent intent = new Intent(	android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
	    File photo = new File(Environment.getExternalStorageDirectory(),  "temp_pic.jpg");
	    intent.putExtra(MediaStore.EXTRA_OUTPUT,
	            Uri.fromFile(photo));
	    mTempImageUri = Uri.fromFile(photo);
	    startActivityForResult(intent, CreateExerciseActivity.TAKE_PICTURE);
	}
	
	

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		Log.v(TAG, "onActivityResult(), requestCode=" + requestCode
				+ ", resultCode=" + resultCode);
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case CreateExerciseActivity.TAKE_PICTURE:
			if (resultCode == Activity.RESULT_OK) {
				Uri selectedImage = mTempImageUri;
				getActivity().getContentResolver().notifyChange(
						selectedImage, null);
				ContentResolver cr = getActivity().getContentResolver();
				Bitmap bitmap;
				try {
					bitmap = android.provider.MediaStore.Images.Media
							.getBitmap(cr, selectedImage);

					mImageView.setImageBitmap(bitmap);
					
					mImageList.add(bitmap);
					mListAdapter.notifyDataSetChanged();
					
					Toast.makeText(getActivity(), selectedImage.toString(),
							Toast.LENGTH_LONG).show();
				} catch (Exception e) {
					Toast.makeText(getActivity(), "Failed to load",
							Toast.LENGTH_SHORT).show();
					Log.e(TAG, e.toString(), e);
				}
			}else{
				Toast.makeText(getActivity(), getString(R.string.did_not_provide_image),
						Toast.LENGTH_SHORT).show();
			}
		}
	}
	

	
	public Uri getImage(){
		return mTempImageUri;
	}



}