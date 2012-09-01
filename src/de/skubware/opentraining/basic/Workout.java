/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
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

import java.util.*;

import android.util.Log;


/**
 * This class represents a single workout.
 * A workout needs a name and consists out of more than one {@code FitnessExercises}.
 * 
 * It is possible to iterate through the {@code FitnessExercises} and to add new ones.
 * 
 * @author Christian Skubich
 * 
 */
public class Workout implements Iterable<FitnessExercise>{

	public static int DEFAULT_EMPTYROWS = 5;
	
	private String name;
	private int emptyRows = DEFAULT_EMPTYROWS;
	private ArrayList<FitnessExercise> fitnessExercises = new ArrayList<FitnessExercise>();

	
	/**
	 * Constructor of this class.
	 * 
	 * @param name The name of the workout
	 * @param fExes The FitnessExercises of the workout
	 * 
	 * @throws NullPointerException if any argument is null or empty
	 * 
	 */
	public Workout(String name, Collection<FitnessExercise> fExes) {
		if (name == null || fExes == null || fExes.isEmpty() || fExes.contains(null)) {
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
	 * Alternative constructor of this class for given ExerciseTypes.
	 * Have a look at @see #Workout(String, Collection<FitnessExercise>) 
	 * for more information.
	 *
	 * @param name The name of the workout
	 * @param exes The ExerciseTypes that should be added to workout.
	 */
	public Workout(String name, List<ExerciseType> exes){
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

	/**
	 * Getter for name
	 * 
	 * @return The name of the workout
	 */
	public String getName() {
		return this.name;
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
	 * @param fEx The FitnessExercise to add
	 */
	public void addFitnessExercise(FitnessExercise fEx) {
		this.fitnessExercises.add(fEx);
	}

	/**
	 * Removes the given {@code FitnessExercise} from the Workout.
	 * 
	 * @param fEx The FitnessExercise to remove
	 */
	public void removeFitnessExercise(FitnessExercise fEx) {
		this.fitnessExercises.remove(fEx);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean equals(Object o){
		if(! (o instanceof Workout) )
			return false;
		Workout w = (Workout) o;
		
		// hashCode() check
		if(this.hashCode() != w.hashCode())
			return false;
		
		// name check
		if(!w.getName().equals(this.getName()))
			return false;
		
		// FitnessExercise check
		if(!w.getFitnessExercises().containsAll(this.fitnessExercises))
			return false;
		if(!this.fitnessExercises.containsAll(w.getFitnessExercises()))
			return false;
		
		return true;
	}
	
	/** {@inheritDoc} */
	@Override
	public int hashCode(){
		return this.name.hashCode() + this.fitnessExercises.hashCode();
	}
	
		
	/** {@inheritDoc} */
	@Override
	public String toString() {
		return this.name;
	}
	
	public void switchExercises(FitnessExercise first, FitnessExercise second){
		if(!this.fitnessExercises.contains(first) || !this.fitnessExercises.contains(second)){
			throw new AssertionError("FitnessExercise does not exist in workout");
		}	
		
		int idxFirst = this.fitnessExercises.indexOf(first);
		int idxSecond = this.fitnessExercises.indexOf(second);

		Collections.swap(fitnessExercises, idxFirst, idxSecond);

	}

	public int getEmptyRows() {
		return emptyRows;
	}

	public void setEmptyRows(int emptyRows) {
		if(emptyRows>0)
			this.emptyRows = emptyRows;
		else
			throw new IllegalArgumentException("There must be more than 0 empty rows");
	}

}