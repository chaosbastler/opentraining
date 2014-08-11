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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


/**
 * A class that makes (dynamic) translation easier. The Android I18N mechanism
 * only supports translation at compile time, this class supports translating at
 * runtime.
 * 
 * Furthermore it is possible to define synonyms. These alternative names and
 * translations can be used to improve searching: It does not matter if the user
 * searches for "Übungsmatte", "Gymnastikmatte" or "Exercise Mat".
 * 
 * Another advantage of using this translation mechanism is, that it will be
 * much easier to use the database(exercises, muscles, equipment, ...) for other
 * projects, e.g. an iOS app or a web application. Therefore translations are in
 * XML or JSON format.
 * 
 * 
 */
public abstract class Translatable implements Comparable<Translatable>, Serializable {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Map that stores the primary name and alternative names for each Locale. */
	private Map<Locale, Set<String>> nameMap = new HashMap<Locale, Set<String>>();

	/** The (primary) name of the Translatable */
	protected String name;

	/**
	 * Default constructor.
	 * 
	 * @param locale
	 *            The Locale of the names.
	 * @param nameList
	 *            The list with the primary name and the alternative names. The
	 *            first name of the list will be the primary name.
	 */
	public Translatable(Locale locale, List<String> nameList) {
		this.addNames(locale, nameList);
		name = nameList.get(0);
	}

	/**
	 * Adds the names to the list of alternative names. First name will be the
	 * primary name for this locale.
	 * 
	 * @param locale
	 *            The Locale of the names.
	 * @param names
	 *            The list with the primary name and the alternative names. The
	 *            first name of the list will be the primary name.
	 */
	public void addNames(Locale locale, String... names) {
		if (nameMap.get(locale) == null) {
			Set<String> nameSet = new HashSet<String>();
			nameMap.put(locale, nameSet);
		}

		Locale userLocale = Locale.getDefault();
		if (locale.getLanguage().equals(userLocale.getLanguage())) {
			this.name = names[0];
		}

		Set<String> nameSet = nameMap.get(locale);
		for (String name : names) {
			nameSet.add(name);
			nameSet.add(name.toLowerCase(Locale.GERMANY));
		}

	}

	/**
	 * @see #addNames(Locale, String...)
	 */
	public void addNames(Locale locale, List<String> nameList) {
		this.addNames(locale, nameList.toArray(new String[nameList.size()]));
	}

	public boolean isAlternativeName(String name) {
		for (Locale locale : nameMap.keySet()) {
			if (nameMap.get(locale).contains(name))
				return true;

		}

		return false;
	}

	/**
	 * Returns the (localized) name.
	 * 
	 * @return The (localized) name of this object.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns a String that represents this object. Should only be used for
	 * debugging.
	 * 
	 * @return A String that represents this object.
	 */
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Primary name: " + name + "\n");
		for (Locale locale : nameMap.keySet()) {
			builder.append("\n Locale/language: " + locale.getLanguage().toString());
			for (String name : nameMap.get(locale)) {
				builder.append("\n  - " + name);
			}
		}

		return builder.toString();
	}

	/**
	 * Two {@link Translatable}s are compared by comparing the result of
	 * toString().
	 * 
	 * @param t
	 *            The object that should be compared
	 */
	@Override
	public int compareTo(Translatable t) {
		return this.toString().compareTo(t.toString());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}
	
	
	/**
	 * Two {@link Translatable}s are considered equal, if their toString()
	 * returns the same String.
	 * 
	 * @param t
	 *            The object that should be compared
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Translatable other = (Translatable) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}



}