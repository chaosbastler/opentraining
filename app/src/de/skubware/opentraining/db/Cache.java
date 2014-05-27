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

package de.skubware.opentraining.db;

import java.util.List;

import android.content.Context;
import android.util.Log;

import de.skubware.opentraining.basic.ExerciseTag;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;

/**
 * Singleton Cache for {@link ExerciseType}s.
 * 
 * This cache should be transparent to most parts of the app. Only the
 * {@link DataProvider} should use this class. For a better performance it may
 * be necessary to run updateCache() once when the app starts. Besides this the
 * activities should never use this class.
 * 
 * 
 */
public enum Cache {
	INSTANCE;
	
	/** Tag for logging. */
	private final static String TAG = "Cache";

	private List<ExerciseType> mExerciseList = null;
	private List<Muscle> mMuscleList = null;
	private List<SportsEquipment> mSportsEquipmentList = null;
	private List<ExerciseTag> mExerciseTagList = null;
	private List<Workout> mWorkoutList = null;


	/**
	 * Refreshes the cached data.
	 * 
	 * @param mContext
	 */
	public void updateCache(Context mContext) {
		DataProvider dataProvider = new DataProvider(mContext);
		mMuscleList = dataProvider.loadMuscles();
		mSportsEquipmentList = dataProvider.loadEquipment();
		mExerciseTagList = dataProvider.loadExerciseTags();
		// important: ExerciseTypes have to be loaded after muscles, equipment and tags
		mExerciseList = dataProvider.loadExercises();
		// workouts have to be loaded last
		mWorkoutList = dataProvider.loadWorkouts();
	}

	/**
	 * Returns the cached {@link ExerciseType}s.
	 * 
	 * @return The cached {@link ExerciseType}s or null.
	 */
	public synchronized List<ExerciseType> getExercises() {
		return mExerciseList;
	}

	public List<Muscle> getMuscles() {
		return mMuscleList;
	}

	public List<SportsEquipment> getEquipment() {
		return mSportsEquipmentList;
	}

	public List<ExerciseTag> getExerciseTags() {
		return mExerciseTagList;
	}
	
	public synchronized List<Workout> getWorkouts() {
		return mWorkoutList;
	}

	/**
	 * Most of the data that is cached, does not change during runtime. This is
	 * not true for the Workouts. So the cache has to be updated everytime that
	 * a Workout changes. This should be done in a new Thread.
	 * 
	 * @param mContext
	 */
	public synchronized void updateWorkoutCache(Context mContext){
		Log.d(TAG, "updating Workout cache");
		DataProvider dataProvider = new DataProvider(mContext);
		mWorkoutList = dataProvider.loadWorkouts();
	}
	
	/**
	 * Updates the cached exercises. Should be callend when the exercises have
	 * changed.
	 * 
	 * @param mContext
	 */
	public synchronized void updateExerciseCache(Context mContext){
		Log.d(TAG, "updating Exercise cache");
		DataProvider dataProvider = new DataProvider(mContext);
		mExerciseList = dataProvider.loadExercises();
	}
	
}
