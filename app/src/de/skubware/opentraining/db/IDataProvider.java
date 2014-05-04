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

package de.skubware.opentraining.db;

import java.util.List;

import de.skubware.opentraining.basic.ExerciseTag;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.License.LicenseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.Workout;

/**
 * Interface for classes that handle I/O.
 * 
 * @author Christian Skubich
 * 
 */
public interface IDataProvider {
	/** The name of the folder that contains exercises .xml files and images */
	static public final String EXERCISE_FOLDER = "opentraining-exercises";
	
	/** The name of the folder that contains user-created exercises .xml files */
	static public final String CUSTOM_EXERCISE_FOLDER = "user_exercises";
	
	/** The name of the folder that contains synced exercise .xml files */
	static public final String SYNCED_EXERCISE_FOLDER = "synced_exercises";

	/** The name of the folder that contains user-created images */
	static public final String CUSTOM_IMAGES_FOLDER = "user_images";

	/** The name of the folder that contains synced (downloaded) images */
	static public final String SYNCED_IMAGES_FOLDER = "synced_images";
	
	/** The name of the folder that contains exercises the example {@link Workout}s */
	static public final String EXAMPLE_WORKOUT_FOLDER = "example_workouts";
	
	/** The name of the folder that contains the workout .xml files */
	static public final String WORKOUT_FOLDER = "";

	/** The JSON file that contains the {@link Muscle}s */
	static public final String MUSCLE_FILE = "muscles.json";

	/** The JSON file that contains the {@link SportsEquipment} */
	static public final String EQUIPMENT_FILE = "equipment.json";
	
	/** The JSON file that contains the {@link ExerciseTag}s */
	static public final String EXERCISE_TAG_FILE = "exercisetags.json";

	/**
	 * Lists all {@link ExerciseType}s
	 * 
	 * @return All {@link ExerciseType}s
	 */
	public List<ExerciseType> getExercises();

	/**
	 * Saves the (user-generated) exercise to {@link CUSTOM_EXERCISE_FOLDER}.
	 * 
	 * @param ex
	 *            The {@link ExerciseType} to save.
	 * 
	 * @return true if successful
	 */
	public boolean saveCustomExercise(ExerciseType ex);

	/**
	 * Deletes the (user-generated) exercise if available.
	 * Will also delete related images if not referenced anywhere else.
	 * 
	 * @param ex
	 *            The {@link ExerciseType} to delete.
	 * 
	 * @return true if successful
	 */
	public boolean deleteCustomExercise(ExerciseType ex);
	
	
	/**
	 * Deletes the (user-generated) image if available.
	 * 
	 * @param ex
	 *            The name of the image to delete.
	 * 
	 * @param checkForReferences
	 *            If this flag is set, images will not be deleted
	 *            when they are referenced by any exercise.            
	 * 
	 * @return true if successful
	 */
	public boolean deleteCustomImage(String imageName, boolean checkForReferences);
	
	
	/**
	 * Saves the (synced) exercises to {@link SYNCED_EXERCISE_FOLDER}.
	 * 
	 * @param ex
	 *            The {@link ExerciseType}s to save.
	 * 
	 * @return all exercises that could not be saved
	 */
	public List<ExerciseType> saveSyncedExercises(List<ExerciseType> exerciseList);	
	
	/**
	 * Tries to find and return the exercise with the name.
	 * 
	 * @param name
	 *            The name of the exercise to look for
	 * 
	 * @return An exercise with the name or null.
	 */
	public ExerciseType getExerciseByName(String name);

	/**
	 * Checks if the exercise does exist.
	 * 
	 * @param name
	 *            The name of the exercise to look for
	 * 
	 * @return true if the exercise exists, false otherwise
	 */
	public boolean exerciseExists(String name);

	/**
	 * Lists all {@link Muscle}s
	 * 
	 * @return All {@link Muscle}s
	 */
	public List<Muscle> getMuscles();

	/**
	 * Tries to find and return the {@link Muscle} with the name.
	 * 
	 * @param name
	 *            The name of the {@link Muscle} to look for
	 * 
	 * @return An {@link Muscle} with the name or null.
	 */
	public Muscle getMuscleByName(String name);

	/**
	 * Tries to find and return the {@link SportsEquipment} with the name.
	 * 
	 * @param name
	 *            The name of the {@link SportsEquipment} to look for
	 * 
	 * @return An {@link SportsEquipment} with the name or null.
	 */
	public SportsEquipment getEquipmentByName(String name);

	/**
	 * Lists all {@link SportsEquipment}s
	 * 
	 * @return All {@link SportsEquipment}s
	 */
	public List<SportsEquipment> getEquipment();
	
	/**
	 * Tries to find and return the {@link ExerciseTag} with the name.
	 * 
	 * @param name
	 *            The name of the {@link ExerciseTag} to look for
	 * 
	 * @return An {@link ExerciseTag} with the name or null.
	 */
	public ExerciseTag getExerciseTagByName(String name);

	/**
	 * Lists all available {@link LicenseType}s
	 * 
	 * @return All {@link LicenseType}s
	 */
	public List<LicenseType> getLicenseTypes();
	
	/**
	 * Tries to find and return the {@link LicenseType} with the name.
	 * If the License cannot be found LicenseType.UNKNOWN will be returned.
	 * 
	 * @param name
	 *            The (short) name of the {@link LicenseType}
	 * 
	 * @return An {@link LicenseType} with the name or LicenseType.UNKNOWN.
	 */
	public LicenseType getLicenseTypeByName(String name);
	
	/**
	 * Lists all {@link ExerciseTag}s
	 * 
	 * @return All {@link ExerciseTag}s
	 */
	public List<ExerciseTag> getExerciseTags();

	/**
	 * Lists all {@link Workout}s
	 * 
	 * @return All {@link Workout}s
	 */
	public List<Workout> getWorkouts();

	/**
	 * Saves the {@link Workout}.
	 * 
	 * @param w
	 *            The {@link Workout} to save.
	 * 
	 * @return true if successful
	 */
	public boolean saveWorkout(Workout w);

	/**
	 * Deletes the {@link Workout}
	 * 
	 * @param w
	 *            The {@link Workout} to delete
	 * 
	 * @return True if successful, false otherwise
	 */
	public boolean deleteWorkout(Workout w);

}
