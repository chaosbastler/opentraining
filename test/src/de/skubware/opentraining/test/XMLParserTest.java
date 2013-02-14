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
import de.skubware.opentraining.db.DataProvider;

/**
 * Tests for the Parsers for {@link Muscle} and {@link ExerciseType}.
 *
 * @author Christian Skubich
 *
 */
public class XMLParserTest extends AndroidTestCase {
	
	final String EXERCISE_NAME = "exercise";

	
	public void testParseMuscleJSON(){
		DataProvider dataProvider = new DataProvider(getContext());
		List<Muscle> muscleList = dataProvider.getMuscles();
		assertFalse(muscleList.isEmpty());
		
		String[] backNames = { "Rückenmuskeln", "Rückenmuskel", "Rücken", "Back muscle", "Back"  };
		Muscle back = dataProvider.getMuscleByName(backNames[0]);
		assertNotNull(back);
		for(String backName:backNames){
			assertEquals(back,dataProvider.getMuscleByName(backName));
		}	
	}

}
