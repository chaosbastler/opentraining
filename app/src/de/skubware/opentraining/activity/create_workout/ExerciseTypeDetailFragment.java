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


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import at.technikum.mti.fancycoverflow.FancyCoverFlow;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_workout.upload_exercise.UploadExerciseImagesAsyncTask;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A fragment representing a single ExerciseType detail screen. This fragment is
 * either contained in a {@link ExerciseTypeListActivity} in two-pane mode (on
 * tablets) or a {@link ExerciseTypeDetailActivity} on handsets.
 */
public class ExerciseTypeDetailFragment extends Fragment {
	/** Tag for logging */
	public static final String TAG = "ExerciseTypeDetailFragment";

	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_EXERCISE = "exercise";

	public static final String ARG_WORKOUT = "workout";

	/** Result-Code when the exercises changed (e.g. one has been deleted) */
	public static final int RESULT_EXERCISE_CHANGED = 93278734;
	
	/** Key for passing the exercise that has been deleted. */
	public static final String ARG_DELETED_EXERCISE = "delted_ex";
	
	/**
	 * The {@link ExerciseType} this fragment is presenting.
	 */
	private ExerciseType mExercise;
	private Workout mWorkout;

	private GestureDetector mGestureScanner;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public ExerciseTypeDetailFragment() {
	}

	/**
	 * A callback interface that all activities containing this fragment must
	 * implement. This mechanism allows activities to be notified of changes.
	 */
	public interface Callbacks {
		/**
		 * Callback for when the Workout has changed.
		 */
		public void onWorkoutChanged(Workout w);
		
		/** 
		 * Callback, when an exercise has been deleted. 
		 */
		public void onExerciseDeleted(ExerciseType deletedExercise);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);

		mExercise = (ExerciseType) getArguments().getSerializable(ExerciseTypeDetailFragment.ARG_EXERCISE);
		mWorkout = (Workout) getArguments().getSerializable(ExerciseTypeDetailFragment.ARG_WORKOUT);

		this.getActivity().setTitle(mExercise.getLocalizedName());
	}

	/** Saves the state of this Fragment, e.g. when screen orientation changed. */
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ExerciseTypeDetailFragment.ARG_EXERCISE, mExercise);
		outState.putSerializable(ExerciseTypeDetailFragment.ARG_WORKOUT, mWorkout);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_exercisetype_detail, container, false);
        ViewGroup rootLayout = (ViewGroup) rootView.findViewById(R.id.fragment_exercisetype_detail_layout);

		// If Image Exists
		if (!mExercise.getImagePaths().isEmpty()) {
            // Create Image View
            ImageView imageView = new ImageView (rootView.getContext());
            imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            imageView.setPadding(20, 20, 20, 20);
            rootLayout.addView(imageView);
            imageView.setVisibility(View.VISIBLE);

            // set gesture detector
            this.mGestureScanner = new GestureDetector(this.getActivity(), new ExerciseDetailOnGestureListener
                    (this, imageView, mExercise));

            //Set Image
            DataHelper data = new DataHelper(getActivity());
			imageView.setImageDrawable(data.getDrawable(mExercise.getImagePaths().get(0).toString()));


            rootView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return mGestureScanner.onTouchEvent(event);
                }
            });
		} else {
            TextView textView = new TextView (rootView.getContext());
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            textView.setGravity(Gravity.CENTER);
            textView.setPadding(10,10,10,10);
            textView.setText(Html.fromHtml(mExercise.getDescription()));
            rootLayout.addView(textView);
		}
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_detail_menu, menu);

		// configure menu_item_add_exercise
		MenuItem menu_item_add_exercise = (MenuItem) menu.findItem(R.id.menu_item_add_exercise);
		menu_item_add_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {

				// assert, that an exercise was choosen
				if (mExercise == null) {
					Log.wtf(TAG, "No exercise has been choosen. This should not happen");
					return true;
				}

				// add exercise to workout or create a new one
				if (mWorkout == null) {
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
					String defaultWorkoutName =  settings.getString("default_workout_name", "Workout");

					mWorkout = new Workout(defaultWorkoutName, new FitnessExercise(mExercise));
				} else {

					// assert that there is not already such an exercise in the
					// workout
					for (FitnessExercise fEx : mWorkout.getFitnessExercises()) {
						if (fEx.getExType().equals(mExercise)) {
							Toast.makeText(getActivity(), getString(R.string.exercise_already_in_workout), Toast.LENGTH_LONG).show();
							return true;
						}
					}

					mWorkout.addFitnessExercise(new FitnessExercise(mExercise));
				}

				// update Workout in Activity
				if (getActivity() instanceof Callbacks) {
					// was launched by ExerciseTypeListActivity
					((Callbacks) getActivity()).onWorkoutChanged(mWorkout);
				} else {
					// was launched by ExerciseTypeDetailActivity
					Intent i = new Intent();
					i.putExtra(ExerciseTypeListActivity.ARG_WORKOUT, mWorkout);
					getActivity().setResult(Activity.RESULT_OK, i);
					getActivity().finish();
				}

				Toast.makeText(getActivity(),
						getString(R.string.exercise) + " " + mExercise.getLocalizedName() + " " + getString(R.string.has_been_added),
						Toast.LENGTH_SHORT).show();

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
	
		// configure menu_item_delete_exercise
		if(mExercise != null  && mExercise.getExerciseSource() == ExerciseSource.CUSTOM){
			MenuItem menu_item_delete_exercise = (MenuItem) menu.findItem(R.id.menu_item_delete_exercise);
			menu_item_delete_exercise.setVisible(true);
			menu_item_delete_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					IDataProvider dataProvider = new DataProvider(getActivity());
					dataProvider.deleteCustomExercise(mExercise);
					
					if (getActivity() instanceof Callbacks) {
						// was launched by ExerciseTypeListActivity
						((Callbacks) getActivity()).onExerciseDeleted(mExercise);
					} else {
						// was launched by ExerciseTypeDetailActivity
						Intent i = new Intent();
						i.putExtra(ARG_DELETED_EXERCISE, mExercise);
						getActivity().setResult(RESULT_EXERCISE_CHANGED, i);
						getActivity().finish();
					}
					
										
					return false;
				}
			});
		}
		
		
		// configure menu_item_upload_exercise
		MenuItem menu_item_upload_exercise = (MenuItem) menu.findItem(R.id.menu_item_upload_exercise);
		menu_item_upload_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {	
				Context context = ExerciseTypeDetailFragment.this.getActivity();
				
				//UploadExerciseAsyncTask exUpload = new UploadExerciseAsyncTask(context);
				//exUpload.execute(mExercise);
				
				
				UploadExerciseImagesAsyncTask exImageUpload = new UploadExerciseImagesAsyncTask(context);
				exImageUpload.execute(mExercise);

				return true;
			}
		});
	
		// configure menu_item_send_exercise_feedback
			MenuItem menu_item_delete_exercise = (MenuItem) menu.findItem(R.id.menu_item_send_exercise_feedback);
			menu_item_delete_exercise.setVisible(true);
			menu_item_delete_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener(){
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					
					FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
					Fragment prev = getActivity().getSupportFragmentManager().findFragmentByTag("dialog");
					if (prev != null) {
						ft.remove(prev);
					}
					ft.addToBackStack(null);

					// Create and show the dialog.
					DialogFragment newFragment = SendExerciseFeedbackDialogFragment.newInstance(mExercise);
					newFragment.show(ft, "dialog");

					return false;
				}
			});
		
		
		

		
	}

}
