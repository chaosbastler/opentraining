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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import de.skubware.opentraining.activity.create_workout.upload_exercise.ExerciseImage;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.rest.ServerModel.Equipment;
import de.skubware.opentraining.db.rest.ServerModel.Language;
import de.skubware.opentraining.db.rest.ServerModel.MuscleCategory;

/**
 * Class for serializing {@link ExerciseType} to the wger.de-JSON-format.
 * 
 * The use of static members is not ideal, but should not cause too much
 * problems. Unfortunately GSON/retrofit do not allow to pass the maps as method
 * parameter, so they have to be set via static methods before.
 * 
 */
public class ExerciseImageGSONSerializer implements JsonSerializer<ExerciseImage> {

	private final static String TAG = "ExerciseImageGSONSerializer";

	@Override
	public JsonElement serialize(ExerciseImage ex, Type typeOfSrc, JsonSerializationContext context) {

		JsonObject mainObject = new JsonObject();


			FileInputStream fis = null;
			String imgString = null;

			try {
				fis = new FileInputStream(ex.getRealImagePath());
			} catch (FileNotFoundException e) {
				Log.i(TAG, "File not found: " + ex.getRealImagePath());
				e.printStackTrace();
			}
			
			Bitmap bm = BitmapFactory.decodeStream(fis);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bm.compress(CompressFormat.JPEG, 100, baos);
			byte[] imgByte = baos.toByteArray();
			imgString = Base64.encodeToString(imgByte, Base64.DEFAULT);

			Log.i("Minion", "imgString -> JSONObject SUCCESS");
			mainObject.addProperty("image", imgString);
			
			mainObject.addProperty("license", 3);
			mainObject.addProperty("exercise", 260);

		return mainObject;

	}

}
