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

package de.skubware.opentraining.activity.start_training;


import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_workout.ExerciseDetailOnGestureListener;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;

/**
 * A fragment representing a single Exercise detail screen. This fragment is
 * either contained in a {@link FExListActivity} in two-pane mode (on tablets)
 * or a {@link FExDetailActivity} on handsets.
 */
public class FExDetailFragment extends Fragment implements DialogFragmentAddEntry.Callbacks {
	/** Tag for logging */
	public static final String TAG = "FExDetailFragment";

	public static final String ARG_FEX = "f_ex";

	public static final String ARG_WORKOUT = "workout";

	/**
	 * The {@link FitnessExercise} this fragment is presenting.
	 */
	private FitnessExercise mExercise;
	
	/** Currently edited TrainingEntry */
	private TrainingEntry mTrainingEntry;

	/** Currently shown {@link Workout}. */
	private Workout mWorkout;

	private GestureDetector mGestureScanner;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public FExDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);

		mExercise = (FitnessExercise) getArguments().getSerializable(ARG_FEX);
		mWorkout = (Workout) getArguments().getSerializable(ARG_WORKOUT);
		
		
		// select latest TrainingEntry
		if(mTrainingEntry == null){
			List<TrainingEntry> entryList = mExercise.getTrainingEntryList();
			TrainingEntry latestEntry = entryList.get(entryList.size() - 1);
			mTrainingEntry = latestEntry;
		}
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_fex_detail, container, false);

		// show the current exercise
		ImageView imageview = (ImageView) rootView.findViewById(R.id.imageview);

		// set gesture detector
		this.mGestureScanner = new GestureDetector(this.getActivity(), new ExerciseDetailOnGestureListener(this, imageview, mExercise));

		// Images
		if (!mExercise.getImagePaths().isEmpty()) {
			DataHelper data = new DataHelper(getActivity());
			imageview.setImageDrawable(data.getDrawable(mExercise.getImagePaths().get(0).toString()));
		} else {
			imageview.setImageResource(R.drawable.ic_launcher);
		}

		imageview.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureScanner.onTouchEvent(event);
			}
		});
		
		
		// set adapter
		ListView listView = (ListView) rootView.findViewById(R.id.list);
		final TrainingEntryListAdapter mAdapter = new TrainingEntryListAdapter((FragmentActivity) getActivity(), mExercise,  mTrainingEntry);
		listView.setAdapter(mAdapter);
		
		
		SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
				listView,
				new SwipeDismissListViewTouchListener.OnDismissCallback() {
					@Override
					public void onDismiss(ListView listView,
							int[] reverseSortedPositions) {
						for (int position : reverseSortedPositions) {
							mAdapter.remove(position);
						}
						mAdapter.notifyDataSetChanged();
					}
				});
		listView.setOnTouchListener(touchListener);
		// Setting this scroll listener is required to ensure that during
		// ListView scrolling,
		// we don't look for swipes.
		listView.setOnScrollListener(touchListener.makeScrollListener());
	

		return rootView;
	}

	@Override
	public void onStart() {
		super.onStart();
		updateTrainingEntries();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.fex_detail_menu, menu);

		// configure menu_item_add_entry
		MenuItem menu_item_add_entry = (MenuItem) menu.findItem(R.id.menu_item_add_entry);
		menu_item_add_entry.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				showDialog();
				return true;
			}
		});
		
		
		// configure menu_item_license_info
		MenuItem menu_item_license_info = (MenuItem) menu.findItem(R.id.menu_item_license_info);
		menu_item_license_info.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.license_info));
				
				String license = "";

				if (mExercise.getImageLicenseMap().values().iterator().hasNext()) {
					license = mExercise.getImageLicenseMap().values().iterator().next().toString();
				} else {
					license = getString(R.string.no_license_available);
				}
				
				builder.setMessage(license);
				builder.create().show();

				return true;
			}
		});
		

		
		// configure menu_item_license_info
		MenuItem menu_item_history = (MenuItem) menu.findItem(R.id.menu_item_history);
		menu_item_history.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager()
						.findFragmentByTag("dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);

				// Create and show the dialog.
				DialogFragment newFragment = DialogFragmentHistory
						.newInstance(mExercise);
				newFragment.show(ft, "dialog");

				return true;
			}
		});
		
		
		// configure menu_item_description
		MenuItem menu_item_description = (MenuItem) menu.findItem(R.id.menu_item_description);
		menu_item_description.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if(mExercise.getDescription() == null || mExercise.getDescription().equals("")){
					Toast.makeText(getActivity(), getString(R.string.no_description_available), Toast.LENGTH_LONG).show();
					return true;
				}
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setTitle(getString(R.string.description));
				
				
				builder.setMessage(Html.fromHtml(mExercise.getDescription()));
				builder.create().show();

				return true;
			}
		});
	}

	/** Shows DialogFragmentAddEntry. */
	private void showDialog() {
		showDialog(null, -1);
	}


	
	/**
	 * Shows DialogFragmentAddEntry with the given {@link FSet}.
	 * 
	 * @param set
	 *            The FSet to edit. If it is null a new FSet will be added to
	 *            the TrainingEntry.
	 *            
	 * @see DialogFragmentAddEntry#newInstance(FitnessExercise, FSet)           
	 */
	private void showDialog(FSet set, int setPosition) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		DialogFragment newFragment = DialogFragmentAddEntry.newInstance(mExercise, set, setPosition, mTrainingEntry);
		newFragment.show(ft, "dialog");
	}
	

	@Override
	public void onEntryEdited(FitnessExercise fitnessExercise) {
		Log.d(TAG, "onEntryEdited()");

		mWorkout.updateFitnessExercise(fitnessExercise);

		DataProvider dataProvider = new DataProvider(getActivity());
		dataProvider.saveWorkoutAsync(mWorkout);

		FExListFragment fragment = (FExListFragment) getFragmentManager().findFragmentById(R.id.exercise_list);
		if (fragment != null) {
			Log.d(TAG, "updating FExListFragment");
			// either notify list fragment if it's there (on tablets)
			fragment.setWorkout(mWorkout);
		} else {
			Log.d(TAG, "setting Intent for FExListActivity");
			// or return intent if list fragment is not visible (on small
			// screens)
			Intent i = new Intent();
			i.putExtra(FExListActivity.ARG_WORKOUT, mWorkout);
			this.getActivity().setResult(Activity.RESULT_OK, i);
		}

		mExercise = fitnessExercise;
		updateTrainingEntries();
	}

	/**
	 * Updates the displayed {@link TrainingEntry}. That means the text of all
	 * {@link FSet} is updated.
	 */
	private void updateTrainingEntries() {
		if(mTrainingEntry == null){
			// select last TrainingEntry
			List<TrainingEntry> entryList = mExercise.getTrainingEntryList();
			Collections.sort(entryList);
			Log.d(TAG, "entryList.size()= " + entryList.size());
			mTrainingEntry = entryList.get(entryList.size() - 1);
		}

		
		ListView list = (ListView) getActivity().findViewById(R.id.list);
		TrainingEntryListAdapter adapter = new TrainingEntryListAdapter((FragmentActivity) getActivity(), mExercise,  mTrainingEntry);
		list.setAdapter(adapter);
	}

}
