package de.skubware.opentraining.exporter;

import java.io.File;

import android.content.Context;

import de.skubware.opentraining.datamanagement.*;
import de.skubware.opentraining.basic.Workout;

/**
 * The super class for all WorkoutExporter.
 * The default implementation for all methods may throw an UnsupportedOperationExceptions.
 * At least one export method is guaranteed to work.
 * 
 * //TODO Add workoutconstraint
 * 
 * @author Christian Skubich
 *
 */
// Idea: http://code.google.com/p/droidtext/
public abstract class WorkoutExporter {
	protected int rowCount;
	protected Context context;
	
	
	/**
	 * Constructor that requires the number of rows.
	 * 
	 * @param rowCount The number of rows.
	 */
	public WorkoutExporter(int rowCount, Context context){
		this.context = context;
		this.rowCount = rowCount;
	}
	
	/**
	 * Exports a workout and returns the generated file.
	 * 
	 * @param w The workout to export
	 * 
	 * @return The File with the exported workout
	 * 
	 * @throws UnsupportedOperationException If not supported
	 */
	public File exportWorkoutToFile(Workout w){
		try{
			File f = DataManager.INSTANCE.writeFileToCache(this.exportWorkoutToString(w), w.getName(), context);
			
			assert(f!=null);
			
			return f;
		}catch(UnsupportedOperationException unsupported){
			// may happen when String export doesn't work
			throw unsupported;
		}
	}
	
	/**
	 * Exports a workout and returns the generated String.
	 * 
	 * @param w The workout to export
	 * @return The string with the exported Workout
	 * @throws UnsupportedOperationException If not overwritten
	 */
	public String exportWorkoutToString(Workout w){
		throw new UnsupportedOperationException();
	}


}
