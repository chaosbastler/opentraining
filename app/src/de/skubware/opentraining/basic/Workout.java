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

package de.skubware.opentraining.basic;

import java.io.Serializable;
import java.util.*;

import android.util.Log;

/**
 * This class represents a single workout. A workout needs a name and consists
 * out of more than one {@code FitnessExercise}s.
 * 
 * It is possible to iterate through the {@code FitnessExercises} and to add new
 * ones.
 * 
 * @author Christian Skubich
 * 
 */
public class Workout implements Iterable<FitnessExercise>, Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Tag for logging */
	static final String TAG = "Workout";

	public static int DEFAULT_EMPTYROWS = 8;

	private String name;
	private int emptyRows = DEFAULT_EMPTYROWS;
	private ArrayList<FitnessExercise> fitnessExercises = new ArrayList<FitnessExercise>();

	/**
	 * Constructor of this class.
	 * 
	 * @param name
	 *            The name of the workout
	 * @param fExes
	 *            The FitnessExercises of the workout
	 * 
	 * @throws NullPointerException
	 *             if any argument is null or empty
	 * 
	 */
	public Workout(String name, Collection<FitnessExercise> fExes) {
		if (name == null || name.replaceAll(" ", "").equals("") || fExes == null || fExes.isEmpty() || fExes.contains(null)) {
			throw new NullPointerException();
		}

		// Assign given values
		this.name = name;
		this.fitnessExercises = new ArrayList<FitnessExercise>();
		this.fitnessExercises.addAll(fExes);
	}

	/**
	 * @see #Workout(String, Collection<FitnessExercise>)
	 */
	public Workout(String name, FitnessExercise... fExes) {
		this(name, Arrays.asList(fExes));
	}

	/**
	 * Alternative constructor of this class for given ExerciseTypes. Have a
	 * look at @see #Workout(String, Collection<FitnessExercise>) for more
	 * information.
	 * 
	 * @param name
	 *            The name of the workout
	 * @param exes
	 *            The ExerciseTypes that should be added to workout.
	 */
	public Workout(String name, List<ExerciseType> exes) {
		this(name, ExerciseType.asFitnessExercise(exes));
	}

	/** {@inheritDoc} */
	public Iterator<FitnessExercise> iterator() {
		return this.fitnessExercises.iterator();
	}

	/**
	 * @see java.util.List#contains(Object)
	 */
	public boolean contains(FitnessExercise fEx) {
		return this.fitnessExercises.contains(fEx);
	}

	/** Adds a new TrainingEntriy to each FitnessExercise in this Workout. */
	public void addTrainingEntry(Date date) {
		for (FitnessExercise fEx : this.fitnessExercises) {
			fEx.addTrainingEntry(date);
		}
	}

	/**
	 * Returns a set of dates, for which {@link TrainingEntry}s do exist.
	 * 
	 * @return A set of dates, for which training entries do exist.
	 */
	/*
	 * public Set<Date> getTrainingEntryDates(){ List<Date> dateList = new
	 * ArrayList<Date>(); for(FitnessExercise fEx:this.fitnessExercises){
	 * for(TrainingEntry e:fEx.getTrainingEntryList()){
	 * dateList.add(e.getDate()); } }
	 * 
	 * // assert that number dates is correct Set<Date> dateSet = new
	 * HashSet<Date>(dateList); if( dateList.size() != (dateSet.size() *
	 * this.fitnessExercises.size()) ){ throw new
	 * AssertionError("Incorrect number of TrainingEntries. This should not happen."
	 * ); } return dateSet; }
	 */

	/**
	 * Checks if there are TrainingEntries for the FitnessExercises.
	 * 
	 * @return True if the FitnessExercises have at least one TrainingEntry
	 */
	public boolean hasTrainingEntries() {
		return this.fitnessExercises.get(0).getTrainingEntryList().size() > 0;
	}

	/**
	 * Getter for name
	 * 
	 * @return The name of the workout
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Setter for name
	 * 
	 * @param name
	 *            The new name of the workout
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Getter for the {@code FitnessExercises}
	 * 
	 * @return A list with the FitnessExercises
	 */
	public List<FitnessExercise> getFitnessExercises() {
		return this.fitnessExercises;
	}

	/**
	 * Adds the given {@code FitnessExercise} to the Workout.
	 * 
	 * @param fEx
	 *            The FitnessExercise to add
	 * 
	 * @throws IllegalArgumentException
	 *             if there is already an FitnessExercise with the same
	 *             ExerciseType
	 */
	public void addFitnessExercise(FitnessExercise fEx) {
		ExerciseType exType = fEx.getExType();
		for (FitnessExercise ex : this.getFitnessExercises()) {
			if (ex.getExType().equals(exType))
				throw new IllegalArgumentException("There is already an Exercise with the ExerciseType: " + exType.toString());
		}

		this.fitnessExercises.add(fEx);
	}

	/**
	 * Removes the given {@code FitnessExercise} from the Workout.
	 * 
	 * @param fEx
	 *            The FitnessExercise to remove
	 */
	public void removeFitnessExercise(FitnessExercise fEx) {
		this.fitnessExercises.remove(fEx);
	}

	/**
	 * Updates the {@link FitnessExercise}.
	 * 
	 * @param changedFEx
	 *            The FitnessExercise that has changed
	 * 
	 * @throws IllegalArgumentException
	 *             if the Exercise is not contained in this Workout
	 */
	public void updateFitnessExercise(FitnessExercise changedFEx) {
		Log.d(TAG, "updateFitnessExercise(), changedFEx: " + changedFEx.toDebugString());
		FitnessExercise oldFex = null;
		for (FitnessExercise fEx : this.fitnessExercises) {
			// this comparison relies on the fact that each Workout can only
			// contain each ExerciseType once
			if (fEx.getExType().equals(changedFEx.getExType())) {
				oldFex = changedFEx;
				int oldIndex = fitnessExercises.indexOf(fEx);
				fitnessExercises.remove(oldIndex);
				fitnessExercises.add(oldIndex, changedFEx);
				break;
			}
		}

		if (oldFex == null) {
			throw new IllegalArgumentException("FitnessExercise: " + changedFEx.toString() + " is not contained in this Workout: "
					+ this.toString());
		}

	}

	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Workout))
			return false;
		Workout w = (Workout) o;

		// hashCode() check
		if (this.hashCode() != w.hashCode())
			return false;

		// name check
		if (!w.getName().equals(this.getName()))
			return false;

		// FitnessExercise check
		if (!w.getFitnessExercises().containsAll(this.fitnessExercises))
			return false;
		if (!this.fitnessExercises.containsAll(w.getFitnessExercises()))
			return false;

		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int hashCode() {
		return this.name.hashCode() + this.fitnessExercises.size();
	}

	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.name;
	}

	/**
	 * Returns a String that represents this object. Should only be used for
	 * debugging.
	 * 
	 * @return A String that represents this object.
	 */
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Name: " + name + "\n");
		builder.append("Empty Rows: " + emptyRows + "\n");
		for (FitnessExercise fEx : getFitnessExercises()) {
			builder.append("\n" + fEx.toDebugString());
		}

		return builder.toString();
	}

	public void switchExercises(FitnessExercise first, FitnessExercise second) {
		if (!this.fitnessExercises.contains(first) || !this.fitnessExercises.contains(second)) {
			throw new AssertionError("FitnessExercise does not exist in workout");
		}

		int idxFirst = this.fitnessExercises.indexOf(first);
		int idxSecond = this.fitnessExercises.indexOf(second);

		Collections.swap(fitnessExercises, idxFirst, idxSecond);

	}

	/**
	 * Getter for emptyRows
	 * 
	 * @return The number of empty rows (>0)
	 */
	public int getEmptyRows() {
		return this.emptyRows;
	}

	/**
	 * Setter for emptyRows
	 * 
	 * @param emptyRows
	 *            New number of empty rows, must be positive
	 * 
	 * @throws IllegalArgumentException
	 *             if argument is below 0
	 */
	public void setEmptyRows(int emptyRows) {
		if (emptyRows > 0)
			this.emptyRows = emptyRows;
		else
			throw new IllegalArgumentException("There must be more than 0 empty rows");

		Log.d(TAG, "setEmptyRows() to " + this.emptyRows);
	}

	/**
	  * Returns a new Workout only containing the Exercises, but no history(FSets).
	  * The crated Workout will have the same name.
	  */
	public Workout getWorkoutWithoutHistory(){
		List<FitnessExercise> fExList = new ArrayList<FitnessExercise>();
		for(FitnessExercise fEx: this.fitnessExercises){
			fExList.add(new FitnessExercise(fEx.getExType())); // copy exercises without FSets
		}
		Workout w = new Workout(this.name, fExList);
		return w;
	}
}