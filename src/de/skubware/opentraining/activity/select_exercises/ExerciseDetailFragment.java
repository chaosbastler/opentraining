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

package de.skubware.opentraining.activity.select_exercises;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.ContentProvider;

/**
 * Fragment that shows the details of the ExerciseType. Has an own options menu.
 * 
 */
public class ExerciseDetailFragment extends Fragment {
	/** Tag for logging */
	private static final String TAG = "ExerciseDetailFragment";

	/** The key for the argument that contains the exercise */
	public static final String ARG_ITEM_ID = "EXERCISE_ID";

	/** Currently selected exercise */
	ExerciseType exercise;

	/** GestureDetector for changing image */
	private GestureDetector gestureScanner;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			exercise = ExerciseType.getByName(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
		ImageView imageview = (ImageView) (ImageView) rootView.findViewById(R.id.imageview);

		// set gesture detector
		this.gestureScanner = new GestureDetector(this.getActivity(), new ExerciseDetailOnGestureListener(this, imageview));

		if (exercise != null) {
			// Images
			if (!exercise.getImagePaths().isEmpty()) {
				imageview.setImageDrawable(ContentProvider.INSTANCE.getDrawable(exercise.getImagePaths().get(0).toString(), this.getActivity()));
			} else {
				imageview.setImageResource(R.drawable.defaultimage);
			}

			// Image license
			TextView image_license = (TextView) rootView.findViewById(R.id.textview_image_license);
			if (exercise.getImageLicenseMap().values().iterator().hasNext()) {
				image_license.setText(exercise.getImageLicenseMap().values().iterator().next());
			} else {
				image_license.setText("Keine Lizenzinformationen vorhanden");
			}

			rootView.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return gestureScanner.onTouchEvent(event);
				}
			});

		}

		return rootView;

	}

	/**
	 * Sets the option menu.
	 */
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_detail_menu, menu);

		// configure menu_item_add_exercise
		MenuItem menu_item_add_exercise = (MenuItem) menu.findItem(R.id.menu_item_add_exercise);
		menu_item_add_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {

				// assert, that an exercise was choosen
				if (exercise == null) {
					Log.w(TAG, "No exercise has been choosen. This should not happen");
					return true;
				}

				// add exercise to workout or create a new one
				Workout w = ContentProvider.INSTANCE.getCurrentWorkout();
				if (w == null) {
					w = new Workout("My Plan", new FitnessExercise(exercise));
					ContentProvider.INSTANCE.setWorkout(w);
				} else {
					w.addFitnessExercise(new FitnessExercise(exercise));
				}

				// finally show message
				CharSequence text = getString(R.string.exercise) + " " + exercise.toString() + " " + getString(R.string.has_been_added);
				Toast.makeText(ExerciseDetailFragment.this.getActivity(), text, Toast.LENGTH_LONG).show();

				return true;
			}
		});

	}

}
