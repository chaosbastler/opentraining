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
import de.skubware.opentraining.basic.SportsEquipment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.util.Log;

/**
 * Fragment for equipment preferences.
 */
public class PreferencesEquipmentFragment extends PreferenceFragment {

	/** Tag for logging */
	private static final String TAG = "PreferencesFragment";
	
	/** Empty constructor is required */
	public PreferencesEquipmentFragment(){ }

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.preferences_empty);
        
        // load settings
        SharedPreferences settings = this.getActivity().getSharedPreferences("OpenTraining", Context.MODE_PRIVATE);
        
        for(SportsEquipment eq:SportsEquipment.values()){
            CheckBoxPreference pref = new CheckBoxPreference(this.getActivity());
            pref.setTitle(eq.toString());
            pref.setKey(eq.toString());
            
            boolean enabled = settings.getBoolean(eq.toString(), true);
            pref.setChecked(enabled);
            
            this.getPreferenceScreen().addPreference(pref);
            Log.i(TAG, eq.toString() + " was loaded, value is: " + enabled); 
        }


        
	}
}
