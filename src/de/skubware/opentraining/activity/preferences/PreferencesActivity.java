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

package de.skubware.opentraining.activity.preferences;

import java.util.List;

import de.skubware.opentraining.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	/** {@link onResume()} */
	private List<Header> mHeaders;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onBuildHeaders(List<Header> target) {
		loadHeadersFromResource(R.xml.preference_header, target);
		mHeaders = target;
	}

	/**
	 * This is a workaround for a bug/strange behaviour of android.
	 * http://code.google.com/p/android/issues/detail?id=22430
	 */
	/*@Override
	protected void onResume() {
		super.onResume();
		final String showFragment = getIntent().getStringExtra(EXTRA_SHOW_FRAGMENT);
		if (showFragment != null) {
			for (final Header header : mHeaders) {
				if (showFragment.equals(header.fragment)) {
					switchToHeader(header);
					break;
				}
			}
		}
	}*/

}
