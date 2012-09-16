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

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.Muscle;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Fragment for muscle preferences.
 */
public class PreferencesMusclesFragment extends PreferenceFragment {

	/** Tag for logging */
	private static final String TAG = "PreferencesMusclesFragment";
	
	/** Empty constructor is required */
	public PreferencesMusclesFragment(){ }

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
        addPreferencesFromResource(R.xml.preferences_empty);
        
        // load settings
        SharedPreferences settings = this.getActivity().getSharedPreferences("OpenTraining", Context.MODE_PRIVATE);
        
        for(Muscle m:Muscle.values()){
            CheckBoxPreference pref = new CheckBoxPreference(this.getActivity());
            pref.setTitle(m.toString());
            pref.setKey(m.toString());
            
            boolean enabled = settings.getBoolean(m.toString(), true);
            pref.setChecked(enabled);
            
            this.getPreferenceScreen().addPreference(pref);
            Log.i(TAG, m.toString() + " was loaded, value is: " + enabled); 
        }


        
	}
}
