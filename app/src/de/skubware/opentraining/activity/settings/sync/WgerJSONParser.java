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

package de.skubware.opentraining.activity.settings.sync;

import java.io.File;
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

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.basic.License.LicenseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.IDataProvider;

/**
 * A class for parsing the JSON-data from wger.
 *
 */
public class WgerJSONParser {
	
	private List<ExerciseType> mNewExerciseList = new ArrayList<ExerciseType>();
	/* The exercise builder objects, for modifying the exercises after parsing (e.g. changing image) */
	private List<ExerciseType.Builder> mNewExerciseBuilderList = new ArrayList<ExerciseType.Builder>();

	//TODO Find better solution, remove static methods and fields
	private static IDataProvider mDataProvider;
	
	/** Tag for logging */
	private static final String TAG = "WgerJSONParser";
	
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
	public WgerJSONParser(String exerciseJSONString, String languageJSONString, String muscleJSONString, String equipmentJSONString, String licenseJSONString, IDataProvider dataProvider) throws JSONException{
		mDataProvider = dataProvider;
		
		// parse languages
		SparseArray<Locale> localeSparseArray = parseLanguages(languageJSONString);
		// parse muscles
		SparseArray<Muscle> muscleSparseArray = parseMuscles(muscleJSONString);
		// parse licenses
		SparseArray<LicenseType> licenseSparseArray = parseLicenses(licenseJSONString);

		// parse equipment (not required until REST-API supports this)
		// SparseArray<SportsEquipment> equipmentSparseArray = parseEquipment(equipmentJSONString);
		
		
		
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
			
			ExerciseType.Builder builder = new ExerciseType.Builder(name, ExerciseSource.SYNCED);
			
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
			
			// licenses
			// the json-language String might look like this:
			// '/api/v1/license/1/'
			// only the number at the end is required
			
			if(jsonExercise.has("license")){
				int licenseNumber = getLastNumberOfJson(jsonExercise.getString("license"));
				LicenseType licenseType = licenseSparseArray.get(licenseNumber);
				String license_author = jsonExercise.getString("license_author");
				Log.v(TAG, "license=" + licenseType + " license_author=" + license_author);
			}
			
			
			// equipment
			// not yet supported by REST-API
			/*SortedSet<SportsEquipment> equipmentSet = new TreeSet<SportsEquipment>();
			JSONArray equipmentArray = jsonExercise.getJSONArray("equipment");
			for (int l = 0; l < equipmentArray.length(); l++) {
				String equipmentString = equipmentArray.getString(l);
				SportsEquipment equipment = equipmentSparseArray.get(getLastNumberOfJson(equipmentString));
				equipmentSet.add(equipment);
			}*/
			
			
			builder.activatedMuscles(muscleSet);
			// images
			List<File> imageList= new ArrayList<File>();
			JSONArray imageArray = jsonExercise.getJSONArray("images");
			for (int l = 0; l < imageArray.length(); l++) {
				String imageString = imageArray.getString(l);
				imageList.add(new File(imageString));
			}
			builder.imagePath(imageList);
			
			
			mNewExerciseList.add(builder.build());
			mNewExerciseBuilderList.add(builder);
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
	public static int getLastNumberOfJson(String jsonString){
		String[] split = jsonString.split("/");
		return Integer.parseInt(split[split.length - 1]);
	}
	
	/**
	 * Parses the JSON-language(locale)-String and returns an SparseArray that maps the
	 * language numbers to {@link Locale} objects
	 */
	private SparseArray<Locale> parseLanguages(String languagesJSONString) throws JSONException{
		return parse(languagesJSONString, Locale.class);
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
	 */
	private SparseArray<Muscle> parseMuscles(String musclesJSONString) throws JSONException{
		return parse(musclesJSONString, Muscle.class);
	}
	
	public static SparseArray<LicenseType> parseLicenses(String licenseJSONString) throws JSONException{
		return parse(licenseJSONString, LicenseType.class);
	}
	
	private SparseArray<SportsEquipment> parseEquipment(String equipmentJSONString) throws JSONException{
		return parse(equipmentJSONString, SportsEquipment.class);
	}
	
	/**
	 * A generic parsing method for parsing JSON to SportsEquipment, Muscle or Locale.
	 */
	private static <T> SparseArray<T> parse(String jsonString, Class<T> c) throws JSONException{
		JSONObject mainObject = new JSONObject(jsonString);
		Log.d(TAG, "jsonString: " + mainObject.toString());
		JSONArray mainArray = mainObject.getJSONArray("objects");

		SparseArray<T> sparseArray = new SparseArray<T>();
		
		

		// parse each exercise of the JSON Array
		for (int i = 0; i < mainArray.length(); i++) {
			JSONObject singleObject = mainArray.getJSONObject(i);
			
			Integer id = singleObject.getInt("id");
			Object parsedObject;
			if(c.equals(Muscle.class)){
				// handle Muscles
				String name = singleObject.getString("name");
				parsedObject = mDataProvider.getMuscleByName(name);
				
				if(parsedObject == null)
					Log.e(TAG, "Could not find Muscle: " + name);
				
			}else if(c.equals(SportsEquipment.class)){
				// handle SportsEquipment
				String name = singleObject.getString("name");
				parsedObject = mDataProvider.getEquipmentByName(name);
				
				if(parsedObject == null)
					Log.e(TAG, "Could not find SportsEquipment: " + name);
				
			}else if(c.equals(Locale.class)){
				// handle Locales
				String short_name = singleObject.getString("short_name");
				parsedObject = new Locale(short_name);	
				
				if(short_name == null || short_name.equals(""))
					Log.e(TAG, "Error, no short_name=" + short_name);
				
			}else if(c.equals(LicenseType.class)){
				// handle licenses
				String short_name = singleObject.getString("short_name");
				
				parsedObject = mDataProvider.getLicenseTypeByName(short_name);	
				
				
				if(short_name == null || short_name.equals(""))
					Log.e(TAG, "Error, no short_name=" + short_name);
				
			}else{
				throw new IllegalStateException("parse(String, Class<T>) cannot be applied for class: " + c.toString());
			}

			sparseArray.put(id, (T) parsedObject);

			
		}
		
		return sparseArray;
	}

	
	/**
	 * Returns the list with the new exercises.
	 * 
	 * 
	 * @return A list containing all new exercises.
	 */
	public ArrayList<ExerciseType> getNewExercises(){		
		return new ArrayList<ExerciseType>(mNewExerciseList);
	}
	
	/**
	 * Returns the list with the builder objects for the new exercises
	 * 
	 * 
	 * @return A list containing all builder objects for the new exercises
	 */
	public ArrayList<ExerciseType.Builder> getNewExercisesBuilder(){		
		return new ArrayList<ExerciseType.Builder>(mNewExerciseBuilderList);
	}	
	
	
}
