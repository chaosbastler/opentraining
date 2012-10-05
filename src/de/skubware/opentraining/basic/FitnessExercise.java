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

/**
 * A FitnessExercise consists out of an ExerciseType and (optional) FSets
 * 
 * @author Christian Skubich
 * 
 */
public class FitnessExercise {

	private ExerciseType exType;
	private ArrayList<FSet> fSets = new ArrayList<FSet>();
	private String customName;

	/**
	 * Constructor of this class
	 * 
	 * @param exType The ExerciseType of the new object
	 * @param set  The FitnessSets for this FitnessExercise, may be emtpy
	 * 
	 * @throws NullPointerException  if exType is null
	 */
	public FitnessExercise(ExerciseType exType, FSet... set) {
		if (exType == null) {
			throw new NullPointerException();
		}

		for (FSet s : set) {
			if (s == null) {
				throw new NullPointerException();
			}
		}

		// assign given parameters to fields
		this.exType = exType;
		for (FSet fs : set) {
			this.fSets.add(fs);
		}
		this.customName = exType.getName();
	}

	/**
	 * Getter for exType
	 * 
	 * @return The type of Exercise
	 */
	public ExerciseType getExType() {
		return exType;
	}

	/**
	 * Getter for the FitnessSets
	 * 
	 * @return An unmodifiable List with FitnessSets
	 */
	public List<FSet> getFSetList() {
		return Collections.unmodifiableList(this.fSets);
	}

	/**
	 * Returns a String representation of this object.
	 * This is identical to the {@code customName}.
	 * 
	 * @return The custumName
	 */
	@Override
	public String toString() {
		return customName;
	}
	
	/**
	 * Setter for {@code customName}
	 * 
	 * @param newName The new custom name for this FitnessExercise.
	 * 
	 * @throws NullPointerException if argument is null
	 */
	public void setCustomName(String newName){
		if(newName==null)
			throw new NullPointerException("setCustonName() Argument must not be null");
		
		this.customName = newName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((exType == null) ? 0 : exType.hashCode());
		result = prime * result + ((fSets == null) ? 0 : fSets.hashCode());
		return result;
	}

	/**
	 * Compares two FitnessExercises.
	 * Important: a different customName does not change the result of equals()
	 * 
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof FitnessExercise)) {
			return false;
		}
		FitnessExercise other = (FitnessExercise) obj;
		if (exType == null) {
			if (other.exType != null) {
				return false;
			}
		} else if (!exType.equals(other.exType)) {
			return false;
		}
		if (fSets == null) {
			if (other.fSets != null) {
				return false;
			}
		} else if (!fSets.equals(other.fSets)) {
			return false;
		}
		return true;
	}

}