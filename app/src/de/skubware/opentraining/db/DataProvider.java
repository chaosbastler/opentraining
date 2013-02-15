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

package de.skubware.opentraining.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.IExercise;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.parser.ExerciseTypeXMLParser;
import de.skubware.opentraining.db.parser.IParser;
import de.skubware.opentraining.db.parser.MuscleJSONParser;
import de.skubware.opentraining.db.parser.SportsEquipmentJSONParser;
import de.skubware.opentraining.db.parser.WorkoutXMLParser;
import de.skubware.opentraining.db.parser.XMLSaver;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

/**
 * Implementation of {@link IDataProvider}.
 * 
 * @author Christian Skubich
 * 
 */
public class DataProvider implements IDataProvider {
	/** Tag for logging */
	public static final String TAG = "DataProvider";

	private Context mContext;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The applications context.
	 */
	public DataProvider(Context context) {
		mContext = context;
	}

	@Override
	public List<ExerciseType> getExercises() {
		if(Cache.INSTANCE.getExercises() == null)
			Cache.INSTANCE.updateCache(mContext);
		
		return Cache.INSTANCE.getExercises();
	}

	/**
	 * Loads the .xml exercise files from the filesystem.
	 * 
	 * @return The loaded {@link ExerciseType}s.
	 * 
	 */
	List<ExerciseType> loadExercises() {
		List<ExerciseType> list = new ArrayList<ExerciseType>();

		try {
			String[] files = mContext.getAssets().list(IDataProvider.EXERCISE_FOLDER);

			for (String f : files) {
				if (f.endsWith(".xml")) {
					ExerciseTypeXMLParser parser = new ExerciseTypeXMLParser(mContext);
					ExerciseType ex = parser.read(mContext.getAssets().open(IDataProvider.EXERCISE_FOLDER + "/" + f));
					list.add(ex);
				}
			}
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing exercises.", ioEx);
		}

		return list;
	}

	@Override
	public boolean saveExercise(IExercise ex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public ExerciseType getExerciseByName(String name) {
		for (ExerciseType ex : this.getExercises()) {
			if (name.equals(ex.getUnlocalizedName()) || name.equals(ex.getLocalizedName()))
				return ex;
		}

		return null;
	}

	@Override
	public boolean exerciseExists(String name) {
		return getExerciseByName(name) != null;
	}
	
	@Override
	public List<Muscle> getMuscles(){
		if(Cache.INSTANCE.getMuscles() == null)
			Cache.INSTANCE.updateCache(mContext);
		
		return Cache.INSTANCE.getMuscles();
	}
	
	/**
	 * Loads the {@link Muscle}s from the filesytem.
	 * 
	 * @return The loaded {@link Muscle}s
	 */
	List<Muscle> loadMuscles(){
		List<Muscle> list = new ArrayList<Muscle>();

		try {
			IParser<List<Muscle>> muscleParser = new MuscleJSONParser();
			list = muscleParser.parse(mContext.getAssets().open(IDataProvider.MUSCLE_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing muscles.", ioEx);
		}

		return list;

	}
	
	@Override
	public Muscle getMuscleByName(String name) {
		for(Muscle m:getMuscles()){
			if(m.isAlternativeName(name))
				return m;
		}

		return null;
	}
	
	
	@Override
	public List<SportsEquipment> getEquipment(){
		if(Cache.INSTANCE.getMuscles() == null)
			Cache.INSTANCE.updateCache(mContext);
		
		return Cache.INSTANCE.getEquipment();
	}
	
	/**
	 * Loads the {@link Muscle}s from the filesytem.
	 * 
	 * @return The loaded {@link Muscle}s
	 */
	List<SportsEquipment> loadEquipment(){
		List<SportsEquipment> list = new ArrayList<SportsEquipment>();

		try {
			IParser<List<SportsEquipment>> equipmentParser = new SportsEquipmentJSONParser();
			list = equipmentParser.parse(mContext.getAssets().open(IDataProvider.EQUIPMENT_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing muscles.", ioEx);
		}

		return list;

	}
	
	@Override
	public SportsEquipment getEquipmentByName(String name) {
		for(SportsEquipment m:getEquipment()){
			if(m.isAlternativeName(name))
				return m;
		}

		return null;
	}
	
	
	
	

	@Override
	public List<Workout> getWorkouts() {
		List<Workout> workoutList = new ArrayList<Workout>();

		// list files in directory that end with ".xml"
		String files[] = mContext.getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".xml"))
					return true;
				else
					return false;
			}
		});

		// parse each file
		for (String file : files) {
			Workout w = this.loadWorkout(mContext.getFilesDir().toString() + "/" + file);
			workoutList.add(w);
			
			if (w == null)
				Log.e(TAG, "Read Workout and parser returned null. This should not happen");
		}

		Log.v(TAG, "Read " + files.length + " Workouts. workoutList.size()= " + workoutList.size());

		return workoutList;
	}

	/**
	 * Tries to load and parse a {@link Workout} .xml file.
	 * 
	 * @param path
	 *            The path of the .xml file
	 * 
	 * @return The {@link Workout} or null if the file could not be read
	 */
	private Workout loadWorkout(String path) {
		String xmlData;
		try {
			xmlData = loadFileFromFileSystem(path);

			// write file again ...
			FileOutputStream fos = mContext.openFileOutput("my_xml", Context.MODE_PRIVATE);
			fos.write(xmlData.getBytes());
			fos.close();

			// ... to read it
			WorkoutXMLParser parser = new WorkoutXMLParser();
			Workout w = parser.read(mContext.getFileStreamPath("my_xml"), mContext);

			if (w == null) {
				Log.e(TAG, "Read Workout and parser returned null. This should not happen");
			}
			return w;
		} catch (IOException e) {
			Log.i(TAG, "Could not read training plan \n" + e.getMessage());
			return null;
		}

	}

	@Override
	public boolean saveWorkout(Workout w) {
		return XMLSaver.writeTrainingPlan(w, mContext.getFilesDir());
	}

	/**
	 * Loads a file from the raw folder.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	private String loadFileFromRaw(String fileName) throws IOException {
		Resources resources = mContext.getResources();

		// Create a InputStream to read the file into

		int rID = resources.getIdentifier("de.skubware.opentraining:raw/" + fileName, null, null);
		// get the file as a stream
		InputStream is = resources.openRawResource(rID);

		return loadFile(is);
	}

	/**
	 * Loads a file from the assets folder.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	private String loadFileFromAssets(String fileName) throws IOException {
		Resources resources = mContext.getResources();
		InputStream is = resources.getAssets().open(fileName);
		return loadFile(is);
	}

	/**
	 * Loads a file from the assets folder.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	private String loadFileFromFileSystem(String fileName) throws IOException {
		InputStream is = new FileInputStream(new File(fileName));
		return loadFile(is);
	}

	/**
	 * Loads the InputStream.
	 * 
	 * @param is
	 *            The InputStream to read
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	private String loadFile(InputStream is) throws IOException {
		// create a buffer that has the same size as the InputStream
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		ByteArrayOutputStream oS = new ByteArrayOutputStream();
		oS.write(buffer);
		oS.close();
		is.close();

		// return the output stream as a String
		return oS.toString();
	}

}
