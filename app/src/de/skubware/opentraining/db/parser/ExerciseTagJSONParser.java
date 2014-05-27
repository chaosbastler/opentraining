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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import de.skubware.opentraining.basic.ExerciseTag;

public class ExerciseTagJSONParser extends AbstractJSONParser<List<ExerciseTag>> {
	/** Tag for logging */
	public static final String TAG = "ExerciseTagJSONParser";
	
	// JSON Node names
	private static final String TAG_NAME = "name";
	private static final String TAG_DESCRIPTION = "description";
	
	private static final String[] TAG_LOCALES = {"de", "en", "it"};
	 
	/**
	 * Parses the JSON-String to a list of {@link ExerciseTag}s.
	 * 
     *   
     * @param jsonString The String to parse.
     * 
     * @return A list of {@link ExerciseTag}s, null if an error occurs.
 	 * 
	 */
	@Override
	public List<ExerciseTag> parse(String jsonString) {
		List<ExerciseTag> exerciseTagList = new ArrayList<ExerciseTag>();


		
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject SportsEquipmentObject = jsonArray.getJSONObject(i);
				
				ExerciseTag exerciseTag = null;

				for(String locale:TAG_LOCALES){
					if(SportsEquipmentObject.has(locale)){
						JSONObject languageObject = SportsEquipmentObject.getJSONObject(locale);
						
						// name
						String name = languageObject.getString(TAG_NAME);
						List<String> nameList = new ArrayList<String>();
						nameList.add(name);
						
						String description = null;
						
						// description	
						if(languageObject.has(TAG_DESCRIPTION)){
							//JSONObject descriptionJSONObject = languageObject.getJSONObject(TAG_DESCRIPTION);
							
							// description
							description = languageObject.getString(TAG_DESCRIPTION);
							
						}	

						
						if(exerciseTag == null){
							exerciseTag = new ExerciseTag(new Locale(locale), nameList, description);
						}else{
							exerciseTag.addNames(new Locale(locale), nameList, description);
						}
					}
				}
				
				// Log.d(TAG, "Finished parsing ExerciseTag: \n" + exerciseTag.toDebugString());
				exerciseTagList.add(exerciseTag);

			}

		} catch (JSONException e) {
			Log.e(TAG, "Error during parsing JSON File.", e);
			return null;
		}

		if(exerciseTagList.isEmpty())
			throw new AssertionError("JSON parsing failed: no ExerciseTag parsed.");
		return exerciseTagList;
	}


}
