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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.util.Log;

/**
 * There are several parameters that define how an exercise is executed:<br><br>
 * 
 * weight - which weight you should put on your barbell, dumbbell, ...<br>
 * repetitions - how often the exercise should be executed until you take a
 * short break<br> 
 * number of sets - a successional series of repetitions is called a
 * set e.g. 3 sets / 12 repetitions do mean: 12 repetitions; short break; 12
 * repetitions; short break; 12 repetitions;<br><br>
 * 
 * This class represents a single set. If it is part of a
 * {@link FitnessExercise} it describes how the exercise should be done. If it
 * is part of a {@link TrainingEntry} it indicates how the exercise has been
 * done.
 */
public class FSet implements Serializable, Cloneable {

	/** Tag for logging */
	public static final String TAG = "FSet";
	
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
	public static abstract class SetParameter implements Serializable {
		
		/** Default serialVersionUID */
		private static final long serialVersionUID = 1L;
		
		/** Not negative number */
		protected int value;
		
		/** The name of the SetParameter */
		public final String name;

		/**
		 * Default constructor.
		 * 
		 * @param name
		 *            The name of the SetParameter
		 * @param value
		 *            A not negative number
		 * 
		 * @throws IllegalArgumentException
		 *             if value is below 0
		 */
		SetParameter(String name, int value) {
			if(value < 0)
				throw new IllegalArgumentException("value must be >= 0 , was: " + value);
			
			this.name = name;
			this.value = value;
		}
		
		SetParameter(SetParameter parameter){
			this.name = parameter.name;
			this.value = parameter.value;
		}
		

		public final String getName() {
			return name;
		}

		public int getValue() {
			return value;
		}

		@Override
		public String toString() {
			return name + " : " + value;
		}
		

		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			return this.name.hashCode();
		}

		/* (non-Javadoc)
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			SetParameter other = (SetParameter) obj;
			return other.name.equals(this.name) && other.value == this.value;
		}		

		

		// Classes
		public static class Repetition extends SetParameter {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public Repetition(int value) {
				super("repetition", value);
			}

			@Override
			public String toString() {
				return value + " x";
			}
			
			public Repetition(Repetition parameter){
				this(parameter.value);
			}
		}

		public static class Weight extends SetParameter {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * 
			 * @param value The weight in gramm
			 */
			public Weight(int value) {
				super("weight", value);
			}

			@Override
			public String toString() {
				float weight = value;
				weight /= 1000;

				return weight + " kg";
			}
			
			public Weight(Weight parameter){
				this(parameter.value);
			}
		}

		public static class Duration extends SetParameter {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			/**Object
			 * 
			 * @param value The time in secons
			 */
			public Duration(int value) {
				super("duration", value);
			}

			@Override
			public String toString() {
				return value + " s";
			}
			
			public Duration(Duration parameter){
				this(parameter.value);
			}
		}
		
		public static class FreeField extends SetParameter {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			private String mContent;
			
			public FreeField(String content){
				super("freefield", 1);
				this.value = -1;
				
				mContent = content;
			}
			
			@Override
			public String toString() {
				return mContent;
			}
			
			@Override
			public int getValue(){
				Log.e(TAG, "getVale() should not be used for instances of FreeField.");
				return super.getValue();
			}
			

			/* (non-Javadoc)
			 * @see java.lang.Object#equals(java.lang.Object)
			 */
			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (!super.equals(obj))
					return false;
				if (getClass() != obj.getClass())
					return false;
				FreeField other = (FreeField) obj;
				return other.name.equals(this.name) && other.mContent.equals(this.mContent);
			}
			
			public FreeField(FreeField parameter){
				this(parameter.mContent);
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
		if(cats.length < 1)
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
	
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mSetParameterList == null) ? 0 : mSetParameterList.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FSet other = (FSet) obj;
		if (mSetParameterList == null) {
			if (other.mSetParameterList != null)
				return false;
		} else if (!mSetParameterList.equals(other.mSetParameterList))
			return false;
		return true;
	}
	
	@Override
	public Object clone(){
		try{
			FSet cloned = (FSet) super.clone();
			cloned.mSetParameterList = new ArrayList<SetParameter>();
			
			for(SetParameter para:mSetParameterList){
				SetParameter newPara = null;
				if(para instanceof SetParameter.Weight){
					newPara = new SetParameter.Weight((SetParameter.Weight) para);
				}
				if(para instanceof SetParameter.Duration){
					newPara = new SetParameter.Duration((SetParameter.Duration) para);
				}
				if(para instanceof SetParameter.Repetition){
					newPara = new SetParameter.Repetition((SetParameter.Repetition) para);
				}
				if(para instanceof SetParameter.FreeField){
					newPara = new SetParameter.FreeField((SetParameter.FreeField) para);
				}
				cloned.mSetParameterList.add(newPara);
			}
			
			return cloned;
		} catch(CloneNotSupportedException e){
			e.printStackTrace();
			throw new AssertionError("Clone not supported: " + e.toString());
		}
	}

}