package de.skubware.opentraining.test.basic;

import org.junit.Test;

import de.skubware.opentraining.basic.Muscle;
import junit.framework.TestCase;

public class MuscleTest extends TestCase {

	/**
	 * Tests if the naming conventions of the enums are respected.
	 * That means that enum.getName() returns the name of the enum
	 * in lower case, except the first character.
	 * Example: Oberschenkelmuskel -> OBERSCHENKELMUSKEL,
	 */
	@Test
	public void testGetName() {
		for(Muscle m1:Muscle.values()){
			char[] arr = m1.getName().toCharArray();
			for(int i = 0; i<arr.length; i++){
				arr[i] = Character.toUpperCase(arr[i]);
			}
			Muscle m2 = Enum.valueOf(Muscle.class, String.valueOf(arr));
			assertEquals(m2, m1);
		}
	}

	@Test
	public void testToString() {
		for(Muscle m:Muscle.values()){
			assertEquals(m.getName(), m.toString());
		}
	}

	/**
	 * Tests if the getByName() method works correctly
	 * and throws an exception when calling with
	 * illegal arguments.
	 */
	@Test
	public void testGetByName() {
		for(Muscle m:Muscle.values()){
			assertEquals(m, Muscle.getByName(m.getName()));
		}
		
		try{
			Muscle.getByName("Open Training");
		}catch(IllegalArgumentException ex){
			// expected this exception
		}
		
		
	}

}
