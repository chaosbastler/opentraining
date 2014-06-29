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

package de.skubware.opentraining.db.rest;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

public class ServerModel {
	private static String TAG = "ServerModel";
	
	
	/**
	 * Server equivalent to {@link Equipment}.
	 */
	public static class Equipment{
		public int id;
		public String name;
		
		public SportsEquipment asSportsEquipment(Context context){
			IDataProvider dataProvider = new DataProvider(context);
			SportsEquipment eq = dataProvider.getEquipmentByName(name); 
			
			if(eq== null){
				Log.e(TAG, "Could not find Equipment: " + name);
			}
			
			return eq;
		}
		
		public static SparseArray<SportsEquipment> toSportsEquipmentSparseArray(ServerModel.Equipment[] oldArray, Context context){		
			SparseArray<SportsEquipment> arr = new SparseArray<SportsEquipment>();

		    for(ServerModel.Equipment eq:oldArray){
				SportsEquipment parsedEquipment = eq.asSportsEquipment(context);
				if(eq != null)
					arr.put(eq.id, parsedEquipment);
		    }
		
			return arr;
		}

	}
	
	
	
	/**
	 * Server equivalent to {@link Muscle}.
	 */
	public static class MuscleCategory{
		public int id;
		public String name;
		

		/**
		 * Maps the server model of models with the app model of muscles.
		 * 
		 * @param categoryArr All muscles on server
		 * @param context Current app context
		 * @return A map of the muscle models
		 */
		public static Map<Muscle,MuscleCategory> getMuscleMap(ServerModel.MuscleCategory[] categoryArr, Context context){
			Map<Muscle,MuscleCategory> map = new HashMap<Muscle,MuscleCategory>();
			
			IDataProvider dataProvider = new DataProvider(context);

			
		    for(ServerModel.MuscleCategory cat:categoryArr){
		    	Muscle parsedMuscle = dataProvider.getMuscleByName(cat.name); 
				
				if(parsedMuscle == null){
					Log.e(TAG, "Could not find Muscle: " + cat.name);
				}else{					
					map.put(parsedMuscle, cat);
				}
		    }
		
			return map;
		}

	}
	
}
