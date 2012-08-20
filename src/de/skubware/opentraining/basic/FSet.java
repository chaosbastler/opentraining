package de.skubware.opentraining.basic;

import java.util.*;


/**
 * A class that represents a FSet.
 * 
 * @author Christian Skubich
 */
public class FSet {

	private List<Category> cats = new ArrayList<Category>();

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

		// Klassen
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
		java.util.Collections.addAll(this.cats, cats);
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Category c : cats) {
			b.append(c.toString());
			b.append(" ");
		}
		return b.toString();
	}

	public int[] listValues() {
		int[] arr = new int[cats.size()];
		int i = 0;
		for (Category c : cats) {
			arr[i] = c.getValue();
			i++;
		}

		return arr;
	}

	public String[] listFields() {
		String[] arr = new String[cats.size()];
		int i = 0;
		for (Category c : cats) {
			arr[i] = c.getName();
			i++;
		}

		return arr;
	}

	public int getValueNumber() {
		return cats.size();
	}

	/**
	 * Getter for categories
	 * 
	 * @return A list with the categories
	 */
	public List<Category> getCategories() {
		return Collections.unmodifiableList(this.cats);
	}

}