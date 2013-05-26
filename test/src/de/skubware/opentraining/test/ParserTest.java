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

import java.io.IOException;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;


import android.test.AndroidTestCase;
import android.util.Log;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FSet.SetParameter.*;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.TrainingEntry;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import de.skubware.opentraining.db.parser.ExerciseTypeXMLParser;

/**
 * Tests for the Parsers for {@link Muscle}, {@link SportsEquipment}, {@link ExerciseType} and {@link Workout}.
 *
 * @author Christian Skubich
 *
 */
public class ParserTest extends AndroidTestCase {
	/** Tag for logging */
	public static final String TAG = "ParserTest";
	
	
	final String WORKOUT_NAME = "TEST_woRkout!=0";
	
	final String EXERCISE_NAME_1 = "Curl";
	final String EXERCISE_NAME_2 = "Crunch";
	final String EXERCISE_NAME_3 = "Benchpress";
	
	
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
		// create a workout
		ExerciseType EX_1 = new ExerciseType.Builder(EXERCISE_NAME_1).build();
		ExerciseType EX_2 = new ExerciseType.Builder(EXERCISE_NAME_2).build();
		ExerciseType EX_3 = new ExerciseType.Builder(EXERCISE_NAME_3).build();

		
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
		mWorkout.addTrainingEntry(Calendar.getInstance().getTime());
		
		TrainingEntry firstTrainingEntry = FEX_1.getTrainingEntryList().get(0);
		firstTrainingEntry.add(SET_2);
		firstTrainingEntry.add(SET_2);
		firstTrainingEntry.add(SET_2);

		FSet firstSet = firstTrainingEntry.getFSetList().get(0);
		firstTrainingEntry.setHasBeenDone(firstSet, false);
		
		DataProvider dataProvider = new DataProvider(getContext());
		DataHelper dataHelper = new DataHelper(getContext());
		// save workout
		dataProvider.saveWorkout(mWorkout);
		
		// print Workout for debugging
		String path = mContext.getFilesDir().toString() + "/"  + WORKOUT_NAME + ".xml";
		try {
			String xmlData = dataHelper.loadFileFromFileSystem(path);
			Log.d(TAG, "Workout XML File: \n\n" + xmlData);
		} catch (IOException e) {
			Log.e(TAG, "Error parsing xml", e);
		}


		// load again from filesystem
		List<Workout> workoutList = dataProvider.getWorkouts();
		
		boolean foundWorkout = false;
		for(Workout w:workoutList){
			if(w.getName().equals(mWorkout.getName())){
				foundWorkout = true;
				//TODO find bug in equals()
				assertEquals(mWorkout.toDebugString(), w.toDebugString());
				Log.d(TAG, "Expected: " + mWorkout.toDebugString());
				Log.d(TAG, "Actual: " + w.toDebugString());
				//assertEquals(mWorkout, w);
			}
		}
		assertTrue(foundWorkout);
		
		/*if(!workoutList.contains(mWorkout)){
			String msg = "Workout list does not contain Workout. WorkoutList: " ;
			for(Workout w:workoutList){
				msg += w.toDebugString();
				msg += "\n\n";
			}
			msg += "\n\n\nexpected: \n\n" + mWorkout.toDebugString();
			
			fail(msg);
		}*/


	}
	
	public void testSaveAndLoadExerciseType(){
		IDataProvider dataProvider = new DataProvider(getContext());

		
		ExerciseType.Builder builder = new ExerciseType.Builder("NAME_1");
		
		Map<Locale, String> translationMap = new HashMap<Locale, String>();
		translationMap.put(Locale.GERMAN, "NAME_1");
		translationMap.put(Locale.ENGLISH, "English name");
		builder.translationMap(translationMap);
		
		builder.description("Some description text ..!");
		
		// muscles
		SortedSet<Muscle> muscles = new TreeSet<Muscle>();
		muscles.add(dataProvider.getMuscles().get(0));
		muscles.add(dataProvider.getMuscles().get(1));
		builder.activatedMuscles(muscles);
		
		// equipment
		SortedSet<SportsEquipment> equipment = new TreeSet<SportsEquipment>();
		equipment.add(dataProvider.getEquipment().get(2));
		equipment.add(dataProvider.getEquipment().get(4));
		builder.neededTools(equipment);
		
		ExerciseType exerciseBefore = builder.build();
		
		// save ex
		dataProvider.saveExercise(exerciseBefore);
		
		// load ex, check fields		
		ExerciseType exerciseAfter = dataProvider.getExerciseByName(exerciseBefore.getUnlocalizedName());
		// equals check is not enough(it only checks the name)
		assertEquals(exerciseAfter.getDescription(), exerciseBefore.getDescription());
		assertEquals(exerciseAfter.getImageHeight(), exerciseBefore.getImageHeight());
		assertEquals(exerciseAfter.getImageWidth(), exerciseBefore.getImageWidth());
		assertEquals(exerciseAfter.getLocalizedName(), exerciseBefore.getLocalizedName());
		assertEquals(exerciseAfter.getUnlocalizedName(), exerciseBefore.getUnlocalizedName());
		assertEquals(exerciseAfter.getActivatedMuscles(), exerciseBefore.getActivatedMuscles());
		assertEquals(exerciseAfter.getActivationMap(), exerciseBefore.getActivationMap());
		assertEquals(exerciseAfter.getAlternativeNames(), exerciseBefore.getAlternativeNames());
		assertEquals(exerciseAfter.getHints(), exerciseBefore.getHints());
		assertEquals(exerciseAfter.getIconPath(), exerciseBefore.getIconPath());
		assertEquals(exerciseAfter.getImageLicenseMap(), exerciseBefore.getImageLicenseMap());
		assertEquals(exerciseAfter.getImagePaths(), exerciseBefore.getImagePaths());
		assertEquals(exerciseAfter.getRequiredEquipment(), exerciseBefore.getRequiredEquipment());
		assertEquals(exerciseAfter.getTags(), exerciseBefore.getTags());
		assertEquals(exerciseAfter.getURLs(), exerciseBefore.getURLs());
		
		
	}

}
