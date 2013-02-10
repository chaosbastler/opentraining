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
 * A class that represents a FSet.
 * 
 * @author Christian Skubich
 */
public class FSet implements Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	private List<Category> mCategoryList = new ArrayList<Category>();

	public static abstract class Category{

		protected int value;
		public final String name;

		Category(String name, int value) {
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
		public static class Repetition extends Category {

			public Repetition(int value) {
				super("Wdh", value);
			}

			@Override
			public String toString() {
				return value + " x";
			}
		}

		public static class Weight extends Category {

			public Weight(int value) {
				super("Gewicht", value);
			}

			@Override
			public String toString() {
				float weight = value;
				weight /= 1000;

				return weight + " kg";
			}
		}

		public static class Duration extends Category {

			public Duration(int value) {
				super("Dauer", value);
			}

			@Override
			public String toString() {
				return value + " s";
			}
		}
	}

	/**
	 * Constructor for FSet. Does only allow non-negative values.
	 * 
	 * @param repetitions
	 *            How often the exercise should be executed
	 * @param weight
	 *            The weight in gram
	 * 
	 * @throws IllegalArgumentException
	 *             if an argument is below 0
	 * @throws NullPointerException
	 *             if any argument is null
	 */
	public FSet(Category... cats) {
		for (Category c : cats) {
			if (c == null) {
				throw new NullPointerException();
			}
		}
		java.util.Collections.addAll(this.mCategoryList, cats);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Category c : mCategoryList) {
			b.append(c.toString());
			b.append(" ");
		}
		return b.toString();
	}

	public int[] listValues() {
		int[] arr = new int[mCategoryList.size()];
		int i = 0;
		for (Category c : mCategoryList) {
			arr[i] = c.getValue();
			i++;
		}

		return arr;
	}

	public String[] listFields() {
		String[] arr = new String[mCategoryList.size()];
		int i = 0;
		for (Category c : mCategoryList) {
			arr[i] = c.getName();
			i++;
		}

		return arr;
	}

	public int getValueNumber() {
		return mCategoryList.size();
	}

	/**
	 * Getter for categories
	 * 
	 * @return A list with the categories
	 */
	public List<Category> getCategories() {
		return Collections.unmodifiableList(this.mCategoryList);
	}

}