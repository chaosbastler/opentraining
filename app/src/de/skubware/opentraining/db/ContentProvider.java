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

import java.io.*;
import java.util.*;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.db.parser.ExerciseTypeXMLParser;
import de.skubware.opentraining.db.parser.WorkoutXMLParser;
import de.skubware.opentraining.db.parser.XMLSaver;

//TODO clean up API
/**
 * This class is implemented as a Singleton, though it isn't the standard
 * implementation of this pattern.
 * 
 * @author Christian Skubich
 * 
 */
public enum ContentProvider {
	/** Reference to singleton instance */
	INSTANCE;

	/** The name of the folder that contains exercises .xml files and images */
	private static final String EXERCISE_FOLDER = "opentraining-exercises";

	/** Tag for logging */
	private static final String TAG = "ContentProvider";

	/** List with all Workouts */
	private List<Workout> workoutList = new ArrayList<Workout>();

	/** Map that contains the path of each Workout */
	private Map<Workout, String> workoutPathMap = new HashMap<Workout, String>();

	/** Current currentWorkout */
	private Workout currentWorkout;

	/** Currently choosen .css style for plan */
	private CSSFile css = CSSFile.Default;

	/** Enumeration to define the source, where a file should be loaded from */
	public enum Source {
		RAW_FOLDER, ASSETS, FILE_SYSTEM;
	}

	/** Enumeration for .css files */
	public enum CSSFile {
		Default, Green;

		public final static CharSequence[] items = new CharSequence[CSSFile.values().length];
		static {
			int i = 0;
			for (CSSFile css : CSSFile.values()) {
				items[i] = css.name();
				i++;
			}
		}

		String filename;

		CSSFile() {
			this.filename = "trainingplan_" + this.name().toLowerCase(Locale.US) + ".css";
		}

	}



	/**
	 * Removes all ExerciseTypes and reads them again. A new thread is started
	 * for this.
	 */
	/*public void loadExercises(final Context context) {

		// do this in background
		new Thread(new Runnable() {
			public void run() {
				// start localizing first
				//Muscle.localize(context);
				//SportsEquipment.localize(context);
				// load images for equipment
				String[] eq;
				try {
					eq = context.getAssets().list("equipment");
					for (String image : eq) {
						String without_suffix = image.substring(0, image.lastIndexOf('.'));
						SportsEquipment equipment = SportsEquipment.getByName(without_suffix);
						equipment.setImage(Drawable.createFromStream(context.getAssets().open("equipment/" + image), null));
					}
				} catch (IOException e) {
					Log.e(TAG, "Loading images for equipment failed", e);
				}

				try {
					// next line is necessary to avoid current modification
					// exception while iterating
					Set<ExerciseType> l = new HashSet<ExerciseType>(ExerciseType.listExerciseTypes());
					int removedExercises = 0;
					for (IExercise exType : l) {
						boolean removed = ExerciseType.removeExerciseType(exType);
						if (removed)
							removedExercises++;
					}
					Log.d(TAG, "Loading exercises, removed " + removedExercises + " old exercises");

					String[] files = context.getAssets().list(EXERCISE_FOLDER);

					for (String f : files) {
						if (f.endsWith(".xml")) {
							ExerciseTypeXMLParser parser = new ExerciseTypeXMLParser();
							parser.read(context.getAssets().open("opentraining-exercises/" + f));
						}

					}

				} catch (IOException ioEx) {
					ioEx.printStackTrace();
				}
			}
		}).start();

	}*/

	/**
	 * Saves the Workout to the file system.
	 */
	/*public boolean savePlan(Context context, Workout workout) {
		// TODO handle saving different plans (do not override existing ones!)
		boolean succ = XMLSaver.writeTrainingPlan(workout, context.getFilesDir());
		Log.v(TAG, "Saving plan. succ=" + succ);
		return succ;
	}*/

	/**
	 * Deletes the current workout.
	 * 
	 * @param context
	 *            The context
	 * 
	 * @return true if the workout could be deleted, false if not(e.g. no
	 *         current workout, path of workout is null, ...)
	 */
	public boolean deleteWorkout(Context context) {
		if (this.currentWorkout == null)
			return false;

		this.workoutList.remove(currentWorkout);
		String path = this.workoutPathMap.get(currentWorkout);
		this.currentWorkout = null;

		if (path == null) {
			Log.e(TAG, "Path of workout is null. This should not happen.");
			return false;
		}

		File workout = new File(path);
		return workout.delete();

	}

	/**
	 * Tries to load and parse a Workout .xml file.
	 * 
	 * @param context
	 *            The context
	 * @param path
	 *            The path of the .xml file
	 * 
	 * @return The Workout or null if the file could not be read
	 */
	/*public Workout loadWorkout(Context context, String path) {
		String xmlData;
		try {
			xmlData = loadFile(path, Source.FILE_SYSTEM, context);

			// write file again ...
			FileOutputStream fos = context.openFileOutput("my_xml", Context.MODE_PRIVATE);
			fos.write(xmlData.getBytes());
			fos.close();

			// ... to read it
			WorkoutXMLParser parser = new WorkoutXMLParser();
			Workout w = parser.read(context.getFileStreamPath("my_xml"));

			if (w != null) {
				this.workoutList.add(w);
				this.workoutPathMap.put(w, path);
				this.currentWorkout = w;
			} else
				Log.e(TAG, "Read Workout and parser returned null. This should not happen");
			return w;
		} catch (IOException e) {
			Log.i(TAG, "Could not read training plan \n" + e.getMessage());
			return null;
		}

	}*/

	/**
	 * Loads all saved {@link Workout}s. List with Workouts will be
	 * recreated(old Workouts will be gone). If {@link #currentWorkout} is null,
	 * the first read Workout will be set as current Workout.
	 * 
	 * @return true if successful
	 */
	/*public boolean loadWorkouts(Context context) {
		// list files in directory that end with ".xml"
		String files[] = context.getFilesDir().list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
				if (filename.endsWith(".xml"))
					return true;
				else
					return false;
			}
		});

		// recreate list
		this.workoutList = new ArrayList<Workout>();

		// parse each file
		for (String file : files) {
			Workout w = this.loadWorkout(context, context.getFilesDir().toString() + "/" + file);

			if (w == null)
				Log.e(TAG, "Read Workout and parser returned null. This should not happen");
		}

		Log.v(TAG, "Read " + files.length + " Workouts. workoutList.size()= " + this.workoutList.size());
		if (files.length != this.workoutList.size())
			throw new AssertionError("files.length != this.workoutList.size()");

		// set new current Workout
		if (this.currentWorkout == null && !this.workoutList.isEmpty()) {
			this.currentWorkout = this.workoutList.get(0);
			Log.v(TAG, "Set new Workout:  " + this.workoutList.get(0).toString());
		}

		return true;
	}*/

	/**
	 * Saves the ExerciseType to exercise folder.
	 * 
	 * @param ex
	 *            The exercise to save.
	 */
	/*public boolean saveExercise(IExercise ex) {
		// TODO implment saving ExerciseTypes
		throw new AssertionError("Not implemented");
		// return XMLSaver.writeExerciseType(ex,
		// ContentProvider.getExerciseXMLFolder());
	}*/

	/**
	 * Loads a file from the raw folder, the assets folder or the file system.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * @param src
	 *            Where the file is located
	 * @param context
	 *            Current context
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	/*public String loadFile(String fileName, Source src, Context context) throws IOException {
		Resources resources = context.getResources();

		// Create a InputStream to read the file into
		InputStream iS;

		if (src == Source.RAW_FOLDER) {
			// get the resource id from the file name
			int rID = resources.getIdentifier("de.skubware.opentraining:raw/" + fileName, null, null);
			// get the file as a stream
			iS = resources.openRawResource(rID);
		} else if (src == Source.ASSETS) {
			// get the file as a stream
			iS = resources.getAssets().open(fileName);
		} else { // FILE_SYSTEM

			iS = new FileInputStream(new File(fileName));
		}

		// create a buffer that has the same size as the InputStream
		byte[] buffer = new byte[iS.available()];
		// read the text file as a stream, into the buffer
		iS.read(buffer);
		// create a output stream to write the buffer into
		ByteArrayOutputStream oS = new ByteArrayOutputStream();
		// write this buffer to the output stream
		oS.write(buffer);
		// Close the Input and Output streams
		oS.close();
		iS.close();

		// return the output stream as a String
		return oS.toString();
	}*/

	public Workout getCurrentWorkout() {
		return currentWorkout;
	}

	/**
	 * Writes a file to the cache folder.
	 * 
	 * @param data
	 *            Data to write
	 * @param name
	 *            Name of the file
	 * @param context
	 *            Current context
	 * @return The file that was written or null if a problem occurred.
	 */
	public File writeFileToCache(String data, String name, Context context) {
		File destination = context.getCacheDir();

		File f = new File(destination.toString() + "/" + name);

		FileOutputStream fos;
		try {
			fos = context.openFileOutput(f.toString(), Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			f = null;
			Log.e(TAG, "Could not write file to cache: " + name + "\n" + e.getMessage());
		} catch (IOException e) {
			f = null;
			Log.e(TAG, "Could not write file to cache: " + name + "\n" + e.getMessage());
		}

		return f;
	}

	/**
	 * Writes a file to a folder.
	 * 
	 * @param data
	 *            Data to write
	 * @param name
	 *            Name of the file
	 * @param context
	 *            Current context
	 * @param destination
	 *            The destination folder
	 * @return The file that was written or null if a problem occurred.
	 * 
	 * @throws IllegalArgumentException
	 *             if destination is no directory
	 */
	public File writeFile(String data, String name, Context context, File destination) {
		if (!destination.isDirectory())
			throw new IllegalArgumentException("No valid directory");

		File f = new File(destination + "/" + name);
		FileOutputStream fileOutput;
		try {
			fileOutput = new FileOutputStream(f.toString());
			fileOutput.write(data.getBytes());
			fileOutput.close();
		} catch (FileNotFoundException e) {
			f = null;
			Log.e(TAG, "Could not write to cache: " + name + ", destination: " + destination.toString() + "\n" + e.getMessage());
		} catch (IOException e) {
			f = null;
			Log.e(TAG, "Could not write to cache: " + name + ", destination: " + destination.toString() + "\n" + e.getMessage());
		}

		return f;
	}

	/**
	 * Sets the current Workout and adds it to the list of Workouts. If the list
	 * already contains this Workout, it will only be set as current Workout.
	 * 
	 * @param workout
	 *            The new current Workout
	 */
	/*public void setCurrentWorkout(Workout workout) {
		if (!this.workoutList.contains(workout))
			this.workoutList.add(workout);

		Log.d(TAG, "Set new currentWorkout: " + workout.toString());
		this.currentWorkout = workout;
	}*/

	/**
	 * Adds a Workout to the list of Workouts, without setting the new Workout
	 * as current Workout. If the list already contains this Workout, it will
	 * only be set as current Workout.
	 * 
	 * @param workout
	 *            The Workout to add.
	 */
	/*public void addWorkout(Workout workout) {
		if (!this.workoutList.contains(workout))
			this.workoutList.add(workout);
	}*/

	/**
	 * Setter for css
	 * 
	 * @param css
	 *            The new CSSFile
	 * 
	 * @throws IllegalArgumentException
	 *             if css is null
	 */
	/*public void setCSSFile(CSSFile css) {
		if (css == null) {
			Log.e(TAG, "You tried to set a css file to null. This is not allowed. \n");
			throw new IllegalArgumentException("CSS must not be null");
		}
		this.css = css;
	}*/

	/**
	 * Reads and returns the .css file for the plan.
	 * 
	 * @param context
	 *            The context from which this method is called from.
	 * @return A string of the .css file.
	 */
	/*public String getCSSFileAsString(Context context) {
		try {
			return this.loadFile(css.filename, Source.ASSETS, context);
		} catch (IOException e) {
			Log.e(TAG, "Error reading .css file: " + css + "\n" + e.getMessage());
			return "<!--Error reading style information-->";
		}
	}*/

	/**
	 * Returns all {@link Workout}s. Remember to call
	 * {@link #loadWorkouts(Context)} before.
	 * 
	 * @return The list with all {@link Workout}s, may be empty.
	 */
	/*public List<Workout> listWorkouts() {
		return this.workoutList;
	}*/

}
