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

import java.io.File;
import java.util.List;

import android.util.Log;
import android.view.MotionEvent;
import android.view.GestureDetector.OnGestureListener;
import android.widget.ImageView;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.datamanagement.DataManager;

/**
 * A GestureListener for {@link ExerciseDetailFragment}.
 *
 */
public class ExerciseDetailOnGestureListener implements OnGestureListener {
	
	/** Tag for logging */
	private static final String TAG = "ExerciseDetailOnGestureListener";
	
	/** Currently selected exercise */
	private ExerciseType exercise;
	
	/** ImageView for the image of the exercise*/
	private ImageView imageview;
	
	private int imageIndex = 0;

	public ExerciseDetailOnGestureListener(ExerciseDetailFragment fragment, ImageView imageview) {
		if(! (fragment instanceof ExerciseDetailFragment))
			Log.e(TAG, "Missuse of ExerciseDetailOnGestureListener: fragmet is not instanceof ExerciseDetailFragment" );
		
		this.exercise = fragment.exercise;
		this.imageview = imageview;
	}

	

	/**
	 * Next image is shown when image is just taped.
	 */
	public boolean onSingleTapUp(MotionEvent e) {
		Log.i(TAG, "Taped on image");
		
		List<File> paths = exercise.getImagePaths();
		// ignore, if there is no other image to show
		if(paths.size()<2)
			return true;

		imageIndex--;
		if (imageIndex < 0){
			imageIndex = exercise.getImagePaths().size() - 1;
		}
		
		imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(paths.get(imageIndex).toString()));
			
		return true;
	}
	
	

	/** Not used */
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return false;

		/*final ViewConfiguration vc = ViewConfiguration.get(activity);
		final int swipeMinDistance = vc.getScaledTouchSlop();
		final int swipeMaxDistance = vc.getScaledMaximumFlingVelocity();
		final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();

		if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDistance)
			return false;

		// right to left swipe
		if (e1.getX() - e2.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
			Log.i(TAG, "Swiped forward");
		} else if (e2.getX() - e1.getX() > swipeMinDistance && Math.abs(velocityX) > swipeThresholdVelocity) {
			Log.i(TAG, "Swiped backwards");
		}

		return true;*/
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
