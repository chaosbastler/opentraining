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
