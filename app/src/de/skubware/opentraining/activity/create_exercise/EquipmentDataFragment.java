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

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.start_training.SwipeDismissListViewTouchListener;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

public class EquipmentDataFragment extends SpinnerDataFragment<SportsEquipment>{
	
	public EquipmentDataFragment(){
		super(R.layout.fragment_create_exercise_equipment_data);
		mSpinnerDataList = (new DataProvider(getActivity()).getEquipment());
	}
}

/*public class EquipmentDataFragment extends Fragment implements OnItemSelectedListener{
	private Spinner mEquipmentSpinner;

	private ListView mEquipmentListView;
	private ArrayAdapter<SportsEquipment> mListAdapter;
	private List<SportsEquipment> mEquipmentList = new ArrayList<SportsEquipment>();
	
	public EquipmentDataFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View layout = inflater.inflate(R.layout.fragment_create_exercise_equipment_data, container, false);
		
		IDataProvider dataProvider = new DataProvider(getActivity());

		mEquipmentSpinner = (Spinner) layout.findViewById(R.id.spinner_equipment);
		ArrayAdapter<SportsEquipment> eqadapter = new ArrayAdapter<SportsEquipment>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, dataProvider.getEquipment());
		mEquipmentSpinner.setAdapter(eqadapter);
		// if you dont post a runnable, the first item will be added to the mListAdapter on activity start
		mEquipmentSpinner.post(new Runnable() {
			public void run() {
				mEquipmentSpinner
						.setOnItemSelectedListener(EquipmentDataFragment.this);
				;
			}
		});
		
		mEquipmentListView = (ListView) layout.findViewById(R.id.listview_ex_equipment);
		mListAdapter = new ArrayAdapter<SportsEquipment>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, mEquipmentList);
		mEquipmentListView.setAdapter(mListAdapter);
		
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				mEquipmentListView,
				new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							mListAdapter.remove((SportsEquipment)(mListAdapter.getItem(position)));
						}
						mListAdapter.notifyDataSetChanged();
					}
				});
		mEquipmentListView.setOnTouchListener(touchListener);			
		
		
		return layout;
	}
	
	
	public List<SportsEquipment> getSportsEquipment(){
		return mEquipmentList;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		SportsEquipment selectedItem = (SportsEquipment) mEquipmentSpinner.getItemAtPosition(position);
		if(mEquipmentList.contains(selectedItem)){
			Toast.makeText(getActivity(), getString(R.string.equipment_already_in_list), Toast.LENGTH_LONG).show();
			return;
		}
		
		mListAdapter.add(selectedItem);
		((CreateExerciseActivity) getActivity()).swipeToDismissAdvise();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
}*/