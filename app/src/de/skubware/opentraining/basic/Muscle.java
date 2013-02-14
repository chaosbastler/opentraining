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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * An enumeration like class for the different muscles of a men(or women).
 * 
 * 
 * @author Christian Skubich
 * 
 */
public class Muscle implements Comparable<Muscle>, Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;	
	
	/** Tag for logging */
	@SuppressWarnings("unused")
	private static final String TAG = "Muscle";

	/**
	 * Map that connects names and Muscle objects.
	 */
	private Map<Locale, Set<String>> nameMap = new HashMap<Locale, Set<String>>();

	/** The (primary) name of the Muscle */
	private String name;

	/**
	 * 
	 * 
	 * @param locale
	 * @param nameList The list with the primary name and the alternative names. The first name of the list will be the primary name.
	 */
	public Muscle(Locale locale, List<String> nameList) {
		this.addNames(locale, nameList);
		name = nameList.get(0);
	}


	public void addNames(Locale locale, String ... names){
		if(nameMap.get(locale) == null){
			Set<String> nameSet = new HashSet<String>();
			nameMap.put(locale, nameSet);
		}
		
		Locale userLocale = Locale.getDefault();
		if(locale.getLanguage().equals(userLocale.getLanguage())){
			this.name = names[0];
		}
		
		Set<String> nameSet = nameMap.get(locale);
		for(String name:names){
			nameSet.add(name);
			nameSet.add(name.toLowerCase(Locale.GERMANY));
		}
		
	}
	
	public void addNames(Locale locale, List<String> nameList){
		this.addNames(locale, nameList.toArray(new String[nameList.size()]));
	}
	
	public boolean isAlternativeName(String name){
		for(Locale locale:nameMap.keySet()){
			if(nameMap.get(locale).contains(name))
				return true;

		}
		
		return false;
	}

	/**
	 * Returns the localized name.
	 */
	@Override
	public String toString(){
		return name;
	}
	
	/**
	 * Returns a String that represents this object.
	 * Should only be used for debugging.
	 * 
	 * @return A String that represents this object.
	 */
	public String toDebugString(){
		StringBuilder builder = new StringBuilder();
		
		builder.append("Primary name: " + name+ "\n");
		for(Locale locale:nameMap.keySet()){
			builder.append("\n Locale/language: " + locale.getLanguage().toString());
			for(String name:nameMap.get(locale)){
				builder.append("\n  - " + name);
			}
		}

		
		return builder.toString();
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
