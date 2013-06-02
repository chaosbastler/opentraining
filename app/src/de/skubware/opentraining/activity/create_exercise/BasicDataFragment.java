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

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import de.skubware.opentraining.R;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

public class BasicDataFragment extends Fragment{
	/** Tag for logging*/
	private final String TAG = "BasicDataFragment";
	
	/** The ImageView with the exercise image */
	private ImageView mImageView;
	
	/** Uri of the image that is returned by the Intent */
	private Uri mTempImageUri = null;
	
	private EditText mEditTextExerciseNameEnglish;
	private EditText mEditTextExerciseNameGerman;

	
	public BasicDataFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_create_exercise_basic_data, container, false);

		mImageView = (ImageView) layout.findViewById(R.id.imageview_exercise_image);
		mImageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				takePhoto(v);					
			}
		});
		
		mEditTextExerciseNameEnglish = (EditText) layout.findViewById(R.id.edittext_exercise_name_english);
		mEditTextExerciseNameGerman = (EditText) layout.findViewById(R.id.edittext_exercise_name_german);
		
		mEditTextExerciseNameGerman.addTextChangedListener(new ExerciseNameTextWatcher(mEditTextExerciseNameGerman));
		mEditTextExerciseNameEnglish.addTextChangedListener(new ExerciseNameTextWatcher(mEditTextExerciseNameEnglish));

		
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
	
	public String getExerciseNameEnglish(){
		return mEditTextExerciseNameEnglish.getText().toString();
	}
	
	public String getExerciseNameGerman(){
		return mEditTextExerciseNameGerman.getText().toString();
	}
	
	public Uri getImage(){
		return mTempImageUri;
	}


	
	private class ExerciseNameTextWatcher implements TextWatcher {
	    private EditText mEditText;
		IDataProvider mDataProvider = new DataProvider(getActivity());

	    
	    public ExerciseNameTextWatcher(EditText e) { 
	        mEditText = e;
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(mDataProvider.getExerciseByName(s.toString()) != null )
				mEditText.setError(getString(R.string.name_already_used));	
	    }

	    public void afterTextChanged(Editable s) { }
	}
}