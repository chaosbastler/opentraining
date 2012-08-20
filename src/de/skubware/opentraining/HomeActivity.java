package de.skubware.opentraining;

import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.datamanagement.*;
import de.skubware.training_app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;


public class HomeActivity extends Activity {
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.home_menu, menu);
    	
    	// configure menu_button_select_exercises
        final MenuItem  menu_button_select_exercises =(MenuItem) menu.findItem(R.id.menu_button_select_exercises);
        menu_button_select_exercises.setOnMenuItemClickListener( new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem item) {
				if(!ExerciseType.listExerciseTypes().isEmpty())
					startActivity(new Intent(HomeActivity.this, SelectExercisesActivity.class));
				else{
					AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
	            	builder.setMessage("Es sind keine Übungen in der Datenbank vorhanden.");
	            	AlertDialog alert = builder.create();
	            	alert.show();
				}
				return true;
			}
        }); 

    	
    	// configure menu_button_manage_database
        final MenuItem  menu_button_manage_database =(MenuItem) menu.findItem(R.id.menu_button_manage_database);
        menu_button_manage_database.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(HomeActivity.this, ManageDatabaseActivity.class));
			
				return true;
			}
        }); 
    	
        // configure menu_button_settings
    	MenuItem menu_button_settings = (MenuItem) menu.findItem(R.id.menu_button_settings);
        menu_button_settings.setOnMenuItemClickListener(this.getNotSupportedYetOnMenuItemClickListener());

        // configure menu_button_help
        final MenuItem  menu_button_help =(MenuItem) menu.findItem(R.id.menu_button_help);
        menu_button_help.setOnMenuItemClickListener(this.getNotSupportedYetOnMenuItemClickListener()); 

    	return true;
	}
	
	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        
        // Load exercises
		DataManager.INSTANCE.loadExercises(this);
        
    }
    
    
    /**
     * Creates a dialog that informs the user, that this is not supported yet.
     * 
     * @return
     */
    private OnMenuItemClickListener getNotSupportedYetOnMenuItemClickListener(){
    	
    	return new OnMenuItemClickListener() {

			public boolean onMenuItemClick(MenuItem item) {
            	AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            	builder.setMessage("Diese Funktion ist noch nicht verfügbar.")
            	       .setCancelable(true)
            	       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	        	   dialog.cancel();
            	           }
            	       })
            	       .setNegativeButton("Na toll ...", new DialogInterface.OnClickListener() {
            	           public void onClick(DialogInterface dialog, int id) {
            	                dialog.cancel();
            	           }
            	       });
            	AlertDialog alert = builder.create();
            	alert.show();
            	return true;
			}
        };
  
    }

    
}