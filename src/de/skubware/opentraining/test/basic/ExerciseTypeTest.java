package de.skubware.opentraining.test.basic;

import static org.junit.Assert.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.skubware.opentraining.basic.*;

/**
 * Unit test for class ExerciseType.
 * 
 * @author Christian Skubich
 * 
 */
public class ExerciseTypeTest {

	ExerciseType EX_1;
	ExerciseType EX_2;

	final String EX_1_NAME = "Exercise ONE";
	final String EX_2_NAME = "Exercise TWO";

	/**
	 * 
	 * @throws Exception
	 */
	@Before
	public void setUp() throws Exception {
		Set<ExerciseType> list = new TreeSet<ExerciseType>(ExerciseType.listExerciseTypes());
		for (ExerciseType ex : list) {
			ExerciseType.removeExerciseType(ex);
		}

		EX_1 = new ExerciseType.Builder(EX_1_NAME).build();
		EX_2 = new ExerciseType.Builder(EX_2_NAME).build();
	}

	@After
	public void tearDown() throws Exception {
		assertTrue(ExerciseType.removeExerciseType(EX_1));
		assertTrue(ExerciseType.removeExerciseType(EX_2));
	}

	@Test
	public void testConstructor() {
		// constructor must be private
		try {
			Constructor<ExerciseType> cons = ExerciseType.class.getConstructor();
			assertTrue(Modifier.isPrivate(cons.getModifiers()));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testHashCode() {
		assertEquals(EX_1_NAME.hashCode(), EX_1.hashCode());
		assertEquals(EX_2_NAME.hashCode(), EX_2.hashCode());
	}

	@Test
	public void testListExerciseTypes() {
		String exes = "";
		for (ExerciseType ex : ExerciseType.listExerciseTypes()) {
			exes += ex.toString();
			exes += "\n";
		}
		assertEquals(exes, 2, ExerciseType.listExerciseTypes().size());

		assertTrue(ExerciseType.listExerciseTypes().contains(EX_1));
		assertTrue(ExerciseType.listExerciseTypes().contains(EX_2));

	}

	@Test
	public void testGetName() {
		assertEquals(EX_1_NAME, EX_1.getName());
		assertEquals(EX_2_NAME, EX_2.getName());
	}

	@Test
	public void testGetDescription() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetImagePaths() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetIconPath() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetImageLicenseMap() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetImageWidth() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetImageHeight() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetRequiredEquipment() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivatedMuscles() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetActivationMap() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetTags() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetURLs() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetHints() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetByName() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveExerciseType() {
		ExerciseType.removeExerciseType(EX_1);
		for(Method m:ExerciseType.class.getMethods()){
			if(m.getParameterTypes().length==0){
				try {
					m.invoke(EX_1);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
			}	
		}
	}

	@Test
	public void testEqualsObject() {
		assertEquals(EX_1_NAME, new ExerciseType.Builder(EX_1_NAME).build());
		assertEquals(EX_2_NAME, new ExerciseType.Builder(EX_2_NAME).build());
	}

	@Test
	public void testToString() {
		assertEquals(EX_1_NAME, EX_1.toString());
		assertEquals(EX_2_NAME, EX_2.toString());
	}

	@Test
	public void testCompareTo() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetMd5() {
		fail("Not yet implemented");
	}

	@Test
	public void testAsFitnessExercise() {
		fail("Not yet implemented");
	}

	@Test
	public void testAsFitnessExerciseListOfExerciseType() {
		fail("Not yet implemented");
	}

}
