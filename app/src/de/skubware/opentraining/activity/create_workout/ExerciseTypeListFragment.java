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

package de.skubware.opentraining.activity.create_workout;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ListFragment;
import android.support.v7.widget.SearchView.OnQueryTextListener;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A list fragment representing a list of ExerciseTypes. This fragment also
 * supports tablet devices by allowing list items to be given an 'activated'
 * state upon selection. This helps indicate which item is currently being
 * viewed in a {@link ExerciseTypeDetailFragment}.
 * <p>
 * Activities containing this fragment MUST implement the {@link Callbacks}
 * interface.
 */
public class ExerciseTypeListFragment extends ListFragment implements OnQueryTextListener {
	/** Tag for logging */
	public static final String TAG = "ExerciseTypeListFragment";

	/** Currently display exercises */
	private List<ExerciseType> mExerciseList;

	/** Last query. */
	private String mSearchQuery = "";
	/** 
	 * The serialization (saved instance state) Bundle key representing the
	 * last query
	 */	
	private static final String STATE_QUERY = "state_query";
	

	private Integer mScrollIndex = null;
	private Integer mScrollTop = null;
	/** 
	 * The serialization (saved instance state) Bundle key representing the
	 * scroll index (for restoring scroll position)
	 */	
	private static final String STATE_SCROLL_INDEX = "state_scroll_index";
	/** 
	 * The serialization (saved instance state) Bundle key representing the
	 * scroll top (for restoring scroll position)
	 */	
	private static final String STATE_SCROLL_TOP = "state_scroll_top";
	
	/**
	 * Reference for reacting to preference changes (list of exercises will be
	 * updated if the preferences change)
	 */
	private OnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener;

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * activated item position. Only used on tablets.
	 */
	private static final String STATE_ACTIVATED_POSITION = "activated_position";

	/**
	 * The fragment's current callback object, which is notified of list item
	 * clicks.
	 */
	private Callbacks mCallbacks = sDummyCallbacks;

	/**
	 * The current activated item position. Only used on tablets.
	 */
	private int mActivatedPosition = ListView.INVALID_POSITION;
	
	public static final String PREF_KEY_SHOW_DEFAULT_EXERCISES = "PREF_KEY_SHOW_DEFAULT_EXERCISES";
	public static final String PREF_KEY_SHOW_SYNCED_EXERCISES = "PREF_KEY_SHOW_SYNCED_EXERCISES";
	public static final String PREF_KEY_SHOW_CUSTOM_EXERCISES = "PREF_KEY_SHOW_CUSTOM_EXERCISES";

	
	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of item
	 * selections.
	 */
	public interface Callbacks {
		/**
		 * Callback for when an item has been selected.
		 */
		public void onItemSelected(ExerciseType ex);
	}

	/**
	 * A dummy implementation of the {@link Callbacks} interface that does
	 * nothing. Used only when this fragment is not attached to an activity.
	 */
	private static Callbacks sDummyCallbacks = new Callbacks() {
		@Override
		public void onItemSelected(ExerciseType ex) {
		}
	};

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ExerciseTypeListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		IDataProvider dataProvider = new DataProvider(getActivity());
		mExerciseList = dataProvider.getExercises();

		setListAdapter(new ExerciseTypeListAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1,
				mExerciseList));

		
		//SelectMuscleDialog muscleDialog = new SelectMuscleDialog(getActivity());
		//muscleDialog.show();
	}

	@Override
	public void onStart(){
		super.onStart();
		
		// register for changed settings
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		mOnSharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
			@Override
			public void onSharedPreferenceChanged(SharedPreferences pref, String key) {
				Log.v(TAG, "Preference changed, will update shown exercises");
				filterExercises();
			}

		};
		sharedPrefs.registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);

		
		filterExercises();
	}

	@Override
	public void onStop(){
		super.onStop();
		// unregister shared preference listener
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		sharedPrefs.unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
	}
	
	private void filterExercises(){
		// the order of the calls is important
		filterExercisesForMusclesAndEquipment();
		filterExercisesForSearchQuery();
		filterExercisesForExerciseSource();
	}
	
	/**
	 * Filters the list of exercises for muscles and equipment.
	 */
	private void filterExercisesForMusclesAndEquipment() {
		IDataProvider dataProvider = new DataProvider(getActivity());
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

		List<Muscle> acceptedMuscles = new ArrayList<Muscle>();
		for (Muscle m : dataProvider.getMuscles()) {
			if (sharedPrefs.getBoolean(m.toString(), true)) {
				acceptedMuscles.add(m);
			}
		}

		List<SportsEquipment> acceptedEquipment = new ArrayList<SportsEquipment>();
		for (SportsEquipment e : dataProvider.getEquipment()) {
			if (sharedPrefs.getBoolean(e.toString(), true)) {
				acceptedEquipment.add(e);
			}
		}

		mExerciseList = dataProvider.getExercises();
		for (ExerciseType ex : dataProvider.getExercises()) {
			boolean accepted = false;
			for (Muscle m : acceptedMuscles) {
				if (ex.getActivatedMuscles().contains(m))
					accepted = true;
			}
			
			if(ex.getActivatedMuscles().isEmpty())
				accepted = true;

			if (!accepted) {
				// Log.d(TAG, "Exercise: " + ex.toString() + " will not be shown. Muscles do not fit.");
				mExerciseList.remove(ex);
				continue;
			}

			if (!acceptedEquipment.containsAll(ex.getRequiredEquipment())) {
				// Log.d(TAG, "Exercise: " + ex.toString() + " will not be shown. Equipment does not fit");
				mExerciseList.remove(ex);
				continue;
			}
		}

		setListAdapter(new ExerciseTypeListAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1,
				mExerciseList));	
	}

	/**
	 * Filters the list of exercises for the search query. 
	 * 
	 * Do not forget to call {@link #filterExercisesForMusclesAndEquipment()} 
	 * before.
	 * 
	 */
	private void filterExercisesForSearchQuery() {
		Log.d(TAG, "filterExercisesForSearchQuery() mSearchEquery=" + mSearchQuery);

		if (mSearchQuery == null)
			mSearchQuery = "";

		// quit if the user did not search for anything
		if (mSearchQuery.equals("") || mSearchQuery.replaceAll(" ", "").equals(""))
			return;

		for (ExerciseType ex : new ArrayList<ExerciseType>(mExerciseList)) {
			boolean accepted = false;

			for (String name : ex.getAlternativeNames()) {
				name = name.toLowerCase(Locale.GERMANY);
				if (name.contains(mSearchQuery.toLowerCase(Locale.GERMANY))) {
					accepted = true;
					continue;
				}
			}

			if (!accepted) {
				mExerciseList.remove(ex);
			}
		}

		setListAdapter(new ExerciseTypeListAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1,
				mExerciseList));	
	}
	
	/**
	 * Filters the list of exercises for their {@link ExerciseSource}}
	 *  
	 */
	private void filterExercisesForExerciseSource() {		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
		
		boolean showDefaultExercises = sharedPrefs.getBoolean(PREF_KEY_SHOW_DEFAULT_EXERCISES, true);
		boolean showSyncedExercises = sharedPrefs.getBoolean(PREF_KEY_SHOW_SYNCED_EXERCISES, true);
		boolean showCustomExercises = sharedPrefs.getBoolean(PREF_KEY_SHOW_CUSTOM_EXERCISES, true);

		for (ExerciseType ex : new ArrayList<ExerciseType>(mExerciseList)) {
			switch(ex.getExerciseSource()){
				case DEFAULT:
					if(!showDefaultExercises)
						mExerciseList.remove(ex);
					break;
				case SYNCED:
					if(!showSyncedExercises)
						mExerciseList.remove(ex);
					break;
				case CUSTOM:
					if(!showCustomExercises)
						mExerciseList.remove(ex);
					break;
			}
			
		}

		setListAdapter(new ExerciseTypeListAdapter(getActivity(), android.R.layout.simple_list_item_single_choice, android.R.id.text1,
				mExerciseList));	
	}
	
	
	


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		// Restore the previously serialized values
		if (savedInstanceState != null) {
			// restore activated item position
			if(savedInstanceState.containsKey(STATE_ACTIVATED_POSITION))
				setActivatedPosition(savedInstanceState.getInt(STATE_ACTIVATED_POSITION));

			// restore search query
			if(savedInstanceState.containsKey(STATE_QUERY)){
				mSearchQuery = savedInstanceState.getString(STATE_QUERY);
			}
			filterExercises();

			
			// restore scroll state
			if(savedInstanceState.containsKey(STATE_SCROLL_INDEX) && savedInstanceState.containsKey(STATE_SCROLL_TOP)){
				mScrollIndex = savedInstanceState.getInt(STATE_SCROLL_INDEX);
				mScrollTop = savedInstanceState.getInt(STATE_SCROLL_TOP);
				restoreScrollState();
			}

		}
		
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		// Activities containing this fragment must implement its callbacks.
		if (!(activity instanceof Callbacks)) {
			throw new IllegalStateException("Activity must implement fragment's callbacks.");
		}

		mCallbacks = (Callbacks) activity;
	}

	@Override
	public void onDetach() {
		super.onDetach();

		// Reset the active callbacks interface to the dummy implementation.
		mCallbacks = sDummyCallbacks;
	}

	@Override
	public void onListItemClick(ListView listView, View view, int position, long id) {
		super.onListItemClick(listView, view, position, id);

		// Notify the active callbacks interface (the activity, if the
		// fragment is attached to one) that an item has been selected.
		ExerciseType exercise = (ExerciseType) listView.getAdapter().getItem(position);
		mCallbacks.onItemSelected(exercise);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (mActivatedPosition != ListView.INVALID_POSITION) {
			// Serialize and persist the activated item position.
			outState.putInt(STATE_ACTIVATED_POSITION, mScrollIndex);
		}
		
		// save search query
		outState.putString(STATE_QUERY, mSearchQuery);
		
		// save scroll state
		saveScrollState();
		outState.putInt(STATE_SCROLL_INDEX, mScrollIndex);
		outState.putInt(STATE_SCROLL_TOP, mScrollTop);
	}

	
	@Override
	public void onPause(){
		super.onPause();
		saveScrollState();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		filterExercises();
		restoreScrollState();
	}
	
	/**
	 * Saves the 'scroll state' (which items of the ListView are visible).
	 */
	private void saveScrollState(){
		// save index and top position to be able to 
		// restore it later
		mScrollIndex = getListView().getFirstVisiblePosition();
		View v =  getListView().getChildAt(0);
		mScrollTop = (v == null) ? 0 : v.getTop();
	}
	
	/**
	 * Restores the 'scroll state' (which items of the ListView are visible).
	 */
	private void restoreScrollState(){
		// restore selection if possible
		if(mScrollIndex != null && mScrollTop != null)
			getListView().setSelectionFromTop(mScrollIndex, mScrollTop);
	}
	

	/**
	 * Turns on activate-on-click mode. When this mode is on, list items will be
	 * given the 'activated' state when touched.
	 */
	public void setActivateOnItemClick(boolean activateOnItemClick) {
		// When setting CHOICE_MODE_SINGLE, ListView will automatically
		// give items the 'activated' state when touched.
		getListView().setChoiceMode(activateOnItemClick ? ListView.CHOICE_MODE_SINGLE : ListView.CHOICE_MODE_NONE);
	}

	private void setActivatedPosition(int position) {
		if (position == ListView.INVALID_POSITION) {
			getListView().setItemChecked(mActivatedPosition, false);
		} else {
			getListView().setItemChecked(position, true);
		}

		mActivatedPosition = position;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		onQueryTextSubmit(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		Log.d(TAG, "onQueryTextSubmit(" + query + ")");
		mSearchQuery = query;
		filterExercises();
		return false;
	}

	public void onExerciseDeleted(ExerciseType deletedExercise) {
		filterExercises();
	}

}
