/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
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

package de.skubware.opentraining.activity.manage_workouts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_workout.ExerciseTypeListActivity;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import de.skubware.opentraining.db.parser.XMLSaver;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A fragment representing a single Workout detail screen. This fragment is
 * either contained in a {@link WorkoutListActivity} in two-pane mode (on
 * tablets) or a {@link WorkoutDetailActivity} on handsets.
 */
public class WorkoutDetailFragment extends Fragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_WORKOUT = "workout";

	private static final String TAG = "WorkoutDetailFragment";
	
	/**
	 * The {@link Workout} this fragment is presenting.
	 */
	private Workout mWorkout;
	

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public WorkoutDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setHasOptionsMenu(true);

		if (getArguments().containsKey(ARG_WORKOUT)) {
			mWorkout = (Workout) getArguments().getSerializable(ARG_WORKOUT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_workout_detail, container, false);

		// Show the dummy content as text in a TextView.
		if (mWorkout != null) {
			((TextView) rootView.findViewById(R.id.textview_workout_name)).setText(mWorkout.getName());

			ListView listview_exercises = (ListView) rootView.findViewById(R.id.listview_exercises);
			FitnessExercise[] arr = mWorkout.getFitnessExercises().toArray(new FitnessExercise[mWorkout.getFitnessExercises().size()]);
			ArrayAdapter<FitnessExercise> adapter = new ArrayAdapter<FitnessExercise>(getActivity(), android.R.layout.simple_list_item_2,
					android.R.id.text1, arr);

			listview_exercises.setAdapter(adapter);
		}

		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.workout_detail_menu, menu);

		// configure menu_item_rename_workout
		MenuItem menu_item_rename_workout = (MenuItem) menu.findItem(R.id.menu_item_rename_workout);
		menu_item_rename_workout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {

				FragmentTransaction ft = getFragmentManager().beginTransaction();
				Fragment prev = getFragmentManager().findFragmentByTag("dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);

				// Create and show the dialog.
				DialogFragment newFragment = RenameWorkoutDialogFragment.newInstance(mWorkout);
				newFragment.show(ft, "dialog");

				return true;
			}
		});

		// configure menu_item_delete_workout
		MenuItem menu_item_delete_workout = (MenuItem) menu.findItem(R.id.menu_item_delete_workout);
		menu_item_delete_workout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

				builder.setTitle(getString(R.string.really_delete));
				builder.setMessage(getString(R.string.really_delete_long));

				builder.setPositiveButton(getString(R.string.delete_workout), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int wich) {
						IDataProvider dataProvider = new DataProvider(getActivity());
						dataProvider.deleteWorkout(mWorkout);

						if (getActivity() instanceof WorkoutDetailActivity) {
							// request WorkoutListActivity to
							// finish too
							Intent i = new Intent();
							getActivity().setResult(WorkoutListActivity.REQUEST_EXIT, i);
						}

						// finish WorkoutListActivity
						getActivity().finish();

						startActivity(new Intent(getActivity(), WorkoutListActivity.class));

					}
				});
				builder.setNegativeButton(getString(R.string.cancel), new OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int wich) {
						dialog.dismiss();
					}
				});

				builder.create().show();

				return true;
			}
		});
		

		// configure menu_item_edit_workout
		MenuItem menu_item_edit_workout = (MenuItem) menu.findItem(R.id.menu_item_edit_workout);
		menu_item_edit_workout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				
				Intent editItent = new Intent(getActivity(), ExerciseTypeListActivity.class);
				editItent.putExtra(ExerciseTypeListActivity.ARG_WORKOUT, mWorkout);
				startActivity(editItent);
				
				// close the manage workout activities
				if(getActivity() instanceof WorkoutDetailActivity)
					getActivity().finishFromChild(getActivity());
				getActivity().finish();

				return true;
			}
		});
		
		
		// configure menu_item_share

		MenuItem menu_item_share = menu.findItem(R.id.menu_item_share);
		
		menu_item_share.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				
				builder.setTitle(getString(R.string.share_workout));
				builder.setMessage(getString(R.string.should_workout_history_be_included));
			    
				builder.setPositiveButton(getString(R.string.workout_with_history), new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent shareIntent = getShareIntent(mWorkout);
						startActivity(Intent.createChooser(shareIntent, "R.string.send_workout_to"));				
					}
				});
				
				
				builder.setNeutralButton(getString(R.string.workout_without_history), new OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Intent shareIntent = getShareIntent(mWorkout.getWorkoutWithoutHistory());
						startActivity(Intent.createChooser(shareIntent, getString(R.string.send_workout_to)));				
					}
				});
				
				builder.show();
				
				return false;
			}
		});
		

	   /* // Fetch and store ShareActionProvider
	    mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu_item_share);
	    
	    if (mShareActionProvider != null) {
	    	Intent shareIntent = new Intent();
	    	shareIntent.setAction(Intent.ACTION_SEND);
	    	
	    	// write workout (without history) to tmp file
	    	Workout w = mWorkout.getWorkoutWithoutHistory();
	    	File cacheDir = getActivity().getExternalCacheDir();
	    	XMLSaver.writeTrainingPlan(w, cacheDir);
	    	File tmpFile = new File(cacheDir + "/"  + w.getName() + ".xml");
	    	
	    	if(!tmpFile.exists()){
	    		Log.e(TAG, "Temporary Workout file does not exist");
	    	}
	    	
	    	// read file
	    	StringBuilder text = new StringBuilder();
	    	try {
	    	    BufferedReader br = new BufferedReader(new FileReader(tmpFile));
	    	    String line;

	    	    while ((line = br.readLine()) != null) {
	    	        text.append(line);
	    	        text.append('\n');
	    	    }
	    	    br.close();
	    	}
	    	catch(IOException e) {
	    		Log.e(TAG, "Could not read workout", e);
	    	}
	    	Log.e(TAG, "Finished reading file: " + text.toString());
	    	
	    	
	    	shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
	    	shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tmpFile));
	    	shareIntent.setType("text/plain");

	        mShareActionProvider.setShareIntent(shareIntent);
	    }*/


	}
	
	private Intent getShareIntent(Workout w){
		Intent shareIntent = new Intent();
    	shareIntent.setAction(Intent.ACTION_SEND);
    	
    	// write workout (without history) to tmp file
    	File cacheDir = getActivity().getExternalCacheDir();
    	XMLSaver.writeTrainingPlan(w, cacheDir);
    	File tmpFile = new File(cacheDir + "/"  + w.getName() + ".xml");
    	
    	if(!tmpFile.exists()){
    		Log.e(TAG, "Temporary Workout file does not exist");
    	}
    	
    	// read file
    	StringBuilder text = new StringBuilder();
    	try {
    	    BufferedReader br = new BufferedReader(new FileReader(tmpFile));
    	    String line;

    	    while ((line = br.readLine()) != null) {
    	        text.append(line);
    	        text.append('\n');
    	    }
    	    br.close();
    	}
    	catch(IOException e) {
    		Log.e(TAG, "Could not read workout", e);
    	}
    	Log.e(TAG, "Finished reading file: " + text.toString());
    	
    	
    	shareIntent.putExtra(Intent.EXTRA_TEXT, text.toString());
    	shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(tmpFile));
    	shareIntent.setType("text/plain");

    	return shareIntent;
	}

}
