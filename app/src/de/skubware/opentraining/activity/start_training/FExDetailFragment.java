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

package de.skubware.opentraining.activity.start_training;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_workout.ExerciseDetailOnGestureListener;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.basic.FSet.SetParameter;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A fragment representing a single Exercise detail screen. This fragment is
 * either contained in a {@link FExListActivity} in two-pane mode (on tablets)
 * or a {@link FExDetailActivity} on handsets.
 */
public class FExDetailFragment extends SherlockFragment implements DialogFragmentAddEntry.Callbacks {
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
		
		
		ListView list = (ListView) rootView.findViewById(R.id.list);
		
		// Getting adapter by passing xml data ArrayList
		TrainingEntryListAdapter adapter = new TrainingEntryListAdapter(getActivity());
		list.setAdapter(adapter);
		
		
		
		/*TableLayout table = (TableLayout) rootView.findViewById(R.id.table_training_entry);
		boolean odd = false;
		
		TrainingEntry lastTrainingEntry = mExercise.getTrainingEntryList().get(mExercise.getTrainingEntryList().size()-1);

			
		for (FSet set : lastTrainingEntry.getFSetList()) {
			TableRow row;
			if (odd) {
				row = (TableRow) inflater.inflate(R.layout.row_type_3, null);
			} else {
				row = (TableRow) inflater.inflate(R.layout.row_type_4, null);
			}
			odd = !odd;


			for (SetParameter parameter : set.getSetParameters()) {
				TextView text_view_duration = (TextView) row
						.findViewById(R.id.text_view_duration);
				TextView text_view_rep = (TextView) row
						.findViewById(R.id.text_view_rep);
				TextView text_view_weigh = (TextView) row
						.findViewById(R.id.text_view_weigh);

				if (parameter instanceof SetParameter.Duration) {
					text_view_duration.setText(parameter.toString());
				}

				if (parameter instanceof SetParameter.Repetition) {
					text_view_rep.setText(parameter.toString());
				}

				if (parameter instanceof SetParameter.Weight) {
					text_view_weigh.setText(parameter.toString());
				}
			}

			table.addView(row);
		}

	
		
		
		
		
		//EditText editText = (EditText) rootView.findViewById(R.id.edittext_current_entry);
		table.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				List<FSet> fSetList = mTrainingEntry.getFSetList();
				// create new SubEntry if there is none
				if (fSetList.isEmpty()) {
					showDialog();
					return;
				}

				// edit existing SubEntries if there are some
				AlertDialog.Builder builder_subentry_chooser = new AlertDialog.Builder(getActivity());
				builder_subentry_chooser.setTitle(getString(R.string.choose_subentry));

				final ArrayAdapter<FSet> adapter = new ArrayAdapter<FSet>(getActivity(),
						android.R.layout.select_dialog_singlechoice, fSetList);

				builder_subentry_chooser.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						FSet choosenSubEntry = adapter.getItem(item);
						showDialog(choosenSubEntry);
					}

				});
				builder_subentry_chooser.create().show();

			}
		});*/

		
		
		/*ImageButton buttonTrainingEntryTable = (ImageButton) rootView
				.findViewById(R.id.button_training_entry_table);
		buttonTrainingEntryTable.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				FragmentTransaction ft = getFragmentManager()
						.beginTransaction();
				Fragment prev = getFragmentManager()
						.findFragmentByTag("dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);

				// Create and show the dialog.
				DialogFragment newFragment = DialogFragmentTrainingEntryTable
						.newInstance(mExercise);
				newFragment.show(ft, "dialog");
			}
		});*/

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
					license = mExercise.getImageLicenseMap().values().iterator().next();
				} else {
					license = getString(R.string.no_license_available);
				}
				
				builder.setMessage(license);
				builder.create().show();

				return true;
			}
		});
		
		// configure menu_item_other_training_entry
		MenuItem menu_item_other_training_entry = (MenuItem) menu.findItem(R.id.menu_item_other_training_entry);
		menu_item_other_training_entry.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@SuppressLint("SimpleDateFormat")
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder_entry_chooser = new AlertDialog.Builder(getActivity());
				builder_entry_chooser.setTitle(getString(R.string.choose_training));

				final List<TrainingEntry> trainingEntryList = mExercise.getTrainingEntryList();
				// don't show list if there are no other TrainingEntrys
				if (trainingEntryList.size() < 2) {
					Toast.makeText(getActivity(), getString(R.string.no_other_training_entries), Toast.LENGTH_LONG).show();
					return true;
				}

				SimpleDateFormat dateFormat = new SimpleDateFormat("EEEEEEEEEE,  dd.MM.yyyy");
				// create a list with custom strings, otherwise the default
				// toString() method of TrainingEntry would be used
				List<String> trainingEntryStringList = new ArrayList<String>();
				for (TrainingEntry entry : trainingEntryList) {
					// don't show current entry
					if (entry.equals(mTrainingEntry)) {
						continue;
					}
					String dateString = dateFormat.format(entry.getDate());
					trainingEntryStringList.add(dateString);
				}

				final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.select_dialog_singlechoice,
						trainingEntryStringList);

				builder_entry_chooser.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						TrainingEntry choosenEntry = trainingEntryList.get(item);
						mTrainingEntry = choosenEntry;
						updateTrainingEntries();
					}

				});
				builder_entry_chooser.create().show();
				return true;
			}
		});
		
		
	}

	/** Shows DialogFragmentAddEntry. */
	private void showDialog() {
		showDialog(null);
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
	private void showDialog(FSet set) {

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		Fragment prev = getFragmentManager().findFragmentByTag("dialog");
		if (prev != null) {
			ft.remove(prev);
		}
		ft.addToBackStack(null);

		// Create and show the dialog.
		DialogFragment newFragment = DialogFragmentAddEntry.newInstance(mExercise, set, mTrainingEntry);
		newFragment.show(ft, "dialog");
	}
	

	@Override
	public void onEntryEdited(FitnessExercise fitnessExercise) {
		Log.d(TAG, "onEntryEdited()");

		mWorkout.updateFitnessExercise(fitnessExercise);

		IDataProvider dataProvider = new DataProvider(getActivity());
		dataProvider.saveWorkout(mWorkout);

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
		
	}

}
