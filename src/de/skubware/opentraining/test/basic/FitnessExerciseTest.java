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

package de.skubware.opentraining.test.basic;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.basic.FSet.Category.*;

public class FitnessExerciseTest {
	
	String TEST_NAME_1 = "Test Exercise 1";
	String TEST_NAME_2 = "Test Exercise 2";

	ExerciseType EX_1 = new ExerciseType.Builder(TEST_NAME_1).build();
	ExerciseType EX_2 = new ExerciseType.Builder(TEST_NAME_2).build();

		
	FSet FSET_1 = new FSet(new Repetition(10));
	FSet[] FSET_ARR1 = {FSET_1};
	
	FSet FSET_2 = new FSet(new Repetition(20), new Weight(20));
	FSet FSET_3 = new FSet(new Repetition(30), new Weight(30));
	FSet[] FSET_ARR2 = {FSET_2, FSET_3};


	
	FitnessExercise FEX_1;
	FitnessExercise FEX_2;



	@Before
	public void setUp(){
		FEX_1 = new FitnessExercise(EX_1, FSET_ARR1);
		FEX_2 = new FitnessExercise(EX_2, FSET_ARR2);
	}
	


	@Test
	public void testEmptyConstructorArgument() {
		try{
			new FitnessExercise(null);
			fail();
		}catch(NullPointerException nullex){ /* expected this */ }
		
		try{
			new FitnessExercise(EX_1, (FSet[]) null);
			fail();
		}catch(NullPointerException nullex){ /* expected this */ }

		try{
			new FitnessExercise(EX_1, (FSet) null);
			fail();
		}catch(NullPointerException nullex){ /* expected this */ }
	}
	
	@Test
	public void testGetter(){
		assertEquals(FEX_1.getExType(), EX_1);
		assertEquals(FEX_2.getExType(), EX_2);
		
		assertTrue( FEX_1.getFSetList().size()== FSET_ARR1.length && FEX_1.getFSetList().containsAll(Arrays.asList(FSET_ARR1)));
		assertTrue( FEX_2.getFSetList().size()== FSET_ARR2.length && FEX_2.getFSetList().containsAll(Arrays.asList(FSET_ARR2)));

	}
	
	
	@Test
	public void testEquals(){
		// test null
		assertFalse(FEX_1.equals(null));
		
		// test with a different fitnessexercise
		assertFalse(FEX_1.equals(FEX_2));
		assertFalse(FEX_2.equals(FEX_1));
		
		assertEquals(FEX_1, FEX_1);
		assertEquals(FEX_2, FEX_2);

		
		// test with an equal fitnessexercise
		FitnessExercise fex_eq_1 = new FitnessExercise(EX_1, FSET_ARR1);
		
		assertEquals(FEX_1, fex_eq_1);
		

		// test with a different exercise
		FitnessExercise fex_noteq_1 = new FitnessExercise(EX_2, FSET_ARR1);
		assertFalse(FEX_1.equals(fex_noteq_1));
		assertFalse(fex_noteq_1.equals(FEX_1));	
		
		// test with a different fset
		FitnessExercise fex_noteq_2 = new FitnessExercise(EX_2, FSET_ARR1);
		assertFalse(FEX_2.equals(fex_noteq_2));
		assertFalse(fex_noteq_2.equals(FEX_2));	
		
	}
	


}
