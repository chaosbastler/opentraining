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

import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.Translatable;

/**
 * Another abstraction level between real fragments and {@link SimpleDataFragment}.
 * Used for choosing a objects that extend {@link Translatable}s, e.g. {@link Muscle} or {@link SportsEquipment}.
 * 
 * Contract:
 *   - Spinner with the ID "spinner"
 *   - remember to obey the contract of {@link SimpleDataFragment}
 */
@SuppressWarnings("unchecked")
public abstract class SpinnerDataFragment<T extends Translatable> extends SimpleDataFragment<T> implements OnItemSelectedListener{

	protected ArrayAdapter<T> mSpinnerAdapter;
	protected CustomSpinner mSpinner;
	
	protected List mSpinnerDataList;
	
	protected int mLayoutID;

	
	/**
	 * Constructor.
	 * 
	 * @param layoutID
	 *            the layout of the fragment. Has to contain Spinner with id
	 *            "spinner" and listview with id "listview".
	 */
	public SpinnerDataFragment(int layoutID){
		mLayoutID = layoutID;
	}

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout = inflater.inflate(mLayoutID, container, false);
	
		if(mSpinnerDataList == null)
			throw new AssertionError("Sub class of SpinnerDataFragment<T> did not set mObjectList.");
		
		ArrayAdapter<T> mSpinnerAdapter = new ArrayAdapter<T>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, mSpinnerDataList);
	
		
		mSpinner = (CustomSpinner) layout.findViewById(R.id.spinner);
		mSpinner.setAdapter(mSpinnerAdapter);

		// if you dont post a runnable, the first item will be added to the mListAdapter on activity start
		mSpinner.post(new Runnable() {
			public void run() {
				mSpinner.setOnItemSelectedEvenIfUnchangedListener(SpinnerDataFragment.this);
			}
		});

		
		return layout;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		addObject(position);
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	protected String checkObjectConstraints(int position) {
		Object selectedItem = mSpinner.getItemAtPosition(position);
		if(mObjectList.contains(selectedItem)){
			return getActivity().getString(R.string.object_already_in_list, selectedItem.toString());
		}
		return null;
	}

	@Override
	protected T buildObject(int position) {
		((CreateExerciseActivity) getActivity()).swipeToDismissAdvise();
		return (T) mSpinner.getItemAtPosition(position);
	}

}