package de.skubware.opentraining.activity;

import de.skubware.opentraining.R;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SettingsActivity extends TabActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_tab);

		TabHost tabHost = getTabHost();

		// tab for creating managing/downloading exercises
		TabSpec manage_exercises = tabHost.newTabSpec(getString(R.string.manage_exercises));
		// setting title and icon
		manage_exercises.setIndicator(getString(R.string.manage_exercises), getResources().getDrawable(R.drawable.icon_download_exercises));
		manage_exercises.setContent(new Intent(this, DownloadExercisesActivity.class));

		// tab for creating new exercises
		TabSpec create_ex = tabHost.newTabSpec(getString(R.string.create_new_exercise));
		// setting title and icon
		create_ex.setIndicator(getString(R.string.create_new_exercise), getResources().getDrawable(R.drawable.icon_new_ex));
		create_ex.setContent(new Intent(this, CreateExerciseActivity.class));

		// tab for choosing equipment settings
		TabSpec choose_equipment = tabHost.newTabSpec(getString(R.string.select_equipment));
		// setting title and icon
		choose_equipment.setIndicator(getString(R.string.select_equipment), getResources().getDrawable(R.drawable.icon_select_equipment));
		choose_equipment.setContent(new Intent(this, SettingsEquipmentActivity.class));

		// Adding all TabSpec to TabHost
		tabHost.addTab(manage_exercises);
		tabHost.addTab(create_ex);
		tabHost.addTab(choose_equipment);

	}

}
