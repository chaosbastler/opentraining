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
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.util.Log;
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
		

		/** @see MuscleCategory#getMuscleMap(MuscleCategory[], Context) */
		public static Map<SportsEquipment,Equipment> getEquipmentMap(ServerModel.Equipment[] equipmentArr, Context context){
			Map<SportsEquipment,Equipment> map = new HashMap<SportsEquipment,Equipment>();
			
			IDataProvider dataProvider = new DataProvider(context);

			
		    for(ServerModel.Equipment cat:equipmentArr){
		    	SportsEquipment parsedObject = dataProvider.getEquipmentByName(cat.name); 
				
				if(parsedObject == null){
					Log.e(TAG, "Could not find SportsEquipment: " + cat.name);
				}else{					
					map.put(parsedObject, cat);
				}
		    }
		
			return map;
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
				}else if(map.get(parsedMuscle) == null){					
					map.put(parsedMuscle, cat);
				}else{
					Log.e(TAG, "Muscle assigned two times, parsedMuscle: " + parsedMuscle.toString() + ", cat: " + cat.name + ", map.get(parsedMuscle):" + map.get(parsedMuscle));
				}
		    }
		
			return map;
		}
		
		@Override
		public String toString(){
			return name + id;
		}
		

	}
	
	
	/**
	 * Server equivalent to {@link Locale}.
	 */
	public static class Language{



		public int id;
		public String short_name;
		public String full_name;


		/**
		 * Maps the server model of models with the app model of muscles.
		 * 
		 * @param categoryArr All muscles on server
		 * @param context Current app context
		 * @return A map of the muscle models
		 */
		public static Map<Locale,Language> getLanguageMap(ServerModel.Language[] languageArr, Context context){
			Map<Locale,Language> map = new HashMap<Locale,Language>();
			
			
		    for(ServerModel.Language l:languageArr){
		    	Locale parsedLocale = new Locale(l.short_name);
						
				map.put(parsedLocale, l);
		    }
		
			return map;
		}

	}
}
