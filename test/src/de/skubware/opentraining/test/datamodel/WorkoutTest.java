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

package de.skubware.opentraining.test.datamodel;

import android.test.AndroidTestCase;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.basic.FSet.SetParameter.*;

/**
 * Tests for {@link WorkoutTest}.
 *
 */
public class WorkoutTest extends AndroidTestCase {
	/** Tag for logging */
	public static final String TAG = "WorkoutTest";

	final String WORKOUT_NAME = "TEST_woRkout!=0";
	
	final String EXERCISE_NAME_1 = "Curl";
	final String EXERCISE_NAME_2 = "Crunch";
	final String EXERCISE_NAME_3 = "Benchpress";

	public void testEquals(){
		// create a workout
		ExerciseType EX_1 = new ExerciseType.Builder(EXERCISE_NAME_1, ExerciseSource.CUSTOM).build();
		ExerciseType EX_2 = new ExerciseType.Builder(EXERCISE_NAME_2, ExerciseSource.CUSTOM).build();
		ExerciseType EX_3 = new ExerciseType.Builder(EXERCISE_NAME_3, ExerciseSource.CUSTOM).build();

		
		Repetition REP = new Repetition(1);
		Duration DUR = new Duration(30);
		Weight WEIGHT = new Weight(15);
		FreeField FREE_FIELD = new FreeField("some userdata!");

		FSet SET_1 = new FSet(REP, DUR, WEIGHT );
		FSet SET_2 = new FSet(FREE_FIELD);
		
		FitnessExercise FEX_1 = new FitnessExercise(EX_1, SET_1, SET_1);
		FitnessExercise FEX_2 = new FitnessExercise(EX_2, SET_2);
		FitnessExercise FEX_3 = new FitnessExercise(EX_3);

		
		Workout mWorkout = new Workout(WORKOUT_NAME, FEX_1, FEX_2, FEX_3);
		
				
		
		// create a second workout
		ExerciseType EX_1S = new ExerciseType.Builder(EXERCISE_NAME_1, ExerciseSource.CUSTOM).build();
		ExerciseType EX_2S = new ExerciseType.Builder(EXERCISE_NAME_2, ExerciseSource.CUSTOM).build();
		ExerciseType EX_3S = new ExerciseType.Builder(EXERCISE_NAME_3, ExerciseSource.CUSTOM).build();

		
		Repetition REPS = new Repetition(1);
		Duration DURS = new Duration(30);
		Weight WEIGHTS = new Weight(15);
		FreeField FREE_FIELDS = new FreeField("some userdata!");

		FSet SET_1S = new FSet(REPS, DURS, WEIGHTS );
		FSet SET_2S = new FSet(FREE_FIELDS);
		
		FitnessExercise FEX_1S = new FitnessExercise(EX_1S, SET_1S, SET_1S);
		FitnessExercise FEX_2S = new FitnessExercise(EX_2S, SET_2S);
		FitnessExercise FEX_3S = new FitnessExercise(EX_3S);

		
		Workout mWorkoutS = new Workout(WORKOUT_NAME, FEX_1S, FEX_2S, FEX_3S);

		assertEquals(mWorkout, mWorkoutS);
	}

}
