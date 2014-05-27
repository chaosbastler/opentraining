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

import java.io.File;
import java.util.List;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageView;
import de.skubware.opentraining.basic.IExercise;
import de.skubware.opentraining.db.DataHelper;

/**
 * A GestureListener for {@link ExerciseTypeDetailFragment} or
 * {@link FExDetailFragment}.
 * 
 */
public class ExerciseDetailOnGestureListener implements OnGestureListener {

	/** Tag for logging */
	private static final String TAG = "ExerciseDetailOnGestureListener";

	/** Currently selected mExercise */
	private IExercise mExercise;

	/** ImageView for the image of the mExercise */
	private ImageView mImageview;

	/** Reference to the 'parent' fragment */
	private Fragment mFragment;

	private int imageIndex = 0;

	public ExerciseDetailOnGestureListener(Fragment fragment, ImageView imageview, IExercise exercise) {

		this.mExercise = exercise;
		this.mImageview = imageview;
		this.mFragment = fragment;
	}

	/**
	 * Next image is shown when image is just taped.
	 */
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG, "Taped on image");

		List<File> paths = mExercise.getImagePaths();
		// ignore, if there is no other image to show
		if (paths.size() < 2)
			return true;

		imageIndex--;
		if (imageIndex < 0) {
			imageIndex = mExercise.getImagePaths().size() - 1;
		}

		DataHelper dataHelper = new DataHelper(mFragment.getActivity());

		mImageview.setImageDrawable(dataHelper.getDrawable(paths.get(imageIndex).toString()));

		return true;
	}

	/** Not used */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;

		/*
		 * final ViewConfiguration vc = ViewConfiguration.get(activity); final
		 * int swipeMinDistance = vc.getScaledTouchSlop(); final int
		 * swipeMaxDistance = vc.getScaledMaximumFlingVelocity(); final int
		 * swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();
		 * 
		 * if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDistance) return false;
		 * 
		 * // right to left swipe if (e1.getX() - e2.getX() > swipeMinDistance
		 * && Math.abs(velocityX) > swipeThresholdVelocity) { Log.i(TAG,
		 * "Swiped forward"); } else if (e2.getX() - e1.getX() >
		 * swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
		 * Log.i(TAG, "Swiped backwards"); }
		 * 
		 * return true;
		 */
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
