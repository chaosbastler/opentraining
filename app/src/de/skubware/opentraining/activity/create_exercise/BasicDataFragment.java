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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import de.skubware.opentraining.R;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

public class BasicDataFragment extends Fragment{
	/** Tag for logging*/
	private final String TAG = "BasicDataFragment";

	private EditText mEditTextExerciseNameEnglish;
	private EditText mEditTextExerciseNameGerman;

	private EditText mEditTextExerciseDescription;

	
	public BasicDataFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_create_exercise_basic_data, container, false);

		mEditTextExerciseDescription = (EditText) layout.findViewById(R.id.edittext_description);
		mEditTextExerciseNameEnglish = (EditText) layout.findViewById(R.id.edittext_exercise_name_english);
		mEditTextExerciseNameGerman = (EditText) layout.findViewById(R.id.edittext_exercise_name_german);
		
		mEditTextExerciseNameGerman.addTextChangedListener(new ExerciseNameTextWatcher(mEditTextExerciseNameGerman));
		mEditTextExerciseNameEnglish.addTextChangedListener(new ExerciseNameTextWatcher(mEditTextExerciseNameEnglish));

		
		return layout;
	}
	
	
	
	public String getExerciseNameEnglish(){
		return mEditTextExerciseNameEnglish.getText().toString();
	}
	
	public String getExerciseNameGerman(){
		return mEditTextExerciseNameGerman.getText().toString();
	}

	public String getExerciseDescription(){
		return mEditTextExerciseDescription.getText().toString();
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