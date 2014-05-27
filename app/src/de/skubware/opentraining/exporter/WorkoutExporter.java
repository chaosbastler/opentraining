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

package de.skubware.opentraining.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.util.Log;

import de.skubware.opentraining.basic.Workout;

/**
 * The super class for all WorkoutExporter. The default implementation for all
 * methods may throw an UnsupportedOperationExceptions. At least one export
 * method is guaranteed to work.
 * 
 * 
 */
public abstract class WorkoutExporter {
	/** Tag for logging */
	public static final String TAG = "WorkoutExporter";

	/** Reference to the current context */
	protected Context mContext;

	/**
	 * Default constructor.
	 */
	public WorkoutExporter(Context context) {
		this.mContext = context;
	}

	/**
	 * Exports a {@link Workout} and returns the generated file.
	 * 
	 * @param w
	 *            The {@link Workout} to export
	 * 
	 * @return The File with the exported {@link Workout}
	 * 
	 * @throws UnsupportedOperationException
	 *             If not supported
	 */
	public File exportWorkoutToFile(Workout w) {
		File cacheDir = mContext.getCacheDir();
		File exportedWorkout = new File(cacheDir.toString() + "/" + w.getName());

		FileOutputStream fos;
		try {
			fos = new FileOutputStream(exportedWorkout.toString());
			fos.write(exportWorkoutToString(w).getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Could not write file to cache: " + exportedWorkout.toString() + "\n", e);
			exportedWorkout = null;
		} catch (IOException e) {
			Log.e(TAG, "Could not write file to cache: " + exportedWorkout.toString() + "\n", e);
			exportedWorkout = null;
		}

		assert (exportedWorkout != null);

		return exportedWorkout;

	}

	/**
	 * Exports a {@link Workout} and returns the generated String.
	 * 
	 * @param w
	 *            The {@link Workout} to export
	 * 
	 * @return The string with the exported {@link Workout} or an empty string if export failed
	 * 
	 * @throws UnsupportedOperationException
	 *             If not overwritten
	 */
	public String exportWorkoutToString(Workout w) {
		throw new UnsupportedOperationException();
	}

}
