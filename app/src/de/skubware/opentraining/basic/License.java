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
import java.net.MalformedURLException;
import java.net.URL;

import android.util.Log;

/**
 * Class for representing the license of an image. Currently not used, will be
 * required in future (when there are more exercises with images from different
 * authors).
 */
public class License implements Serializable {
	/** Default serialVersionUID */
	private static final long serialVersionUID = 1L;

	/** Tag for logging */
	static final String TAG = "License";

	/**
	 * Enumeration for the general type of the license. Each license type
	 * contains an URL to the license text.
	 */
	public enum LicenseType {
		/** Similar to public domain */
		CC0("http://creativecommons.org/publicdomain/zero/1.0/", "CC0"),
		/** Attribution to author required */
		CC_BY_UNPORTED_3("http://creativecommons.org/licenses/by/3.0/", "CC-BY 3"),
		CC_BY_UNPORTED_4("http://creativecommons.org/licenses/by/3.0/", "CC-BY 4"),
		/** Share alike and attribution to author required */
		CC_BY_SA_UNPORTED_3("http://creativecommons.org/licenses/by/3.0/", "CC-BY-SA 3"),
		CC_BY_SA_UNPORTED_4("http://creativecommons.org/licenses/by/3.0/", "CC-BY-SA 4"),
		/** Unknown */
		UNKNOWN("http://http://creativecommons.org/", "Unknown")
		;

		/** URL to the license */
		URL urlToLicense;
		
		String mShortName;

		/**
		 * 
		 * @param url
		 */
		LicenseType(String url, String short_name) {
			mShortName = short_name;
					
			try {
				this.urlToLicense = new URL(url);
			} catch (MalformedURLException e) {
				Log.e(TAG, "MalformedURL: " + url, e);
			}
		}
		
		@Override
		public String toString(){
			return mShortName;
		}
		
		public String getShortName(){
			return mShortName;
		}
	}

	/** The general type of this license */
	private LicenseType type;

	/** The author */
	private String author;

	/**
	 * Default constructor without parameters. LicenseType is set to
	 * {@link LicenseType#UNKNOWN}, author may remain empty.
	 */
	public License() {
		this.type = LicenseType.UNKNOWN;
		this.author = "Unknown";
	}

	/**
	 * Constructor, requires a {@link #LicenseType}.
	 * 
	 * @param type
	 *            the general type of license
	 */
	public License(LicenseType type, String author) {
		this.type = type;
		this.author = author;
	}

	/**
	 * Getter for the license type.
	 * 
	 * @return the license type
	 */
	public LicenseType getLicenseType() {
		return this.type;
	}

	/**
	 * Getter for author.
	 * 
	 * @return the author
	 */
	public String getAuthor() {
		return this.author;
	}
	
	@Override
	public String toString(){
		return "License: " + type.mShortName + ", Author: " + author;
	}

}
