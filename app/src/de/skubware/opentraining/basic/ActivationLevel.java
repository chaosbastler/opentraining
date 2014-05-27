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

public enum ActivationLevel implements Serializable {
	LOW(1), MEDIUM(3), ENORMOUS(5);

	private final int level;
	public static final int MIN_LEVEL = 1;
	public static final int MAX_LEVEL = 5;

	/**
	 * Constructor for this Enum
	 * 
	 * @param level
	 *            The level, must be between {@value #MIN_LEVEL} and
	 *            {@value #MAX_LEVEL}
	 */
	ActivationLevel(int level) {
		// check argument
		if (!(MIN_LEVEL <= level && level <= ActivationLevel.MAX_LEVEL)) {
			throw new IllegalArgumentException("Not valid level: " + level);
		}
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}

	/**
	 * Gets an ActivationLevel by the int value of it's level.
	 * 
	 * @param level
	 *            The level of the ActivationLevel
	 * 
	 * @return The corresponding ActivationLevel
	 * 
	 * @throws IllegalArgumentException
	 *             if there is no such ActivationLevel
	 */
	public static ActivationLevel getByLevel(int level) {
		for (ActivationLevel a : ActivationLevel.values()) {
			if (a.level == level) {
				return a;
			}
		}

		throw new IllegalArgumentException("An ActivationLevel with the level " + level + " does not exist");
	}

}