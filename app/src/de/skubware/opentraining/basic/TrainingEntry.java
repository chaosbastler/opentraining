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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.util.Log;

/**
 * A TrainingEnty is a collection of {@link TrainingSubEntry}s.
 * 
 * @author Christian Skubich
 * 
 */
public class TrainingEntry implements Comparable<TrainingEntry>, Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Tag for logging */
	static final String TAG = "TrainingEntry";

	/** The mDate to which this Entry refers to. May be null. */
	private Date mDate;

	/** List with all {@link TrainingSubEntry}s. May be empty, but never null. */
	private List<TrainingSubEntry> mSubEntryList = new ArrayList<TrainingSubEntry>();

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
	 * Adds a TrainingSubEntry to this TrainingEntry.
	 * 
	 * @param content
	 *            The content of the TrainingSubEntry to add (may be null)
	 * 
	 * @return The created {@link TrainingSubEntry}
	 * 
	 */
	public TrainingSubEntry add(String content) {
		Log.v(TAG, "Added TrainingSubEntry: " + content);
		@SuppressWarnings("deprecation")
		TrainingSubEntry entry = new TrainingSubEntry(content);
		this.mSubEntryList.add(entry);

		return entry;
	}

	/**
	 * Removes the given TrainingSubEntry if possible.
	 * 
	 * @param entry
	 *            The TrainingSubEntry to remove.
	 * 
	 * @return True if operation was successful
	 */
	public boolean remove(TrainingSubEntry entry) {
		return this.mSubEntryList.remove(entry);
	}

	/**
	 * Getter for {@link #mSubEntryList}.
	 * 
	 * @return The {@link #mSubEntryList}
	 */
	public List<TrainingSubEntry> getSubEntryList() {
		return mSubEntryList;
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

	/**
	 * Returns a String that represents this object. Should only be used for
	 * debugging.
	 * 
	 * @return A String that represents this object.
	 */
	public String toDebugString() {
		StringBuilder builder = new StringBuilder();

		builder.append("Date: " + mDate + "\n");
		for (TrainingSubEntry entry : mSubEntryList) {
			builder.append("\n TrainingEntry: " + entry.toString());
		}

		return builder.toString();
	}

}
