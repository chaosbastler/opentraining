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

import android.util.Log;

/**
 * This class stores user generated entries like: "10 x 50 kg".
 * 
 * @author Christian Skubich
 * 
 */
public class TrainingSubEntry implements Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Tag for logging */
	static final String TAG = "TrainingEntry";

	/** Stores the content of this TrainingSubEntry, is never null. */
	String content;

	/**
	 * Constructor.
	 * 
	 * @param content
	 *            The content of this TrainingSubEntry. Null arguments will be
	 *            replaced by empty strings.
	 * 
	 * @deprecated Should only be used for parsing .xml files, use
	 *             {@link TrainingEntry#add(String)} instead.
	 * 
	 */
	@Deprecated
	public TrainingSubEntry(String content) {
		setContent(content);
	}

	/**
	 * Getter for {@link #content}
	 * 
	 * @return The content of this TrainingSubEntry.
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Setter for {@link #content}
	 * 
	 * @param content
	 *            The new content of this TrainingSubEntry.
	 * 
	 */
	public void setContent(String content) {
		Log.v(TAG, "Set new TrainingSubEntry content: " + content);
		if (content == null) {
			this.content = "";
			return;
		}

		this.content = content;
	}

	@Override
	public String toString() {
		return getContent();
	}

}
