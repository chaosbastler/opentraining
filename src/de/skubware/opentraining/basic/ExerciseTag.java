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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

//TODO make dynamic
//TODO extract an abstract super class
/**
 * This class should be used for "tagging" an ExerciseType.
 * It should behave like an enum, though it isn't one.
 * 
 * Reason: Later the ExerciseTags should be dynamically read at runtime.
 * Why? Two main reasons:
 *  - different languages(English, German, ...)
 *  - easy adding new ExerciseTags, perhaps even for users
 * 
 * Note: Enum may be a misleading comparison as TypeSavety can't be achieved by dynamically adding the values.
 * But as this class once was an enum class(before refactoring it), the term enum seems to be appropriate.
 * 
 * Behavior that is very similar to an enumeration:
 *  - only one instance per name
 *  - the class cares about initializing the instances, neither user nor programmer do have to care about that
 *  - Comparable<ExerciseTag>
 *  - method values()
 *  
 * More Behavior like Iterable<ExerciseTag> may easily be added.
 * 
 * 
 * 
 * @author Christian Skubich
 */
public class ExerciseTag  implements Comparable<ExerciseTag>{

	private static Set<ExerciseTag> values = new HashSet<ExerciseTag>();
	
	static{
		 values.add(new ExerciseTag("Eigengewicht Übung", "Eine Übung die ohne extra Gewicht ausgeführt wird, also nur mit dem eigenen Körpergewicht als Widerstand."));
		 values.add(new ExerciseTag("Fitness Studio Übung", "Eine Übung, die bevorzugt im Fitness Studio ausgeführt werden sollte(z.B. wegen \"exotischen\" Geräten),"));
		 values.add(new ExerciseTag("Heim Übung", "Eine Übung die auch gut zu Hause ausgeführt werden kann(z.B. weil man keine außergewöhnlichen Geräte braucht)."));
		 values.add(new ExerciseTag("Einsteiger Übung", "Eine Übung die auch für Einsteiger geeignet ist."));
		 values.add(new ExerciseTag("Fortgeschrittenen Übung", "Eine Übung die für Fortgeschrittene geeignet ist. Könnte zu schwierig oder kompliziert für Anfänger sein."));
		 values.add(new ExerciseTag("Experten Übung", "Eine Übung die für Experten geeignet ist. Sollte nur von erfahrenen Personen ausgeführt werden"));
		 values.add(new ExerciseTag("Isolierte Übung", "Eine Übung die primär eine einzelne Muskelgruppe anspricht"));
		 values.add(new ExerciseTag("Komplexe Übung", "Eine Übung die mehrere Muskelgruppen anspricht"));
	}
	/*
	 * Bisheriger Quellcode mit enum L�sung:
	 * 
	 * 
	 * BODY_WEIGHT_EXERCISE("Eigengewicht �bung",
	 * "Eine �bung die ohne extra Gewicht ausgef�hrt wird, also nur mit dem eigenen K�rpergewicht als Widerstand."
	 * ),
	 * 
	 * STUDIO_EXERCISE("Fitness Studio �bung",
	 * "Eine �bung, die bevorzugt im Fitness Studio ausgef�hrt werden sollte(z.B. wegen \"exotischen\" Ger�ten),"
	 * ), HOME_EXERCISE("Heim �bung",
	 * "Eine �bung die auch gut zu Hause ausgef�hrt werden kann(z.B. weil man keine au�ergew�hnlichen Ger�te braucht)."
	 * ),
	 * 
	 * DIFFICULTY_BEGINNER("Einsteiger �bung",
	 * "Eine �bung die auch f�r Einsteiger geeignet ist."),
	 * DIFFICULTY_ADVANCED("Fortgeschrittenen �bung",
	 * "Eine �bung die f�r Fortgeschrittene geeignet ist. K�nnte zu schwierig oder kompliziert f�r Anf�nger sein."
	 * ), DIFFICULTY_EXPERT("Experten �bung",
	 * "Eine �bung die f�r Experten geeignet ist. Sollte nur von erfahrenen Personen ausgef�hrt werden"
	 * ),
	 * 
	 * ISOLATED_EXERCISE("Isolierte �bung",
	 * "Eine �bung die prim�r eine einzelne Muskelgruppe anspricht"),
	 * COMPLEX_EXERCISE("Komplexe �bung",
	 * "Eine �bung die mehrere Muskelgruppen anspricht");
	 */

	private String name;
	private String description;

	private ExerciseTag(String name, String description) {
		this.name = name;
		this.description = description;
		
		if(ExerciseTag.values.contains(this)){
			System.err.println("Serious AssertionError in class " + ExerciseTag.class);
			throw new AssertionError("There cannot exist two ExerciseTags with the same name.");
		}
		
		ExerciseTag.values.add(this);
	}

	public static Set<ExerciseTag> values(){        	
        	return Collections.unmodifiableSet(values);
    }

	public String getName() {
		return this.name;
	}

	public String getDescription() {
		return this.description;
	}

	public static ExerciseTag getTagByValue(String name) {
		for (ExerciseTag tag : values()) {
			if (tag.name.equalsIgnoreCase(name)) {
				return tag;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public int compareTo(ExerciseTag o) {
		return o.toString().compareTo(this.toString());
	}

}