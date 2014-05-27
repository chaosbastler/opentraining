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
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.List;

/**
 * A TrainingEnty is particularly a collection of {@link FSet}s and stores the
 * training process ({@link FSet}s).
 * 
 * A TrainingEntry refers to a {@link Date} that represents the day/time when
 * the user trained. One {@code FitnessExercise} does refer to one ore more
 * {@link TrainingEntry}s.
 * 
 */
public class TrainingEntry implements Comparable<TrainingEntry>, Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Tag for logging */
	static final String TAG = "TrainingEntry";

	/** The mDate to which this Entry refers to. May be null. */
	private Date mDate;

	/** List with all {@link FSet}s. May be empty, but never null. */
	private List<FSet> mFSetList = new ArrayList<FSet>();

	/** Saves whether a FSet has been done or not. Default value is true. */
	private IdentityHashMap<FSet,Boolean> mSetBeenDoneMap = new IdentityHashMap<FSet,Boolean>();
	
	
	/**
	 * This constructor should be used, if the {@link #mDate} is unknown.
	 * 
	 * @deprecated Should only be used for parsing .xml files, use
	 *             {@link Workout#addTrainingEntry(java.util.Date)} instead.
	 */
	@Deprecated
	TrainingEntry() {
		this(null);
	}

	/**
	 * This constructor should be used, if the {@link #mDate} is known.
	 * 
	 * @param mDate
	 *            The mDate.
	 * 
	 * @deprecated Should only be used for parsing .xml files, use
	 *             {@link Workout#addTrainingEntry(java.util.Date)} instead.
	 */
	@Deprecated
	public TrainingEntry(Date date) {
		this.mDate = date;
	}

	/**
	 * Adds a FSet to this TrainingEntry.
	 * 
	 * @param set
	 *            The FSet to add (may not be null)
	 * 
	 */
	public void add(FSet set) {
		if(set == null)
			throw new NullPointerException("FSet may not be null");
		
		mFSetList.add(set);
		mSetBeenDoneMap.put(set, true);
	}

	/**
	 * Removes the given FSet if possible.
	 * 
	 * @param set
	 *            The FSet to remove.
	 * 
	 * @return True if operation was successful
	 */
	public boolean remove(FSet set) {
		mSetBeenDoneMap.remove(set);

		return mFSetList.remove(set);
	}

	/**
	 * Getter for {@link #mFSetList}.
	 * 
	 * @return The {@link #mFSetList}
	 */
	public List<FSet> getFSetList() {
		return mFSetList;
	}

	/**
	 * Getter for {@link #mDate}.
	 * 
	 * @return The {@link #mDate} (may be null)
	 */
	public Date getDate() {
		return mDate;
	}

	/**
	 * Setter for {@link #mDate}.
	 * 
	 * @param mDate
	 *            The new mDate to set, may be null
	 */
	public void setDate(Date date) {
		this.mDate = date;
	}

	/** Returns the comparison of the dates of the two TrainingEntries. */
	@Override
	public int compareTo(TrainingEntry another) {
		return this.mDate.compareTo(another.mDate);
	}

	@SuppressWarnings("deprecation")
	@Override
	public String toString(){
		return mDate.toLocaleString();
	}
	
	/**
	 * Returns a String that represents this object. Should only be used for
	 * debugging.
	 * 
	 * @return A String that represents this object.
	 */
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Date: " + mDate + "\n");
		for (FSet entry : mFSetList) {
			builder.append("\n FSet: " + entry.toString() + " , hasBeenDone=" + mSetBeenDoneMap.get(entry));
		}

		return builder.toString();
	}

	/**
	 * Returns whether the FSet has been done or not.
	 * 
	 * @param set The set that should be checked
	 * 
	 * @return true, if the set has been done
	 * 
	 * @throws NullPointerException if the set is not part of this TrainingEntry
	 */
	public boolean hasBeenDone(FSet set){
		if(!mFSetList.contains(set))
			throw new NullPointerException("No such key: " + set.toString());
				
		return mSetBeenDoneMap.get(set);
	}
	
	/**
	 * Sets whether the FSet has been done or not.
	 * 
	 * @param set The FSet to set
	 * 
	 * @param status The value to set
	 */
	public void setHasBeenDone(FSet set, boolean status){
		mSetBeenDoneMap.put(set, status);
	}

	/** Auto-generated hashCode() implementation. */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mDate == null) ? 0 : mDate.hashCode());
		result = prime * result
				+ ((mFSetList == null) ? 0 : mFSetList.hashCode());
		result = prime * result
				+ ((mSetBeenDoneMap == null) ? 0 : mSetBeenDoneMap.hashCode());
		return result;
	}

	/** Auto-generated equals() implementation. */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TrainingEntry other = (TrainingEntry) obj;
		if (mDate == null) {
			if (other.mDate != null)
				return false;
		} else if (!mDate.equals(other.mDate))
			return false;
		if (mFSetList == null) {
			if (other.mFSetList != null)
				return false;
		} else if (!mFSetList.equals(other.mFSetList))
			return false;
		if (mSetBeenDoneMap == null) {
			if (other.mSetBeenDoneMap != null)
				return false;
		} else if (!mSetBeenDoneMap.equals(other.mSetBeenDoneMap))
			return false;
		return true;
	}

}
