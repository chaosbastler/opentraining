package de.skubware.opentraining.activity.preferences;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.SportsEquipment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.util.Log;

public class PreferencesFragment extends PreferenceFragment {

	/** Tag for logging */
	private static final String TAG = "PreferencesFragment";
	
	
	public PreferencesFragment() {

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.preferences_equipment);
        
        SharedPreferences settings = this.getActivity().getSharedPreferences("OpenTraining", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        
        
        for(SportsEquipment eq:SportsEquipment.values()){
            CheckBoxPreference pref = new CheckBoxPreference(this.getActivity());
            pref.setTitle(eq.toString());
            pref.setKey(eq.toString());
            pref.setChecked(settings.getBoolean(eq.toString(), true));
            pref.setOnPreferenceChangeListener(new OnPreferenceChangeListener(){
				@Override
				public boolean onPreferenceChange(Preference pref, Object bo) {
					SharedPreferences settings = PreferencesFragment.this.getActivity().getSharedPreferences("OpenTraining", Context.MODE_PRIVATE);
			        SharedPreferences.Editor editor = settings.edit();
			        editor.putBoolean(pref.getKey(), (Boolean) bo);
					Log.i(TAG, pref.getKey() + " is set to " + bo.toString());
					return true;
				}
            });
            this.getPreferenceScreen().addPreference(pref);
            editor.putBoolean(eq.toString(), true);
        }

        editor.commit();

        
	}
}
