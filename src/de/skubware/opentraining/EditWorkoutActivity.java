package de.skubware.opentraining;

import java.util.HashMap;
import java.util.Map;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.*;

import de.skubware.training_app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//TODO: use hints and icon image

public class EditWorkoutActivity extends Activity {
	
	private Map<Integer, Integer> columnWidthMap = new HashMap<Integer, Integer>();
	
	private int columnCount;
	private int emptyRowCount;

	private final static int COLUMN_PADDING = 5;
	private final static int ROW_PADDING = 5;
	private final static int ROW_HEIGHT = 80;

	
	public EditWorkoutActivity(){
		super();

		this.emptyRowCount = 6;
		
		this.columnCount =  DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().size() + 1;
		this.columnWidthMap.put(0, 100);
		for(int i = 1; i<columnCount; i++){
			columnWidthMap.put(i, 180);
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_workout);
        
        // workout name
        EditText edittext_name = (EditText) findViewById(R.id.edittext_workout_name);
        edittext_name.setText(DataManager.INSTANCE.getCurrentWorkout().getName());
        
        
        // button + row
        Button btn_add_row = (Button) findViewById(R.id.btn_add_row);
        btn_add_row.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				addRow();
			}
        });
        
        // button - row
        Button btn_remove_row = (Button) findViewById(R.id.btn_remove_row);
        btn_remove_row.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				removeRow();
			}
        });
        

        // button export
        Button button_export = (Button) findViewById(R.id.button_export);
        button_export.setOnClickListener(new OnClickListener(){
			public void onClick(View arg0) {
				
				final CharSequence[] items = {"Default", "Boring", "Modern", "Ninja"};

				AlertDialog.Builder builder = new AlertDialog.Builder(EditWorkoutActivity.this);
				builder.setTitle("Design wählen");
				builder.setItems(items, new DialogInterface.OnClickListener() {
				    public void onClick(DialogInterface dialog, int item) {
				    	String css;
				    	switch(item){
				    		case 0:
				    			css = "trainingplan_default.css";
				    			break;
				    		case 1:
				    			css = "trainingplan_boring.css";
				    			break;
				    		case 2:
				    			css = "trainingplan_modern.css";
				    			break;
				    		case 3:
				    			css = "trainingplan_ninja.css";
				    			break;
				    		default:
				    			throw new IllegalStateException("This action is not supported.");
				    	}
				    	DataManager.INSTANCE.setCSSFile(css);
						startActivity(new Intent(EditWorkoutActivity.this, ShowTPActivity.class));						 
				        Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
				    }
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				
			}
        });
        
        // finally show the current workout
        this.updateTable();
        
	}
	
	private void addRow(){
		this.emptyRowCount ++;
		this.updateTable();
	}
	
	private void removeRow(){
		if(this.emptyRowCount>1)
			this.emptyRowCount--;
		this.updateTable();
	}
	
	/**
	 * Updates the workout table
	 * @param workout2
	 */
	private void updateTable() {
        // workout name
        EditText edittext_name = (EditText) findViewById(R.id.edittext_workout_name);
		String new_name = edittext_name.getText().toString();
		if(new_name!=null && !new_name.isEmpty()){
			Workout newWorkout = new Workout(new_name,  DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises());
			DataManager.INSTANCE.setWorkout(newWorkout);
		}
        

		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();

		
        this.buildFirstRow();
        this.buildEmptyRows();		
	}

	public TextView getStyledTextView(String text){
		
	    
        /*<style name="LargeTextView">
    	<item name="android:layout_width">fill_parent</item>
    	<item name="android:layout_height">wrap_content</item>
    	<item name="android:textColor">#000000</item>
    	<item name="android:gravity">center</item>
    	<item name="android:layout_margin">3dp</item>
    	<item name="android:textSize">22dp</item>
    	<item name="android:textStyle">bold</item>
    	</style>*/
        TextView tw = new TextView(this);
        tw.setTextColor(Color.BLACK);
        tw.setText(text);
        tw.setTypeface(null, Typeface.BOLD);
        tw.setTextSize(22);
        tw.setPadding(15, 15, 15, 15);
        tw.setHeight(ROW_HEIGHT);
        tw.setGravity(Gravity.CENTER_HORIZONTAL);
        


        
        Drawable border = (Drawable) getResources().getDrawable(R.drawable.border);
        tw.setBackgroundDrawable(border);
        return tw;

	}
	
	private void addColumPadding(TableRow row, int paddingwidht) {
        TextView tw = new TextView(this);
        tw.setHeight(ROW_HEIGHT);
        tw.setWidth(paddingwidht);
        
        row.addView(tw);
	}
	
	
	
	private void buildFirstRow(){
		TableLayout table =  (TableLayout) findViewById(R.id.table);

		TableRow firstrow =  new TableRow(this);
		firstrow.setPadding(0, ROW_PADDING, ROW_PADDING, 0);

		
		// Date
		TextView date = this.getStyledTextView("Datum");
        date.setWidth(this.columnWidthMap.get(0));
        firstrow.addView(date);
        
        // for space between colums empty tw
    	this.addColumPadding(firstrow, COLUMN_PADDING);
        
        int i = 1;
        for(FitnessExercise fEx:DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises()){
        	TextView tw = this.getStyledTextView(fEx.toString());
        	tw.setWidth(this.columnWidthMap.get(i));

            firstrow.addView(tw);
            
            // for space between colums empty tw
        	this.addColumPadding(firstrow, COLUMN_PADDING);
        	
        	i++;
        }
        
        table.addView(firstrow);
        

	}
	


	private void buildEmptyRows(){
		TableLayout table =  (TableLayout) findViewById(R.id.table);
		
		for(int i = 0; i<this.emptyRowCount; i++){
			TableRow row = new TableRow(this);
			row.setPadding(0, ROW_PADDING, ROW_PADDING, 0);


			for(int k = 0;k<=DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().size(); k++){
				row.addView(this.getStyledTextView(""));
		    	this.addColumPadding(row, COLUMN_PADDING);
			}
			table.addView(row);
		}
	}

}
