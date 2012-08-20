package de.skubware.opentraining.test.basic;

import java.util.*;

import org.junit.*;
import org.junit.Test;

import de.skubware.opentraining.basic.*;

import junit.framework.TestCase;


public class WorkoutTest extends TestCase {
	
	String TEST_NAME_1 = "Test Workout 1";
	String TEST_NAME_2 = "Test Workout 2";

	List<FitnessExercise> FEX_LIST_1 = new ArrayList<FitnessExercise>();
	List<FitnessExercise> FEX_LIST_2 = new ArrayList<FitnessExercise>();
	
	FitnessExercise FEX_1 = new FitnessExercise(new ExerciseType.Builder("Test Exercise 1").build());
	FitnessExercise FEX_2 = new FitnessExercise(new ExerciseType.Builder("Test Exercise 2").build());
	FitnessExercise FEX_3 = new FitnessExercise(new ExerciseType.Builder("Test Exercise 3").build());

	
	Workout WORKOUT_1;
	Workout WORKOUT_2;

	
	@Before
	public void setUp(){
		FEX_LIST_1.add(FEX_1);
		FEX_LIST_1.add(FEX_2);
		FEX_LIST_1.add(FEX_3);

		
		FEX_LIST_2.add(FEX_2);
		FEX_LIST_2.add(FEX_3);
		
		WORKOUT_1 = new Workout(TEST_NAME_1, FEX_LIST_1);		
		WORKOUT_2 = new Workout(TEST_NAME_2, FEX_LIST_2);
	}
	
	/**
	 * Using @Test(expected=NullPointerException.class) does not work.
	 */
	public void testEmptyConstructorArguments(){
		try{
			new Workout(null);
			fail("Expected NullPointerException");
		}catch(NullPointerException ex){ }
		try{
			new Workout(TEST_NAME_1);
			fail("Expected NullPointerException");
		}catch(NullPointerException ex){ }
		try{
			new Workout(TEST_NAME_1, new FitnessExercise[1]);
			fail("Expected NullPointerException");
		}catch(NullPointerException ex){ }
		try{
			new Workout(TEST_NAME_1, new ArrayList<FitnessExercise>());
			fail("Expected NullPointerException");
		}catch(NullPointerException ex){ }
		try{
			new Workout(TEST_NAME_1, Arrays.asList(new ExerciseType[1]));
			fail("Expected NullPointerException");
		}catch(NullPointerException ex){ }
	}
	
	

	/**
	 * Tests if a constructed object with some FitnessExercises is really as it should be.
	 */
	@Test
	public void testFullWorkout(){
		assertEquals(WORKOUT_1.getName(), TEST_NAME_1);
		
		assertTrue(WORKOUT_1.contains(FEX_1));
		assertTrue(WORKOUT_1.contains(FEX_2));
		assertTrue(WORKOUT_1.contains(FEX_3));

		assertEquals(FEX_1, WORKOUT_1.getFitnessExercises().get(0));
		assertEquals(FEX_2, WORKOUT_1.getFitnessExercises().get(1));
		assertEquals(FEX_3, WORKOUT_1.getFitnessExercises().get(2));
		
		assertEquals(WORKOUT_1.getFitnessExercises().size(), 3);
	}
	
	/**
	 * Tests the equals method of Workout.
	 */
	@Test
	public void testEquals(){
		// test null
		assertFalse(WORKOUT_1.equals(null));
		
		// test with a different workout
		assertFalse(WORKOUT_1.equals(WORKOUT_2));
		assertFalse(WORKOUT_2.equals(WORKOUT_1));
		
		assertEquals(WORKOUT_1, WORKOUT_1);
		assertEquals(WORKOUT_2, WORKOUT_2);

		
		// test with an equal workout
		Workout w_eq_1 = new Workout(TEST_NAME_1, FEX_1);
		w_eq_1.addFitnessExercise(FEX_2);
		w_eq_1.addFitnessExercise(FEX_3);
		
		assertEquals(WORKOUT_1, w_eq_1);
		
		// remove FitnessExercises and add them again => different order => not equal
		w_eq_1.removeFitnessExercise(FEX_2);
		assertFalse(w_eq_1.equals(WORKOUT_1));
		w_eq_1.addFitnessExercise(FEX_2);
		assertFalse(w_eq_1.equals(WORKOUT_1)); // false because the order is different

		// test with a different name
		Workout w_noteq_1 = new Workout("TEST_NAME", FEX_LIST_1);
		assertFalse(WORKOUT_1.equals(w_noteq_1));
		assertFalse(w_noteq_1.equals(WORKOUT_1));
	}


}
