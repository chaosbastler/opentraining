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

package de.skubware.opentraining;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageView;
import de.skubware.opentraining.activity.EditWorkoutActivity;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.datamanagement.DataManager;

public class ExerciseDetailOnGestureListener implements OnGestureListener {

	/** Tag for logging */
	private static final String TAG = "ExerciseDetailOnGestureListener";

	private Activity activity;
	private ExerciseType exercise;
	private int currentImage = 0;
	private ImageView imageview;

	public ExerciseDetailOnGestureListener(ExerciseDetailFragment fragment, ImageView imageview) {
		this.activity = fragment.getActivity();
		this.exercise = fragment.exercise;
		this.imageview = imageview;
	}

	/**
	 * Enumeration for defining direction for image change, only used in an
	 * inner class
	 */
	private enum DIRECTION {
		FORWARD, BACKWARD
	};

	/**
	 * Next image is shown when image is just taped.
	 */
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG, "Taped on image");
		switchImage(DIRECTION.FORWARD);
		return true;
	}

	/**
	 * When the user swipes, he changes activity.
	 */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

		final ViewConfiguration vc = ViewConfiguration.get(activity);
		final int swipeMinDistance = vc.getScaledTouchSlop();
		final int swipeMaxDistance = vc.getScaledMaximumFlingVelocity();
		final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();

		if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDistance)
			return false;

		// right to left swipe
		if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
			if (DataManager.INSTANCE.getCurrentWorkout() != null) {
				activity.startActivity(new Intent(activity, EditWorkoutActivity.class));
				activity.finish();
			}
		} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
			activity.finish();
		}

		return true;
	}

	/**
	 * Changes the image according to the given directin.
	 * 
	 * @param direction
	 * @see{SelectExercisesActivity.DIRECTION
	 */
	private void switchImage(DIRECTION direction) {
		if (direction == DIRECTION.FORWARD) {
			Log.i(TAG, "(Right to) left swipe on image");

			currentImage--;
			if (currentImage < 0)
				currentImage = exercise.getImagePaths().size() - 1;
			// TODO find a better solution than exception
			try {
				imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(exercise.getImagePaths().get(currentImage).toString()));
			} catch (IndexOutOfBoundsException ex) {

			}
		} else {
			Log.i(TAG, "(Left to) right swipe on image");

			currentImage++;
			if (currentImage >= exercise.getImagePaths().size())
				currentImage = 0;
			// TODO find a better solution than exception
			try {
				imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(exercise.getImagePaths().get(currentImage).toString()));
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
		return true;
	}

}
