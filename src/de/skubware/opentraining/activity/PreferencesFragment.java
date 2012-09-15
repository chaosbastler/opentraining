package de.skubware.opentraining.activity;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.SportsEquipment;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

public class PreferencesFragment extends PreferenceFragment {

	public PreferencesFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.preferences_equipment);
        
        for(SportsEquipment eq:SportsEquipment.values()){
            CheckBoxPreference pref = new CheckBoxPreference(this.getActivity());
            pref.setTitle(eq.toString());
            pref.setChecked(true);
            this.getPreferenceScreen().addPreference(pref);
            
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this.getActivity());
            Editor editor = sharedPref.edit();
            editor.putBoolean(eq.toString(), true);
            editor.commit();
        }


		
	}
}
