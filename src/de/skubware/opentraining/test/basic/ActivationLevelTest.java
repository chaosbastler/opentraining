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

import org.junit.Test;

import de.skubware.opentraining.basic.ActivationLevel;

public class ActivationLevelTest {

	@Test
	public void testLevelBorders(){
		assertTrue(ActivationLevel.MAX_LEVEL>ActivationLevel.MIN_LEVEL);
		assertEquals(ActivationLevel.MAX_LEVEL, 5);
		assertEquals(ActivationLevel.MIN_LEVEL, 1);
	
		for(ActivationLevel level:ActivationLevel.values()){
			assertTrue(level.getLevel() <= ActivationLevel.MAX_LEVEL);
			assertTrue(level.getLevel() >= ActivationLevel.MIN_LEVEL);
		}	
	}
	
	
	@Test
	public void testGetByLevelMethod(){
		ActivationLevel low = ActivationLevel.getByLevel(1);
		assertEquals(low.getLevel(), 1);
		
		ActivationLevel medium = ActivationLevel.getByLevel(3);
		assertEquals(medium.getLevel(), 3);
		
		ActivationLevel high = ActivationLevel.getByLevel(5);
		assertEquals(high.getLevel(), 5);
		
		
		int[] ILLEGAL_LEVELS = {-1000, -42, -2, -1, 6, 7, 42, 100, 1000, Integer.MAX_VALUE, Integer.MIN_VALUE};
		for(int i: ILLEGAL_LEVELS){
			try{
				ActivationLevel.getByLevel(i);
				fail("There shouldn't be a level for the value:" + i);
			}catch(IllegalArgumentException illex){ /* expected this */	}
		}

	}
	


}
