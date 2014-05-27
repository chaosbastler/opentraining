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

package de.skubware.opentraining.test.datamodel;

import android.test.AndroidTestCase;
import de.skubware.opentraining.basic.FSet;
import de.skubware.opentraining.basic.FSet.SetParameter;
import de.skubware.opentraining.basic.FSet.SetParameter.*;

/**
 * Tests for {@link FSet}.
 *
 */
public class FSetTest extends AndroidTestCase {
	/** Tag for logging */
	public static final String TAG = "FSetTest";
	

	public void testEquals(){

		// these objects will be compared
		Repetition REP_1 = new Repetition(1);
		Duration DUR_1 = new Duration(30);
		Weight WEIGHT_1 = new Weight(15);
		FreeField FREE_FIELD_1 = new FreeField("6t5");

		// these objects should be equal
		Repetition REP_2 = new Repetition(1);
		Duration DUR_2 = new Duration(30);
		Weight WEIGHT_2 = new Weight(15);
		FreeField FREE_FIELD_2 = new FreeField("6t5");
		
		// these objects should not be equal
		Repetition REP_3 = new Repetition(11);
		Duration DUR_3 = new Duration(301);
		Weight WEIGHT_3 = new Weight(151);
		FreeField FREE_FIELD_3 = new FreeField("6t51");
		

		assertEquals(REP_1, REP_2);
		assertEquals(DUR_1, DUR_2);
		assertEquals(WEIGHT_1, WEIGHT_2);
		assertEquals(FREE_FIELD_1, FREE_FIELD_2);
		
		assertFalse(REP_1.equals(REP_3));
		assertFalse(DUR_1.equals(DUR_3));
		assertFalse(WEIGHT_1.equals(WEIGHT_3));
		assertFalse(FREE_FIELD_1.equals(FREE_FIELD_3));

		
	}
	
	public void testClone(){
		// first test "cloning" of SetParameters via constructor
		Repetition REP_1 = new Repetition(1);
		Duration DUR_1 = new Duration(30);
		Weight WEIGHT_1 = new Weight(15);
		FreeField FREE_FIELD_1 = new FreeField("6t5");
		
		
		Repetition REP_2 = new Repetition(REP_1);
		Duration DUR_2 = new Duration(DUR_1);
		Weight WEIGHT_2 = new Weight(WEIGHT_1);
		FreeField FREE_FIELD_2 = new FreeField(FREE_FIELD_1);
		
		assertFalse(REP_1 == REP_2);
		assertEquals(REP_1, REP_2);
		assertFalse(DUR_1 == DUR_2);
		assertEquals(DUR_1, DUR_2);
		assertFalse(WEIGHT_1 == WEIGHT_2);
		assertEquals(WEIGHT_1, WEIGHT_2);
		assertFalse(FREE_FIELD_1 == FREE_FIELD_2);
		assertEquals(FREE_FIELD_1, FREE_FIELD_2);
		
		FSet SET1 = new FSet(REP_1, DUR_1, WEIGHT_1, FREE_FIELD_1);
		FSet SET2 = new FSet(REP_2, DUR_2, WEIGHT_2, FREE_FIELD_2);
		assertEquals(SET1, SET2);
		
		FSet SET2_CLONE = (FSet) SET2.clone();
		assertEquals(SET2_CLONE, SET2);
		assertFalse(SET2_CLONE == SET2);
		for(SetParameter para:SET2_CLONE.getSetParameters()){
			for(SetParameter para2:SET2.getSetParameters()){
				assertFalse(para == para2);
			}
		}

		
	}

}
