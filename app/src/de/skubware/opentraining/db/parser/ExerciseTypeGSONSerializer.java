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


package de.skubware.opentraining.db.parser;

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
			mainObject.addProperty("category", "/api/v1/exercisecategory/8/");
			mainObject.addProperty("comments", "[]");
			mainObject.addProperty("creation_date", "null");
			mainObject.addProperty("description", "Just an example ex for testing uploading.");
			mainObject.addProperty("description", "Just an example ex for testing uploading.");

			JsonArray equipmentJsonArray = new JsonArray();
			equipmentJsonArray.add(new JsonPrimitive("/api/v1/equipment/1/"));
			mainObject.add("equipment", equipmentJsonArray);

			mainObject.addProperty("language", "/api/v1/language/1/");
			mainObject.addProperty("license", "/api/v1/license/1/");
			mainObject.addProperty("license_author", "me ;)");
			mainObject.addProperty("muscles", "[]");
			mainObject.addProperty("secondary", "[]");

			
			mainObject.addProperty("name", "Rest Put Test Exercise");

			return mainObject;
		}

}
