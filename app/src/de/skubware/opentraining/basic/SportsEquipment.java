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


import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * A class for handling SportsEquipment.
 * E.g. different names for the same equipment should be connected to the same equipment.
 * 
 * Only standard equipment can be localized.
 */
public class SportsEquipment implements Comparable<SportsEquipment>, Serializable{
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;
	
	/** Tag for logging */
	private static final String TAG = SportsEquipment.class.getName();

	
	/** An image of the SportsEquipment */
	private Drawable mImage;


	/**
	 * Map that connects names and SportsEquipment objects.
	 * 
	 * Reason: there may be alternative names
	 */
	private static Map<String, SportsEquipment> eqMap = new HashMap<String, SportsEquipment>();

	/** The name of the SportsEquipment */
	private String name;

	/**
	 * The Constructor
	 * 
	 * @param name
	 *            The name of the Tool, names should be used only once
	 */
	private SportsEquipment(String name) {
		this.name = name;
		eqMap.put(name, this);
		eqMap.put(name.toLowerCase(Locale.GERMANY), this);
	}
	
	/**
	 * Adds an alternative name to the SportsEquipment.
	 * 
	 * @param altName The new alternative name.
	 */
	public void provideAlternativeName(String altName){
		if(altName.equals(this.toString()))
			return;
		
		if(eqMap.containsKey(altName)){
			Log.w(TAG, "Warning: " + altName + " is already connected with " + eqMap.get(altName).toString() + ". Will now be connected to " + this.name);
		}
		eqMap.put(altName, this);
		eqMap.put(altName.toLowerCase(Locale.GERMANY), this);
	}
	
	/**
	 * Sets the String that is returned by toString().
	 * 
	 * @param localizedSting The localized String
	 */
	/*public static void localize(Context context){
		eqMap = new HashMap<String, SportsEquipment>();
		localized = true;
		
		
		int language = 0;
		if(Locale.getDefault().equals(Locale.GERMANY)){
			language = 1;
		}
		
		String[][] arr = new String[13][];
		arr[0] = context.getResources().getStringArray(R.array.None);
		arr[1] = context.getResources().getStringArray(R.array.Arm_Curl_Pad);
		arr[2] = context.getResources().getStringArray(R.array.Barbell);
		arr[3] = context.getResources().getStringArray(R.array.Bench);
		arr[4] = context.getResources().getStringArray(R.array.Dip_Stands);
		arr[5] = context.getResources().getStringArray(R.array.Dumbbell);
		arr[6] = context.getResources().getStringArray(R.array.Exercise_Mat);
		arr[7] = context.getResources().getStringArray(R.array.Hand_Strengtheners);
		arr[8] = context.getResources().getStringArray(R.array.Leg_Press);
		arr[9] = context.getResources().getStringArray(R.array.Plate);
		arr[10] = context.getResources().getStringArray(R.array.PullUp_Bar);
		arr[11] = context.getResources().getStringArray(R.array.Swiss_ball);
		arr[12] = context.getResources().getStringArray(R.array.SZ_Curl_Bar);


		for(String[] eqs:arr){
			if(eqs==null)
				continue;
			
			SportsEquipment eq;
			if(eqs.length>=language)
				eq =  getByName(eqs[language]);
			else
				eq =  getByName(eqs[0]);
			for(String s:eqs){
				eq.provideAlternativeName(s);
			}

		}
				
	}*/


	/**
	 * Returns the localized name.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Static factory method. Objects will only be created once, after that old
	 * objects will be reused.
	 * 
	 * @param toolName
	 *            The name of the SportsEquipment
	 * 
	 * @return The SportsEquipment
	 */
	public static SportsEquipment getByName(String toolName) {
		SportsEquipment eq = eqMap.get(toolName);
		if (eq == null) {
			eq = new SportsEquipment(toolName);
			eqMap.put(toolName, eq);
			Log.d(TAG, "Created new SportsEquipment: " + toolName);
		}
		return eq;

	}
	
	public static Iterable<SportsEquipment> values(){
		return new TreeSet<SportsEquipment>(SportsEquipment.eqMap.values());
	}

	@Override
	public int compareTo(SportsEquipment eq) {
		return this.toString().compareTo(eq.toString());
	}
	
	@Override
	public boolean equals(Object o){
		if(! (o instanceof SportsEquipment) )
			return false;
		
		return ((SportsEquipment)o).toString().equals(this.toString());
	}

	/**
	 * Setter for image.
	 * 
	 * @param image The new image
	 */
	public void setImage(Drawable image) {
		this.mImage = image;
	}
	
	/**
	 * Getter for image.
	 * 
	 * @return an image of the SportsEquipment, null if not set
	 */
	public Drawable getImage() {
		return mImage;
	}

}