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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.rest.ServerModel.Equipment;
import de.skubware.opentraining.db.rest.ServerModel.Language;
import de.skubware.opentraining.db.rest.ServerModel.MuscleCategory;

/**
 *  Class for serializing {@link ExerciseType} to the wger.de-JSON-format.
 * 
 *  The use of static members is not ideal, but should not cause too much problems.
 *  Unfortunately GSON/retrofit do not allow to pass the maps as method parameter,
 *  so they have to be set via static methods before.
 *
 */
public class ExerciseTypeGSONSerializer implements JsonSerializer<ExerciseType>{
	
	private final static String TAG = "ExerciseTypeGSONSerializer";
	
	private static Map<Muscle,MuscleCategory> sMuscleMap;
	private static Map<Locale,Language> sLanguageMap;
	private static Map<SportsEquipment,Equipment> sEquipmentMap;

	
	@Override
	public JsonElement serialize(ExerciseType ex, Type typeOfSrc, JsonSerializationContext context) {
		
		if(sMuscleMap == null || sLanguageMap == null || sEquipmentMap == null) 
			throw new NullPointerException("At least one map in " + TAG + " has not been initialized");
		

		JsonObject mainObject = new JsonObject();

		// description
		if (ex.getDescription() == null || ex.getDescription().length() < 40 ) {
			throw new IllegalArgumentException("Exercise description has to be at least 40 characters.");
		}
		mainObject.addProperty("description", ex.getDescription());

		
		
		// muscle category, required
		MuscleCategory cat = null;
		if(ex.getActivatedMuscles().isEmpty()){
			throw new IllegalArgumentException("You have to choose at least one muscle.");
		}
		
		cat = sMuscleMap.get(ex.getActivatedMuscles().first());
		
		if(cat != null){
			mainObject.addProperty("category", cat.id);
		}else{
			Log.e(TAG, "Did not find muscle: " + ex.getActivatedMuscles().first().toString() + ", sMuscleMap size:  " +  sMuscleMap.size());
		}
		
		// language
		Map<Locale,String> translationMap = ex.getTranslationMap();
		Locale chosenLocale = null;
		for(Locale l:translationMap.keySet()){
			if(translationMap.get(l).equals(ex.getLocalizedName())){
				chosenLocale = l;
				break;
			}
		}
		Language l = sLanguageMap.get(chosenLocale);
		if(l == null){
			Log.e(TAG, "Could not find any fitting locale. Will use English.");
			l = sLanguageMap.get(new Locale("en"));
		}
		
		mainObject.addProperty("language", l.id);

		mainObject.addProperty("name", ex.getLocalizedName());

		
		// JsonArray equipmentJsonArray = new JsonArray();
		// equipmentJsonArray.add(new JsonPrimitive(4));
		// mainObject.add("equipment", equipmentJsonArray);

		mainObject.addProperty("license", 3);
		/*
		 * mainObject.addProperty("license_author", "me ;)");
		 * mainObject.addProperty("muscles", "[]");
		 * mainObject.addProperty("secondary", "[]");
		 */


		return mainObject;
	}
	
	
	
	public static void setMuscleMap(Map<Muscle,MuscleCategory> muscleMap){
		sMuscleMap = new HashMap<Muscle,MuscleCategory>(muscleMap);
	}
	
	public static void setLanguageMap(Map<Locale,Language> languageMap){
		sLanguageMap = languageMap;
	}
	
	public static void setEquipmentMap(Map<SportsEquipment,Equipment> equipmentMap){
		sEquipmentMap = equipmentMap;
	}
	

}
