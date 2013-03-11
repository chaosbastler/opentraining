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

package de.skubware.opentraining.basic;

import java.io.Serializable;
import java.util.*;

/**
 * There are several parameters that define how an exercise is executed:
 * 
 * weight			-	which weight you should put on your barbell, dumbbell, ...
 * repetitions		-	how often the exercise should be executed until you take a short break
 * number of sets	-	a successional series of repetitions is called a set
 * 						e.g. 3 sets / 12 repetitions do mean: 12 repetitions; short break; 12 repetitions; short break; 12 repetitions;
 * 
 * This class represents a single set.
 */
public class FSet implements Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** List that contains the parameters that define the set  */
	private List<SetParameter> mSetParameterList = new ArrayList<SetParameter>();

	/**
	 * A SetParameter specifies what should be/has been done in a set, e.g. how
	 * long({@link Duration}), how often({@link Repetition}) or how much(
	 * {@link Weight}).
	 * 
	 * Each SetParameter has a name() and an integer value.
	 * 
	 */
	public static abstract class SetParameter {

		/** Positive number */
		protected int value;
		
		/** The name of the SetParameter */
		public final String name;

		/**
		 * Default constructor.
		 * 
		 * @param name
		 *            The name of the SetParameter
		 * @param value
		 *            A positive number
		 * 
		 * @throws IllegalArgumentException
		 *             if value is below 1
		 */
		SetParameter(String name, int value) {
			if(value <= 0)
				throw new IllegalArgumentException("value must be > 0 , was: " + value);
			
			this.name = name;
			this.value = value;
		}

		public final String getName() {
			return name;
		}

		public final int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name + " : " + value;
		}

		// Classes
		public static class Repetition extends SetParameter {

			public Repetition(int value) {
				super("repetition", value);
			}

			@Override
			public String toString() {
				return value + " x";
			}
		}

		public static class Weight extends SetParameter {

			public Weight(int value) {
				super("weight", value);
			}

			@Override
			public String toString() {
				float weight = value;
				weight /= 1000;

				return weight + " kg";
			}
		}

		public static class Duration extends SetParameter {

			public Duration(int value) {
				super("duration", value);
			}

			@Override
			public String toString() {
				return value + " s";
			}
		}
	}

	/**
	 * Constructor for FSet.
	 * 
	 * @param cats
	 *            How often the exercise should be executed
	 * 
	 * @throws IllegalArgumentException
	 *             if cats is empty
	 * @throws NullPointerException
	 *             if any argument is null
	 */
	public FSet(SetParameter... cats) {
		if(cats.length == 0)
			throw new IllegalArgumentException("cats must not be empty");
		
		for (SetParameter c : cats) {
			if (c == null) {
				throw new NullPointerException();
			}
		}
		java.util.Collections.addAll(this.mSetParameterList, cats);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (SetParameter c : mSetParameterList) {
			b.append(c.toString());
			b.append(" ");
		}
		return b.toString();
	}

	public int[] listValues() {
		int[] arr = new int[mSetParameterList.size()];
		int i = 0;
		for (SetParameter c : mSetParameterList) {
			arr[i] = c.getValue();
			i++;
		}

		return arr;
	}

	public String[] listFields() {
		String[] arr = new String[mSetParameterList.size()];
		int i = 0;
		for (SetParameter c : mSetParameterList) {
			arr[i] = c.getName();
			i++;
		}

		return arr;
	}

	public int getNumberOfSetParameters() {
		return mSetParameterList.size();
	}

	/**
	 * Getter for the {@link SetParameter}s
	 * 
	 * @return A list with the {@link SetParameter}s
	 */
	public List<SetParameter> getSetParameters() {
		return Collections.unmodifiableList(this.mSetParameterList);
	}

}