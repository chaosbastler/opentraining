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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.start_training.SwipeDismissListViewTouchListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;


/**
 * An abstraction to reuse code for fragments that nearly do the same.
 * 
 * Sub class has to fulfill this contract:
 *   - there must be a listview with the id "listview"
 *   - sub class overwrites {@link #checkObjectConstraints(int)} and {@link #buildObject(int)}
 *   - sub class calls {@link #addObject(int)} in the listener of e.g. an add-item-button
 *
 * This class provides:
 *   - chosen items will be added to the listview
 *   - the listview supports the SwipeToDismiss-Action
 *   - there's a getter for the chosen items
 *   - template methods pattern for checking the object constraints and building the object
 *   - error message will be shown when object-constraint-check fails
 * 
 * 
 * @param <T> The type of object that should be stored in the ListView and be returned by {@link #getChosenObjects()}
 */
public abstract class SimpleDataFragment<T extends Serializable> extends Fragment {
	protected ListView mListView;
	protected ArrayAdapter<T> mListAdapter;
	protected ArrayList<T> mObjectList = new ArrayList<T>(); // ArrayList implements Serializable

	private final static String KEY_LIST_ADAPTER_DATA = "KEY_LIST_ADAPTER_DATA";
	
	
	/** 
	 * Restore the instance state, e.g. after rotation.
	 */
	public void onCreate (Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);

	    if (savedInstanceState != null && savedInstanceState.containsKey(KEY_LIST_ADAPTER_DATA)) {
	    	mObjectList = (ArrayList<T>) savedInstanceState.getSerializable(KEY_LIST_ADAPTER_DATA);
	    }
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle savedInstance){
		mListView = (ListView) view.findViewById(R.id.listview);
	}

	@Override
	public void onStart(){
		super.onStart();
		mListAdapter = new ArrayAdapter<T>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, mObjectList);
		mListView.setAdapter(mListAdapter);
		
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				mListView,
				new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							if(mListAdapter.getCount() > position)
								mListAdapter.remove((T)(mListAdapter.getItem(position)));
						}
						mListAdapter.notifyDataSetChanged();
					}
				});
		mListView.setOnTouchListener(touchListener);			
	}
	
	/** 
	 * Save instance state, e.g. for rotation.
	 */
	public void onSaveInstanceState(Bundle savedState) {
	    super.onSaveInstanceState(savedState);
	    savedState.putSerializable(KEY_LIST_ADAPTER_DATA, mObjectList);
	}

	
	/**
	 * Adds the selected item to the list view if constraints are fulfilled.
	 * Uses template methods {@link #checkObjectConstraints(int)} and {@link #buildObject(int)} which have to be overridden by subclasses.
	 * If object validation of {@link #checkObjectConstraints(int)} fails an error message will be shown.
	 * 
	 * @param position The position of the selected item, will be passed on to {@link #checkObjectConstraints(int)} and {@link #buildObject(int)}
	 */
	protected void addObject(int position){
		String errorMsg = checkObjectConstraints(position);
		if(errorMsg == null){
			T object = buildObject(position);
			mListAdapter.add(object);
		}else{
			Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
		}
		
	}
	
	/**
	 * Checks the object that should be added to the listview
	 * 
	 * @param position The position of the selected item
	 * @return null if no error occurred, translated error message otherwise
	 */
	protected abstract String checkObjectConstraints(int position);
	
	/**
	 * Creates the object that should be added to the listview
	 * 
	 * @param position The position of the selected item
	 * @return The created Item
	 */
	protected abstract T buildObject(int position);

	public List<T> getChosenObjects() {
		return mObjectList;
	}



}