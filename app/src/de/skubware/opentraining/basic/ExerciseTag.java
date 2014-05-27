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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;



/**
 * This class should be used for "tagging" an ExerciseType.
 */
public class ExerciseTag extends Translatable {

	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	

	/** Map that stores the description for each Locale. */
	private Map<Locale, String> descriptionMap = new HashMap<Locale, String>();

	public ExerciseTag(Locale locale, List<String> nameList, String description) {
		super(locale, nameList);
		descriptionMap.put(locale, description);
	}

	public void addNames(Locale locale, List<String> nameList, String description) {
		super.addNames(locale, nameList);
		descriptionMap.put(locale, description);
	}

}