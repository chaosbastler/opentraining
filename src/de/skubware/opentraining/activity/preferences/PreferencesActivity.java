package de.skubware.opentraining.activity.preferences;

import java.util.List;

import de.skubware.opentraining.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PreferencesActivity extends PreferenceActivity {

	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	    }
	 
	 @Override
	    public void onBuildHeaders(List<Header> target) {
	        loadHeadersFromResource(R.xml.preference_header, target);
	    }

	 

}
