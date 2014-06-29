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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.skubware.opentraining.basic.ExerciseType;

public class ExerciseTypeGSONSerializer implements JsonSerializer<ExerciseType>{

		@Override
		public JsonElement serialize(ExerciseType ex, Type typeOfSrc, JsonSerializationContext context) {
			
			JsonObject mainObject = new JsonObject();
			mainObject.addProperty("category", 10);
			
			if(ex.getDescription() != null && !ex.getDescription().equals("")){
				mainObject.addProperty("description", ex.getDescription());
			}else{
				mainObject.addProperty("description", "-");
			}

			//JsonArray equipmentJsonArray = new JsonArray();
			//equipmentJsonArray.add(new JsonPrimitive(4));
			//mainObject.add("equipment", equipmentJsonArray);

			mainObject.addProperty("language", 1);
			mainObject.addProperty("license", 1);
			/*mainObject.addProperty("license_author", "me ;)");
			mainObject.addProperty("muscles", "[]");
			mainObject.addProperty("secondary", "[]");
			*/
			
			mainObject.addProperty("name", ex.getUnlocalizedName());

			return mainObject;
		}

}
