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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TreeSet;

import android.content.Context;
import android.util.Log;
import de.skubware.opentraining.R;

/**
 * An enumeration class for the different muscles of a men(or women).
 * 
 * This class is very similar to the class SportsEquipment, so I tried to extract a common super class.
 * But as static methods can't be inherited, I couldn't write my own 'enum-like' super type.
 * 
 * 
 * @author Christian Skubich
 * 
 */
public class Muscle implements Comparable<Muscle> {
	/** Tag for logging */
	private static final String TAG = "Muscle";

	/**
	 * Flag for the localization status of this class. Just for asserting, that
	 * localize() has been called.
	 */
	private static boolean localized = false;

	/**
	 * Map that connects names and Muscle objects.
	 * 
	 * Reason: there may be alternative names
	 */
	private static Map<String, Muscle> nameMap = new HashMap<String, Muscle>();

	/** The name of the Muscle */
	private String name;

	/**
	 * The Constructor
	 * 
	 * @param name
	 *            The name of the Tool, names should be used only once
	 */
	private Muscle(String name) {
		this.name = name;
		nameMap.put(name, this);
		nameMap.put(name.toLowerCase(), this);
	}

	/**
	 * Adds an alternative name to the Muscle.
	 * 
	 * @param altName
	 *            The new alternative name.
	 */
	public void provideAlternativeName(String altName) {
		if (altName.equals(this.toString()))
			return;

		if (nameMap.containsKey(altName)) {
			Log.w(TAG, "Warning: " + altName + " is already connected with " + nameMap.get(altName).toString() + ". Will now be connected to " + this.name);
		}
		nameMap.put(altName, this);
		nameMap.put(altName.toLowerCase(), this);
	}

	/**
	 * Sets the String that is returned by toString().
	 * 
	 * @param localizedSting
	 *            The localized String
	 */
	public static void localize(Context context) {
		nameMap = new HashMap<String, Muscle>();
		localized = true;

		int language = 0;
		if (Locale.getDefault().equals(Locale.GERMANY)) {
			language = 1;
		}

		String[][] arr = new String[13][];
		arr[0] = context.getResources().getStringArray(R.array.Chest);
		arr[1] = context.getResources().getStringArray(R.array.Abdominal);
		arr[2] = context.getResources().getStringArray(R.array.Back);
		arr[3] = context.getResources().getStringArray(R.array.Derriere);
		arr[4] = context.getResources().getStringArray(R.array.Shoulder);
		arr[5] = context.getResources().getStringArray(R.array.Biceps);
		arr[6] = context.getResources().getStringArray(R.array.Triceps);
		arr[7] = context.getResources().getStringArray(R.array.Thigh);
		arr[8] = context.getResources().getStringArray(R.array.Lower_leg);

		for (String[] mus : arr) {
			if (mus == null)
				continue;

			Muscle m;
			if (mus.length >= language)
				m = getByName(mus[language]);
			else
				m = getByName(mus[0]);
			for (String s : mus) {
				m.provideAlternativeName(s);
			}

		}

	}

	/**
	 * Returns the localized name.
	 */
	@Override
	public String toString() {
		if (!localized)
			throw new AssertionError("localize() was not called.");

		return name;
	}

	/**
	 * Static factory method. Objects will only be created once, after that old
	 * objects will be reused.
	 * 
	 * @param toolName
	 *            The name of the Muscle
	 * 
	 * @return The Muscle
	 */
	public static Muscle getByName(String toolName) {
		Muscle m = nameMap.get(toolName);
		if (m == null) {
			m = new Muscle(toolName);
			nameMap.put(toolName, m);
			Log.d(TAG, "Created new Muscle: " + toolName);
		}
		return m;

	}

	public static Iterable<Muscle> values() {
		return new TreeSet<Muscle>(Muscle.nameMap.values());
	}

	@Override
	public int compareTo(Muscle eq) {
		return this.toString().compareTo(eq.toString());
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Muscle))
			return false;

		return ((Muscle) o).toString().equals(this.toString());
	}

}
