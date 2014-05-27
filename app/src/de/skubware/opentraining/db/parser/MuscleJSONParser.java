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

import de.skubware.opentraining.basic.Muscle;

public class MuscleJSONParser extends AbstractJSONParser<List<Muscle>> {
	/** Tag for logging */
	public static final String TAG = "MuscleJSONParser<>";
	
	// JSON Node names
	private static final String TAG_NAME = "name";
	private static final String TAG_ALTERNATIVE_NAMES = "alternative_names";
	
	private static final String[] TAG_LOCALES = {"de", "en", "it"};
	 
	/**
	 * Parses the JSON-String to a list of {@link Muscle}s.
	 * 
	 * Example for such a .json File:
	 * 
	 *  [{
     *     "de": { "name" : "Bizeps" },
     *     "en": { "name" : "Biceps", "alternative_names":["Biceps muscle"] }
     *   }, ...]
     *   
     * @param jsonString The String to parse.
     * 
     * @return A list of {@link Muscle}s, null if an Error occurs..
 	 * 
	 */
	@Override
	public List<Muscle> parse(String jsonString) {
		List<Muscle> muscleList = new ArrayList<Muscle>();


		
		JSONArray jsonArray;
		try {
			jsonArray = new JSONArray(jsonString);
			
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject muscleObject = jsonArray.getJSONObject(i);
				
				Muscle m = null;

				for(String locale:TAG_LOCALES){
					if(muscleObject.has(locale)){
						JSONObject languageObject = muscleObject.getJSONObject(locale);
						
						// name
						String name = languageObject.getString(TAG_NAME);

						// first name is primary name, all other names are alternative names
						List<String> nameList = new ArrayList<String>();
						nameList.add(name);
						
						// alternative names	
						if(languageObject.has(TAG_ALTERNATIVE_NAMES)){
							JSONArray alternativeNameJSONArray = languageObject.getJSONArray(TAG_ALTERNATIVE_NAMES);
							String[]  alternativeNameArray = this.jsonArrayToStringArray(alternativeNameJSONArray);
							
							for (int t = 0; t < alternativeNameArray.length; t++) {
								String altName = alternativeNameArray[t];
								nameList.add(altName);
							}
						}	

						
						if(m == null){
							m = new Muscle(new Locale(locale), nameList);
						}else{
							m.addNames(new Locale(locale), nameList);
						}
					}
				}
				
				// Log.d(TAG, "Finished parsing Muscle: \n" + m.toDebugString());
				muscleList.add(m);

			}

		} catch (JSONException e) {
			Log.e(TAG, "Error during parsing JSON File.", e);
			return null;
		}

		if(muscleList.isEmpty())
			throw new AssertionError("JSON parsing failed: no muscles parsed.");
		return muscleList;
	}
	
	
	private String[] jsonArrayToStringArray(JSONArray jsonArray){
			return jsonArray.toString().substring(1,jsonArray.toString().length()-1).replaceAll("\"","").split(",");
	}


}
