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

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.widget.ShareActionProvider;

import de.skubware.opentraining.basic.ExerciseTag;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.IExercise;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.parser.ExerciseTagJSONParser;
import de.skubware.opentraining.db.parser.ExerciseTypeXMLParser;
import de.skubware.opentraining.db.parser.IParser;
import de.skubware.opentraining.db.parser.MuscleJSONParser;
import de.skubware.opentraining.db.parser.SportsEquipmentJSONParser;
import de.skubware.opentraining.db.parser.WorkoutXMLParser;
import de.skubware.opentraining.db.parser.XMLSaver;
import android.content.Context;
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
		if (Cache.INSTANCE.getExercises() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<ExerciseType>(Cache.INSTANCE.getExercises());
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

			Collections.sort(list);
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
	public List<Muscle> getMuscles() {
		if (Cache.INSTANCE.getMuscles() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<Muscle>(Cache.INSTANCE.getMuscles());
	}

	/**
	 * Loads the {@link Muscle}s from the filesytem.
	 * 
	 * @return The loaded {@link Muscle}s
	 */
	List<Muscle> loadMuscles() {
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
		for (Muscle m : getMuscles()) {
			if (m.isAlternativeName(name))
				return m;
		}

		return null;
	}

	@Override
	public List<SportsEquipment> getEquipment() {
		if (Cache.INSTANCE.getEquipment() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<SportsEquipment>(Cache.INSTANCE.getEquipment());
	}

	/**
	 * Loads the {@link SportsEquipment}s from the filesytem.
	 * 
	 * @return The loaded {@link SportsEquipment}s
	 */
	List<SportsEquipment> loadEquipment() {
		List<SportsEquipment> list = new ArrayList<SportsEquipment>();

		try {
			IParser<List<SportsEquipment>> equipmentParser = new SportsEquipmentJSONParser();
			list = equipmentParser.parse(mContext.getAssets().open(IDataProvider.EQUIPMENT_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing SportsEquipment.", ioEx);
		}

		return list;

	}

	@Override
	public SportsEquipment getEquipmentByName(String name) {
		for (SportsEquipment m : getEquipment()) {
			if (m.isAlternativeName(name))
				return m;
		}

		return null;
	}
	
	@Override
	public List<ExerciseTag> getExerciseTags() {
		if (Cache.INSTANCE.getExerciseTags() == null)
			Cache.INSTANCE.updateCache(mContext);

		return new ArrayList<ExerciseTag>(Cache.INSTANCE.getExerciseTags());
	}

	/**
	 * Loads the {@link ExerciseTag}s from the filesytem.
	 * 
	 * @return The loaded {@link ExerciseTag}s
	 */
	List<ExerciseTag> loadExerciseTags() {
		List<ExerciseTag> list = new ArrayList<ExerciseTag>();

		try {
			IParser<List<ExerciseTag>> equipmentParser = new ExerciseTagJSONParser();
			list = equipmentParser.parse(mContext.getAssets().open(IDataProvider.EXERCISE_TAG_FILE));
		} catch (IOException ioEx) {
			Log.e(TAG, "Error during parsing muscles.", ioEx);
		}

		return list;

	}

	@Override
	public ExerciseTag getExerciseTagByName(String name) {
		for (ExerciseTag m : getExerciseTags()) {
			if (m.isAlternativeName(name))
				return m;
		}
		
		Log.w(TAG, "Did not find ExerciseTag: " + name + ".\n Will create new ExerciseTag.");
		ArrayList<String> nameList = new ArrayList<String> ();
		return new ExerciseTag(Locale.getDefault(), nameList, "");

	}

	@Override
	public List<Workout> getWorkouts() {
		List<Workout> workoutList = new ArrayList<Workout>();

		// list files in directory that end with ".xml"
		String files[] = mContext.getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".xml") && !filename.equals(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME))
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
			DataHelper helper = new DataHelper(mContext);
			xmlData = helper.loadFileFromFileSystem(path);

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
		Log.d(TAG, "w==null: " + (w==null) + "  mContext==null: " + (mContext==null));
		return XMLSaver.writeTrainingPlan(w, mContext.getFilesDir());
	}

	@Override
	public boolean deleteWorkout(Workout w) {
		File directory = mContext.getFilesDir();
		File workout_file = new File(directory.toString() + "/" + w.getName() + ".xml");
		if (!workout_file.exists())
			throw new IllegalArgumentException("The workout that should be deleted does not exist.");

		return workout_file.delete();
	}


}
