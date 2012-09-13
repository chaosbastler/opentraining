package de.skubware.opentraining.activity;

import de.skubware.opentraining.R;
import android.os.Bundle;
import android.app.TabActivity;
import android.content.Intent;
import android.view.Menu;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

public class SettingsActivity extends TabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_tab);
        
 
        TabHost tabHost = getTabHost();
 
        // Tab for Photos
        TabSpec manage_exercises = tabHost.newTabSpec(getString(R.string.manage_exercises));
        // setting Title and Icon for the Tab
        manage_exercises.setIndicator(getString(R.string.manage_exercises), getResources().getDrawable(R.drawable.icon_muscle));
        Intent photosIntent = new Intent(this, DownloadExercisesActivity.class);
        manage_exercises.setContent(photosIntent);
 
        
     // Tab for Videos
        TabSpec create_ex = tabHost.newTabSpec( getString(R.string.create_new_exercise) );
        create_ex.setIndicator(getString(R.string.create_new_exercise), getResources().getDrawable(R.drawable.icon_new_ex));
        Intent videosIntent = new Intent(this, CreateExerciseActivity.class);
        create_ex.setContent(videosIntent);
 
        // Adding all TabSpec to TabHost
        tabHost.addTab(manage_exercises); 
        tabHost.addTab(create_ex); // Adding videos tab
    }

}
