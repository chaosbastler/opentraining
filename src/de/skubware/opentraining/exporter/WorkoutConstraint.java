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

package de.skubware.opentraining.exporter;

import de.skubware.opentraining.basic.*;

/**
 * //TODO check this class, it was just copied from OSTS
 * 
 * This class makes it possible to set some constraints for a Workout. Of course
 * validation is also possible. This is an immutable class. For easily creating
 * instances a Builder is provided.
 * 
 * If any constraint should not be checked, either do not set it or set to with
 * the value null.
 * 
 * @author Christian Skubich
 */
public class WorkoutConstraint {
	
	public enum Reason{
		TOO_SMALL, TOO_BIG;
	}
	
	public class ValidationException extends RuntimeException{

        /**
         * Constructor for this Exception
         * 
         * @param field The field where the Exception occurred
         * @param r     The Reason why the validation failed
         */
        public ValidationException(String field, Reason r){
                super(field + ": " + r.toString());
        }
        
        
        @Override
        public String toString(){
                return "Validierung fehlgeschlagen: " + this.getMessage();
        }	
     }

	/** The maximum number of FitnessExercises */
	private final Integer maxFExs;
	/** The minimum number of FitnessExercises */
	private final Integer minFExs;
	/** The maximum number of FSets */
	private final Integer maxSetCount;

	/**
	 * A Builder class(-> Builder pattern) for easily creating
	 * WorkoutConstraints.
	 */
	public static class Builder {
		Integer maxFExs;
		Integer minFExs;
		Integer maxSetCount;

		public Builder setMaxFExs(Integer i) {
			this.maxFExs = i;
			return this;
		}

		public Builder setMinFExs(Integer i) {
			this.minFExs = i;
			return this;
		}

		public Builder setMaxSetCount(Integer i) {
			this.maxSetCount = i;
			return this;
		}

		/**
		 * Generates the WorkoutConstraint with the settings that were made. The
		 * generated Object is immutable
		 * 
		 * @return The generated WorkoutConstraint
		 */
		public WorkoutConstraint build() {
			return new WorkoutConstraint(this);
		}
	}

	/**
	 * A private constructor which uses a builder
	 * 
	 * @param builder
	 *            The builder to build this instance
	 */
	private WorkoutConstraint(Builder builder) {
		this.maxFExs = builder.maxFExs;
		this.minFExs = builder.minFExs;
		this.maxSetCount = builder.maxSetCount;
	}

	/**
	 * A static factory method for easily getting a Default Constraint for
	 * creating PDF Files.
	 * 
	 * Currently there are 3 conditions: FEx>=5 FEx<=10 maxSet<=4
	 * 
	 * @return The Defauld PDFWorkoutConstraint
	 */
	public static WorkoutConstraint getDefaultPDFConstraint() {
		Builder b = new Builder();
		b.setMinFExs(5);
		b.setMaxFExs(10);
		b.setMaxSetCount(4);
		return b.build();
	}

	/**
	 * Checks all constraints that were set on the given Workout.
	 * 
	 * @param w
	 *            The workout to check
	 * 
	 * @return true if validation succeeds, false otherwise
	 */
	public boolean validate(Workout w) {
		try {
			validateWithException(w);
			return true;
		} catch (ValidationException ill) {
			return false;
		}
	}

	/**
	 * Checks all constraints that were set on the given workout.
	 * 
	 * Attention: In constrast to validate(Workout w) this method throws
	 * Exceptions
	 * 
	 * @param w
	 *            The Workout to check
	 * 
	 * @throws ValidationException
	 *             if validation fails
	 */
	public void validateWithException(Workout w) {
		if (this.minFExs != null
				&& w.getFitnessExercises().size() < this.minFExs) {
			throw new ValidationException("minFEx", Reason.TOO_SMALL);
		}
		if (this.maxFExs != null
				&& w.getFitnessExercises().size() > this.maxFExs) {
			throw new ValidationException("maxFex", Reason.TOO_BIG);
		}
		for (FitnessExercise fEx : w.getFitnessExercises()) {
			if (this.maxSetCount != null
					&& fEx.getFSetList().size() > this.maxSetCount) {
				throw new ValidationException("maxSetCount", Reason.TOO_BIG);
			}
		}

	}

}
