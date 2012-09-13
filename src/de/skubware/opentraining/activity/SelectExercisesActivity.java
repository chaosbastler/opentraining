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

package de.skubware.opentraining.activity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.opentraining.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

// TODO Change layout to master/detail flow for supporting smaller screens

/**
 * Shows a list with all exercises. The user can add these exercises to the
 * training plan. There is an illustration for each exercise(otherwise a dummy
 * image is shown) and more data, like required equipment, the muscles that are
 * activated, ... The user also can set which exercises are shown(user can
 * select muscles).
 * 
 * @author Christian Skubich
 * 
 */
public class SelectExercisesActivity extends Activity {

	private Map<Muscle, Boolean> muscleMap = new HashMap<Muscle, Boolean>();

	private ExerciseType currentExercise = ExerciseType.listExerciseTypes().first();
	private int currentImage = 0;

	private GestureDetector gestureScanner = new GestureDetector(new SelectExerciseGesture());

	private static final String TAG = "SelectExercisesActivity";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.select_exercises_menu, menu);

		// configure menu_item_add_exercise
		MenuItem menu_item_add_exercise = (MenuItem) menu.findItem(R.id.menu_item_add_exercise);
		menu_item_add_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (currentExercise == null) {
					Toast.makeText(SelectExercisesActivity.this, getString(R.string.no_exercises_choosen), Toast.LENGTH_LONG).show();
					return true;
				}

				Workout w = DataManager.INSTANCE.getCurrentWorkout();
				if (w == null) {
					w = new Workout("My Plan", new FitnessExercise(currentExercise));
					DataManager.INSTANCE.setWorkout(w);
				} else {
					w.addFitnessExercise(new FitnessExercise(currentExercise));
				}

				CharSequence text = getString(R.string.exercise) + " " + currentExercise.getName() + " " + getString(R.string.has_been_added);
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(SelectExercisesActivity.this, text, duration);
				toast.show();

				return true;
			}
		});

		// configure menu_item_next
		final MenuItem menu_item_next = (MenuItem) menu.findItem(R.id.menu_item_next);
		menu_item_next.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (DataManager.INSTANCE.getCurrentWorkout() == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(SelectExercisesActivity.this);
					builder.setMessage(getString(R.string.no_exercises_choosen)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					return true;
				} else {

					startActivity(new Intent(SelectExercisesActivity.this, EditWorkoutActivity.class));
					finish();
				}

				return true;
			}

		});

		// configure menu_item_create_new_exercise
		final MenuItem menu_item_create_new_exercise = (MenuItem) menu.findItem(R.id.menu_item_create_new_exercise);
		menu_item_create_new_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(SelectExercisesActivity.this, CreateExerciseActivity.class));
				return true;
			}
		});

		MenuItem menu_item_select_muscles = (MenuItem) menu.findItem(R.id.menu_item_select_muscles);
		menu_item_select_muscles.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem menuitem) {
				final CharSequence[] items = new CharSequence[Muscle.values().length];
				final boolean[] states = new boolean[Muscle.values().length];
				int i = 0;
				for (Muscle m : Muscle.values()) {
					items[i] = m.toString();
					states[i] = muscleMap.get(m);
					i++;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(SelectExercisesActivity.this);
				builder.setTitle(getString(R.string.select_muscles));
				builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialogInterface, int item, boolean state) {
						muscleMap.put(Muscle.getByName(items[item].toString()), state);

						if (!muscleMap.values().contains(Boolean.TRUE)) {
							Toast.makeText(SelectExercisesActivity.this, getString(R.string.please_select_muscle), Toast.LENGTH_LONG).show();
						}
					}
				});
				builder.setNeutralButton(getString(R.string.select_all), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						int i = 0;
						for (Muscle m : Muscle.values()) {
							items[i] = m.toString();
							states[i] = true;
							muscleMap.put(m, true);
							i++;
						}
						updateExList();
						dialog.dismiss();
					}
				});
				builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!muscleMap.values().contains(Boolean.TRUE)) {
							muscleMap.put(Muscle.values()[0], true);
							Toast.makeText(SelectExercisesActivity.this, Muscle.values()[0].toString() + " " + getString(R.string.was_choosen), Toast.LENGTH_LONG).show();

						}
						updateExList();
					}
				});
				builder.setIcon(R.drawable.icon_attention);
				builder.create().show();

				return true;
			}
		});

		return true;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// fill muscle map
		for (Muscle m : Muscle.values()) {
			muscleMap.put(m, true);
		}

		setContentView(R.layout.select_exercises);

		final ListView exListView = (ListView) findViewById(R.id.exListView);

		// Update ExList
		updateExList();

		// Show first exercise
		assert (!ExerciseType.listExerciseTypes().isEmpty());
		this.showExercise(ExerciseType.listExerciseTypes().first());

		// Set action onclick for exercise list
		exListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				String name = exListView.getAdapter().getItem(arg2).toString();
				ExerciseType ex = ExerciseType.getByName(name);
				showExercise(ex);
			}

		});

	}

	@Override
	public void onRestart() {
		super.onRestart();
		
		// update exercise list, saved state my not be up-to-date
		this.updateExList();
	}

	private void updateExList() {
		final ListView exListView = (ListView) findViewById(R.id.exListView);

		ArrayList<String> exes = new ArrayList<String>();
		for (ExerciseType exType : ExerciseType.listExerciseTypes()) {
			boolean shouldAdd = false;
			for (Muscle m : exType.getActivatedMuscles()) {
				if (muscleMap.get(m)) {
					shouldAdd = true;
					break;
				}
			}
			if (shouldAdd || exType.getActivatedMuscles().isEmpty())
				exes.add(exType.getName());
		}

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, exes);
		exListView.setAdapter(listAdapter);

		// consider that there is no exercise in the list
		if (listAdapter.getCount() > 0) {
			String name = exListView.getAdapter().getItem(0).toString();
			ExerciseType ex = ExerciseType.getByName(name);
			showExercise(ex);
		} else {
			showExercise(null);
		}
	}

	/**
	 * Shows the currently exercise. That means that the different TextViews and
	 * the image are updated.
	 * 
	 * @param ex
	 *            The exercise to show
	 */
	private void showExercise(ExerciseType ex) {
		this.currentExercise = ex;

		if (ex == null) {
			ex = new ExerciseType.Builder(getString(R.string.no_exercise_choosen)).build();
		}

		// Exercise Name
		TextView exerciseName = (TextView) findViewById(R.id.textview_exercise_name);
		exerciseName.setText(ex.getName());

		// for loop is a try to reduce code length.
		for (int i = 0; i < 4; i++) {

			@SuppressWarnings("rawtypes")
			Collection c;
			TextView tw, tw0, tw1;

			switch (i) {
			case 0:
				c = ex.getActivatedMuscles();
				tw = (TextView) findViewById(R.id.textview_muscle);
				tw0 = (TextView) findViewById(R.id.textview_muscle0);
				tw1 = (TextView) findViewById(R.id.textview_muscle1);
				break;
			case 1:
				c = ex.getRequiredEquipment();
				tw = (TextView) findViewById(R.id.textview_equipment);
				tw0 = (TextView) findViewById(R.id.textview_equipment0);
				tw1 = (TextView) findViewById(R.id.textview_equipment1);
				break;
			case 2:
				c = ex.getTags();
				tw = (TextView) findViewById(R.id.textview_tag);
				tw0 = (TextView) findViewById(R.id.textview_tag0);
				tw1 = (TextView) findViewById(R.id.textview_tag1);
				break;
			case 3:
				c = ex.getHints();
				tw = (TextView) findViewById(R.id.textview_hint);
				tw0 = (TextView) findViewById(R.id.textview_hint0);
				tw1 = (TextView) findViewById(R.id.textview_hint1);
				break;
			default:
				throw new AssertionError("");
			}

			@SuppressWarnings("rawtypes")
			Iterator it = c.iterator();
			if (c.isEmpty())
				tw.setVisibility(View.GONE);
			else
				tw.setVisibility(View.VISIBLE);

			if (!it.hasNext())
				tw0.setText("");
			else
				tw0.setText(it.next().toString());
			if (!it.hasNext())
				tw1.setText("");
			else
				tw1.setText(it.next().toString());
		}

		// Image license
		TextView image_license = (TextView) findViewById(R.id.textview_image_license);
		if (ex.getImageLicenseMap().values().iterator().hasNext()) {
			image_license.setText(ex.getImageLicenseMap().values().iterator().next());
		} else {
			image_license.setText("Keine Lizenzinformationen vorhanden");
		}

		// Images
		ImageView imageview = (ImageView) findViewById(R.id.imageview);
		if (!ex.getImagePaths().isEmpty()) {
			imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(ex.getImagePaths().get(0).toString()));
		} else {
			imageview.setImageResource(R.drawable.defaultimage);
		}

		if (ex.getName().equals(getString(R.string.no_exercise_choosen)))
			ExerciseType.removeExerciseType(ex);

	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	/**
	 * Enumeration for defining direction for image change, only used in an
	 * inner class
	 */
	private enum DIRECTION {
		FORWARD, BACKWARD
	};

	/**
	 * A private inner class that handles gestures.
	 */
	private class SelectExerciseGesture implements OnGestureListener {

		/**
		 * Next image is shown when image is just taped.
		 */
		public boolean onSingleTapUp(MotionEvent e) {
			Log.i(TAG, "Taped on image");
			switchImage(DIRECTION.FORWARD);
			return true;
		}

		/**
		 * When the user swipes, he can also go back.
		 */
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

			final ViewConfiguration vc = ViewConfiguration.get(SelectExercisesActivity.this);
			final int swipeMinDistance = vc.getScaledTouchSlop();
			final int swipeMaxDistance = vc.getScaledMaximumFlingVelocity();
			final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();

			if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDistance)
				return false;

			// right to left swipe
			if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
				switchExercise(DIRECTION.FORWARD);
			} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
				switchExercise(DIRECTION.BACKWARD);
			}

			return true;
		}

		/**
		 * Shows the next or previous {@link ExerciseType}.
		 * 
		 * @param direction
		 */
		private void switchExercise(DIRECTION direction) {

			// attention - copied code from above
			ArrayList<ExerciseType> exList = new ArrayList<ExerciseType>();
			for (ExerciseType exType : ExerciseType.listExerciseTypes()) {
				boolean shouldAdd = false;
				for (Muscle m : exType.getActivatedMuscles()) {
					if (muscleMap.get(m)) {
						shouldAdd = true;
						break;
					}
				}
				if (shouldAdd || exType.getActivatedMuscles().isEmpty())
					exList.add(exType);
			}
			// end copied

			int idx = exList.indexOf(currentExercise);

			if (direction == DIRECTION.FORWARD) {
				if (idx < exList.size() - 1)
					idx++;
			} else {
				if (idx > 0)
					idx--;
			}

			showExercise(exList.get(idx));

		}

		/**
		 * Changes the image according to the given directin.
		 * 
		 * @param direction
		 * @see{SelectExercisesActivity.DIRECTION
		 */
		private void switchImage(DIRECTION direction) {
			ImageView imageview = (ImageView) findViewById(R.id.imageview);
			if (direction == DIRECTION.FORWARD) {
				Log.i(TAG, "(Right to) left swipe on image");

				currentImage--;
				if (currentImage < 0)
					currentImage = currentExercise.getImagePaths().size() - 1;
				// TODO find a better solution than exception
				try {
					imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(currentExercise.getImagePaths().get(currentImage).toString()));
				} catch (IndexOutOfBoundsException ex) {

				}
			} else {
				Log.i(TAG, "(Left to) right swipe on image");

				currentImage++;
				if (currentImage >= currentExercise.getImagePaths().size())
					currentImage = 0;
				// TODO find a better solution than exception
				try {
					imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(currentExercise.getImagePaths().get(currentImage).toString()));
				} catch (IndexOutOfBoundsException ex) {

				}
			}

		}

		/** Not used */
		public void onLongPress(MotionEvent e) {
		}

		/** Not used */
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		/** Not used */
		public void onShowPress(MotionEvent e) {
		}

		/** Not used */
		public boolean onDown(MotionEvent e) {
			return false;
		}
	}

}
