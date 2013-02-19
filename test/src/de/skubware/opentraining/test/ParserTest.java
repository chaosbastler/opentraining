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

package de.skubware.opentraining.test;

import java.util.List;

import android.test.AndroidTestCase;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * Tests for the Parsers for {@link Muscle}, {@link SportsEquipment} and {@link ExerciseType}.
 *
 * @author Christian Skubich
 *
 */
public class ParserTest extends AndroidTestCase {

	final String WORKOUT_NAME = "TEST_woRkout!=0";
	
	final String EXERCISE_NAME_1 = "First exercise";
	final String EXERCISE_NAME_2 = "2. exercise";

	
	
	
	public void testParseMuscleJSON(){
		IDataProvider dataProvider = new DataProvider(getContext());
		List<Muscle> muscleList = dataProvider.getMuscles();
		assertFalse(muscleList.isEmpty());
		
		String[] backNames = { "Rückenmuskeln", "Rückenmuskel", "Rücken", "Back muscle", "Back"  };
		Muscle back = dataProvider.getMuscleByName(backNames[0]);
		assertNotNull(back);
		for(String backName:backNames){
			assertEquals(back,dataProvider.getMuscleByName(backName));
		}	
	}
	
	public void testParseSportsEquipmentJSON(){
		IDataProvider dataProvider = new DataProvider(getContext());
		List<SportsEquipment> equipmentList = dataProvider.getEquipment();
		assertFalse(equipmentList.isEmpty());
		
		String[] matNames = { "Übungsmatte", "Gymnastikmatte", "Exercise Mat"};
		SportsEquipment mat = dataProvider.getEquipmentByName(matNames[0]);
		assertNotNull(mat);
		for(String backName:matNames){
			assertEquals(mat,dataProvider.getEquipmentByName(backName));
		}	
	}
	
	public void testParseExerciseTypeXML(){
		IDataProvider dataProvider = new DataProvider(getContext());
		List<ExerciseType> exList = dataProvider.getExercises();
		assertFalse(exList.isEmpty());
	}

	
	
	public void testSaveAndLoadWorkout(){
		// crate a workout
		ExerciseType EX_1 = new ExerciseType.Builder(EXERCISE_NAME_1).build();
		ExerciseType EX_2 = new ExerciseType.Builder(EXERCISE_NAME_2).build();

		Workout mWorkout = new Workout(WORKOUT_NAME, EX_1.asFitnessExercise(), EX_2.asFitnessExercise());
		
		
		DataProvider dataProvider = new DataProvider(getContext());
		// save workout
		dataProvider.saveWorkout(mWorkout);
		// load again from filesystem
		List<Workout> workoutList = dataProvider.getWorkouts();
		
		assertTrue(workoutList.contains(mWorkout));
		
	}

}
