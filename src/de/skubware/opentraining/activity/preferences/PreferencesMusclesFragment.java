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
