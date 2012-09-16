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
public class PreferencesFragment extends PreferenceFragment {

	/** Tag for logging */
	private static final String TAG = "PreferencesFragment";
	
	/** Empty constructor is required */
	public PreferencesFragment(){ }

	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        addPreferencesFromResource(R.xml.preferences_equipment);
        
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
