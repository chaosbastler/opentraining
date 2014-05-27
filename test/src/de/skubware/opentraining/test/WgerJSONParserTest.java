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

package de.skubware.opentraining.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONException;


import android.test.InstrumentationTestCase;
import android.util.Log;
import de.skubware.opentraining.activity.settings.sync.WgerJSONParser;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

/**
 * Tests for the Parsers for {@link Muscle}, {@link SportsEquipment},
 * {@link ExerciseType} and {@link Workout}.
 * 
 * 
 */
public class WgerJSONParserTest extends InstrumentationTestCase {
	/** Tag for logging */
	public static final String TAG = "WgerJSONParserTest";
	
	
	final String WORKOUT_NAME = "TEST_woRkout!=0";
	
	final String EXERCISE_NAME_1 = "Übung Nummer 1";
	final String EXERCISE_NAME_2 = "Übung Nummer 2";
	final String EXERCISE_NAME_3 = "Übung Nummer 3";
	
	final String DESCRIPTION_1 = "<ul>\n<li> Irgendeine Beschreibung </li>\n</ul>";
	final String DESCRIPTION_2 = "some description 2";
	final String DESCRIPTION_3 = "some description 3";
	
	final String MUSCLE_1_1 = "Rectus abdominis";
	final String MUSCLE_1_2 = "Gluteus maximus";
	final String MUSCLE_2_1 = "Triceps brachii";
	final String MUSCLE_3_1 = "Gluteus maximus";
	final String MUSCLE_3_2 = "Quadriceps femoris";
	
	
	
	public void testParseExerciseTypeXML() throws JSONException, IOException{		
		IDataProvider dataProvider = new DataProvider(getInstrumentation().getTargetContext());
		WgerJSONParser parser = new WgerJSONParser(readFile(R.raw.exampel_wger_exercises), readFile(R.raw.exampel_wger_languages), readFile(R.raw.exampel_wger_muscles), readFile(R.raw.exampel_wger_equipment), readFile(R.raw.exampel_wger_licenses), dataProvider); 
		
		// the parser should return three exercises
		ExerciseType EXERCISE_ONE_PARSED = null;
		ExerciseType EXERCISE_TWO_PARSED = null;
		ExerciseType EXERCISE_THREE_PARSED = null;
		
		ArrayList<ExerciseType> exerciseList = parser.getNewExercises();
		for(ExerciseType ex:exerciseList){
			Log.d(TAG, "WgerJSONParser returned exercise: " + ex.toString());
			if(ex.getUnlocalizedName().equals(EXERCISE_NAME_1)){
				EXERCISE_ONE_PARSED = ex;
			}
			if(ex.getUnlocalizedName().equals(EXERCISE_NAME_2)){
				EXERCISE_TWO_PARSED = ex;
			}
			if(ex.getUnlocalizedName().equals(EXERCISE_NAME_3)){
				EXERCISE_THREE_PARSED = ex;
			}
		}
		assertNotNull(EXERCISE_ONE_PARSED);
		assertNotNull(EXERCISE_TWO_PARSED);
		assertNotNull(EXERCISE_THREE_PARSED);
		assertEquals(3, exerciseList.size());
		
		// now check if the 3 exercises were parsed correctly
		SortedSet<Muscle> muscles_1 = new TreeSet<Muscle>();
		muscles_1.add(dataProvider.getMuscleByName(MUSCLE_1_1));
		muscles_1.add(dataProvider.getMuscleByName(MUSCLE_1_2));
		ExerciseType EXERCISE_ONE = new ExerciseType.Builder(EXERCISE_NAME_1, ExerciseSource.SYNCED).description(DESCRIPTION_1).activatedMuscles(muscles_1).build();
		// only equals is not enough as ExerciseType.equals() only compares the name of the exercises
		assertEquals(EXERCISE_ONE, EXERCISE_ONE_PARSED);
		assertEquals(EXERCISE_ONE.getDescription(), EXERCISE_ONE_PARSED.getDescription());
		assertEquals(EXERCISE_ONE.getActivatedMuscles(), EXERCISE_ONE_PARSED.getActivatedMuscles());

	
		SortedSet<Muscle> muscles_2 = new TreeSet<Muscle>();
		muscles_2.add(dataProvider.getMuscleByName(MUSCLE_2_1));
		ExerciseType EXERCISE_TWO = new ExerciseType.Builder(EXERCISE_NAME_2, ExerciseSource.SYNCED).description(DESCRIPTION_2).activatedMuscles(muscles_2).build();
		// only equals is not enough as ExerciseType.equals() only compares the name of the exercises
		assertEquals(EXERCISE_TWO, EXERCISE_TWO_PARSED);
		assertEquals(EXERCISE_TWO.getDescription(), EXERCISE_TWO_PARSED.getDescription());
		assertEquals(EXERCISE_TWO.getActivatedMuscles(), EXERCISE_TWO_PARSED.getActivatedMuscles());
	}

	private String readFile(int id) throws IOException {
		BufferedReader br = new BufferedReader(new InputStreamReader(getInstrumentation().getContext().getResources().openRawResource(id)));
		StringBuilder sb = new StringBuilder();
		try {
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				sb.append('\n');
				line = br.readLine();
			}
		} finally {
			br.close();
		}
		return sb.toString();
	}


}
