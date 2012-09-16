package de.skubware.opentraining.activity.preferences;

import de.skubware.opentraining.R;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

/**
 * Fragment for equipment preferences.
 */
public class PreferencesNotImplementedFragment extends PreferenceFragment {

	/** Empty constructor is required */
	public PreferencesNotImplementedFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences_empty);

		Preference pref = new Preference(this.getActivity());
		pref.setTitle("Sorry, this feature is not implemented");
		pref.setSummary("Please wait for the next update :-)");
		this.getPreferenceScreen().addPreference(pref);

	}
}
