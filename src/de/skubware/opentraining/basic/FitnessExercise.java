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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return exType.toString();
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
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