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
