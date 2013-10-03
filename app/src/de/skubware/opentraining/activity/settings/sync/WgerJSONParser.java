/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Christian Skubich
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

package de.skubware.opentraining.activity.settings.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A class for parsing the JSON-data from wger.
 *
 */
public class WgerJSONParser {
	
	private List<ExerciseType> mNewExerciseList = new ArrayList<ExerciseType>();
	
	private IDataProvider mDataProvider;
	
	/** Tag for logging */
	private static final String TAG = "ExerciseJSONParser";
	
	/**
	 * Constructor. Will start download immediately.
	 * 
	 * @param exerciseJSONString
	 *            The exercises as JSON-String
	 * @param languageJSONString
	 *            The languages as JSON-String
	 * @param muscleJSONString
	 *            The muscles as JSON-String
	 * @param dataProvider
	 * @throws JSONException
	 */
	public WgerJSONParser(String exerciseJSONString, String languageJSONString, String muscleJSONString, IDataProvider dataProvider) throws JSONException{
		mDataProvider = dataProvider;
		
		// parse languages
		SparseArray<Locale> localeSparseArray = parseLanguages(languageJSONString);
		// parse muscles
		SparseArray<Muscle> muscleSparseArray = parseMuscles(muscleJSONString);
		
		
		
		JSONObject mainObject = new JSONObject(exerciseJSONString);
		Log.d(TAG, mainObject.toString());
		JSONArray exerciseArray = mainObject.getJSONArray("objects");

		// parse each exercise of the JSON Array
		for (int i = 0; i < exerciseArray.length(); i++) {
			
			JSONObject jsonExercise = exerciseArray.getJSONObject(i);
			// get name and check if exercise already exists
			String name = jsonExercise.getString("name");
			if(dataProvider.exerciseExists(name))
				continue;

			// status - used for sorting out not authorized/refused exercises
			// (1: not authorized, means that a user suggested it but it hasn't been
			// checked by the admin yet; 3: refused)
			Integer status = jsonExercise.getInt("status");
			if(status == 1 || status == 3)
				continue;
			
			
			ExerciseType.Builder builder = new ExerciseType.Builder(name);
			
			// category (unused)
			// String category = jsonExercise.getString("category");
			
			// comments (unused)
			// JSONArray commentArray = jsonExercise.getJSONArray("comments");
			// for (int k = 0; k < commentArray.length(); k++) {
			//	String comment = commentArray.getString(k);
			//}
			
			// description
			String description = jsonExercise.getString("description");
			builder.description(description);
			
			// id (unused)
			//String id = jsonExercise.getString("id");
			
			// language
			// the json-language String might look like this:
			// '/api/v1/language/1/'
			// only the number at the end is required
			String language = jsonExercise.getString("language");
			int languageNumber = getLastNumberOfJson(language);
			
			Map<Locale, String> translationMap = new HashMap<Locale, String>();
			translationMap.put(localeSparseArray.get(languageNumber), name);
			builder.translationMap(translationMap);
			
			// resource_uri (unused)
			//String resource_uri = jsonExercise.getString("resource_uri");
			

			// muscles
			SortedSet<Muscle> muscleSet = new TreeSet<Muscle>();
			JSONArray muscleArray = jsonExercise.getJSONArray("muscles");
			for (int l = 0; l < muscleArray.length(); l++) {
				String muscleString = muscleArray.getString(l);
				Muscle muscle = muscleSparseArray.get(getLastNumberOfJson(muscleString));
				muscleSet.add(muscle);
			}
			builder.activatedMuscles(muscleSet);

			
			mNewExerciseList.add(builder.build());
		}

	}
	
	/**
	 * Returns the last number of a JSON-String.
	 * 
	 * E.g.: for '/api/v1/something/1/' '1' will be returned.
	 * 
	 * @param jsonString
	 * 
	 * @return the last number of a JSON-String
	 */
	private int getLastNumberOfJson(String jsonString){
		String[] split = jsonString.split("/");
		return Integer.parseInt(split[split.length - 1]);
	}
	
	/**
	 * Parses the JSON-language(locale)-String and returns an SparseArray that maps the
	 * muscle numbers to {@link Locale} objects
	 * 
	 * @param languagesJSONString
	 * @return
	 * 
	 * @throws JSONException
	 */
	private SparseArray<Locale> parseLanguages(String languagesJSONString) throws JSONException{
		JSONObject mainObject = new JSONObject(languagesJSONString);
		Log.d(TAG, "language JSON: " + languagesJSONString);
		JSONArray languageArray = mainObject.getJSONArray("objects");

		SparseArray<Locale> languageSparseArray = new SparseArray<Locale>();
		
		// parse each exercise of the JSON Array
		for (int i = 0; i < languageArray.length(); i++) {
			JSONObject languageObject = languageArray.getJSONObject(i);
			String full_name = languageObject.getString("full_name");
			String short_name = languageObject.getString("short_name");
			Integer id = languageObject.getInt("id");
			
			Locale locale = new Locale(short_name);
			
			languageSparseArray.put(id, locale);
			Log.v(TAG, "parsed language, full_name" + full_name + ",short_name: " + short_name + ", id: " + id + ", Locale: " + locale.toString());
		}
		
		return languageSparseArray;
	}
	
	/**
	 * Parses the JSON-muscle-String and returns an SparseArray that maps the
	 * muscle numbers to {@link Muscle} objects
	 * 
	 * Example for muscle JSON:
	 * 
	 * {"meta": {"limit": 20, "next": null, "offset": 0, "previous": null,
	 * "total_count": 15}, "objects": [{"id": 2, "is_front": true, "name":
	 * "Anterior deltoid", "resource_uri": "/api/v1/muscle/2/"}]}
	 * 
	 * @param musclesJSONString
	 * @return
	 */
	private SparseArray<Muscle> parseMuscles(String musclesJSONString) throws JSONException{
		JSONObject mainObject = new JSONObject(musclesJSONString);
		Log.d(TAG, "muscle JSON: " + mainObject.toString());
		JSONArray muscleArray = mainObject.getJSONArray("objects");

		SparseArray<Muscle> muscleSparseArray = new SparseArray<Muscle>();
		
		// parse each exercise of the JSON Array
		for (int i = 0; i < muscleArray.length(); i++) {
			JSONObject languageObject = muscleArray.getJSONObject(i);
			String name = languageObject.getString("name");
			// unused
			// Boolean is_front = languageObject.getBoolean("is_front");
			// unused
			// String resource_uri = languageObject.getString("resource_uri");
			Integer id = languageObject.getInt("id");
			
			Muscle muscle = mDataProvider.getMuscleByName(name);
			if(muscle == null)
				Log.e(TAG, "Could not find muscle: " + name);
			
			muscleSparseArray.put(id, muscle);
		}
		
		return muscleSparseArray;
	}

	
	/**
	 * Returns the list with the new exercises.
	 * 
	 * 
	 * @param withImagesOnly
	 *            only exercises with images will be returned
	 * @param localizedOnly
	 *            only exercises that are localized (that means there's a
	 *            translation for the Locale returned by Locale.getDefault())
	 * 
	 * @return A list containing the specified new exercises.
	 */
	public ArrayList<ExerciseType> getNewExercises(boolean withImagesOnly, boolean localizedOnly){
		ArrayList<ExerciseType> filteredExerciseList =  new ArrayList<ExerciseType>(mNewExerciseList);
		
		for(ExerciseType exercise:mNewExerciseList){
			// remove exercises without images
			if(withImagesOnly && exercise.getImagePaths().isEmpty()){
				filteredExerciseList.remove(exercise);
				continue;
			}
			
			// remove unlocalized exercises
			if(localizedOnly && !exercise.getTranslationMap().keySet().contains(Locale.getDefault())){
				filteredExerciseList.remove(exercise);
				continue;
			}
		}
		
		
		return filteredExerciseList;
	}
	
	
	
}
