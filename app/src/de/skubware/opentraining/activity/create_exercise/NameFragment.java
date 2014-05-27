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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.start_training.SwipeDismissListViewTouchListener;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.Translatable;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

public class NameFragment extends SpinnerDataFragment<NameFragment.NameTranslation>{
	/** Tag for logging*/
	private final String TAG = "NameFragment";

	private HashMap<String,String> mLanguageCodeMap;
	
	private EditText mEditTextExerciseName;
	
	public NameFragment() {
		super(R.layout.fragment_create_exercise_name);

		// set up language c spinnner
		mLanguageCodeMap = new HashMap<String,String>();
			Set<String> localeStringSet = new TreeSet<String>();
			for(Locale l:Locale.getAvailableLocales()){
				if(localeStringSet.add(l.getDisplayLanguage())){
					mLanguageCodeMap.put(l.getDisplayLanguage(), l.getLanguage());
				}
			}
			
		mSpinnerDataList = new ArrayList<String>(localeStringSet);
	}

	
	@Override
	public void onStart(){
		super.onStart();
		// select user language
		for(int i = 0; i<mSpinnerDataList.size(); i++){
			Object o = mSpinnerDataList.get(i);
			if(Locale.getDefault().getDisplayLanguage().equals(o)){
				mSpinner.setSelection(i);
				break;
			}			
		}
		
	}
	
	
	@Override
	protected String checkObjectConstraints(int position) {
		
		if(mEditTextExerciseName.getText().toString().equals("")){
			return getActivity().getString(R.string.exercise_name_empty);
		}
		
		// two or more names for the same language are ok (will be saved as alternative name)
		
		return null;
	}
	
	
	@Override
	protected NameTranslation buildObject(int position) {
		((CreateExerciseActivity) getActivity()).swipeToDismissAdvise();
		// get locale
		Locale locale = new Locale(mLanguageCodeMap.get((String)mSpinner.getItemAtPosition(position)));
		
		return new NameTranslation(locale, mEditTextExerciseName.getText().toString());
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View layout = super.onCreateView(inflater, container, savedInstanceState);

		mEditTextExerciseName = (EditText) layout.findViewById(R.id.edittext_exercise_name);
		mEditTextExerciseName.addTextChangedListener(new ExerciseNameTextWatcher(mEditTextExerciseName));

		
		ImageButton buttonAddName = (ImageButton) layout.findViewById(R.id.button_add_name);
		buttonAddName.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				addObject(mSpinner.getSelectedItemPosition());
			}
		});
		
		return layout;
	}
	

	
	public Map<Locale, String> getTranslationMap(){
		Map<Locale, String> translationMap = new HashMap<Locale, String>();
		for(NameTranslation t:this.getChosenObjects()){
			translationMap.put(t.mLocale, t.mName);
		}
		return translationMap;
	}



	
	private class ExerciseNameTextWatcher implements TextWatcher {
	    private EditText mEditText;
		IDataProvider mDataProvider = new DataProvider(getActivity());

	    
	    public ExerciseNameTextWatcher(EditText e) { 
	        mEditText = e;
	    }

	    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	    public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(mDataProvider.getExerciseByName(s.toString()) != null ){
				mEditText.setError(getString(R.string.name_already_used));	
			}else{
				mEditText.setError(null);
			}
	    }

	    public void afterTextChanged(Editable s) { }
	}
	
	
	
	
	public static class NameTranslation extends Translatable{
		private static final long serialVersionUID = 1L;
		
		Locale mLocale;
		String mName;
		NameTranslation(Locale locale, String name){
			super(locale, wrapNameInList(name));
			mLocale = locale;
			mName = name;
		}
		
		private static List<String> wrapNameInList(String name){
			List<String> list = new ArrayList<String>();
			list.add(name);
			return list;
		}
		
		@Override
		public String toString(){
			return super.toString() + " ("+ mLocale.getDisplayName() +")";
		}

	}


	// disable onItemSelected listener of spinner
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
	}
	
}