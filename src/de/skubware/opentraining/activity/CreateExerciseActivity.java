package de.skubware.opentraining.activity;

import java.util.*;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.opentraining.R;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity to create new exercises.
 * 
 * @author Christian Skubich
 * 
 */
public class CreateExerciseActivity extends Activity {

	/** Tag for logging */
	private static final String TAG = "CreateExerciseActivity";
	
	private Map<ExerciseTag,CheckBox> tagMap = new HashMap<ExerciseTag,CheckBox>();
	private Map<Muscle,CheckBox> muscleMap = new HashMap<Muscle,CheckBox>();
	private Map<SportsEquipment,CheckBox> eqMap = new HashMap<SportsEquipment,CheckBox>();


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.create_exercise, menu);

		// configure menu_item_save
		final MenuItem menu_item_save = (MenuItem) menu.findItem(R.id.menu_item_save);
		menu_item_save.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				
				// get name and check if it is empty
				EditText edittext_exname = (EditText) findViewById(R.id.edittext_exname);
				String name = edittext_exname.getText().toString();
				if (name.isEmpty()) {
					Toast.makeText(CreateExerciseActivity.this, getString(R.string.name_required), Toast.LENGTH_SHORT).show();
					return true;
				}

				try {
					ExerciseType.Builder builder = new ExerciseType.Builder(name);

					
					// collect data
					
					// hint
					EditText edittext_hint = (EditText) findViewById(R.id.edittext_hint);
					String hint = edittext_hint.getText().toString();
					if(!hint.isEmpty()){
						List<String> hintList = new ArrayList<String>();
						builder.hints(hintList);
					}
					
					// description
					EditText edittext_description = (EditText) findViewById(R.id.edittext_description);
					String description = edittext_description.getText().toString();
					if(!description.isEmpty()){
						builder.description(description);
					}
					
					
					// tags
					SortedSet<ExerciseTag> tagList = new TreeSet<ExerciseTag>();
					for(ExerciseTag tag:ExerciseTag.values()){
						if(tagMap.get(tag).isChecked())
							tagList.add(tag);
					}
					if(!tagList.isEmpty())
						builder.exerciseTags(tagList);
					
					// muscles
					SortedSet<Muscle> muscleList = new TreeSet<Muscle>();
					for(Muscle m:Muscle.values()){
						if(muscleMap.get(m).isChecked())
							muscleList.add(m);
					}
					if(!muscleList.isEmpty())
						builder.activatedMuscles(muscleList);
					
					// equipment
					SortedSet<SportsEquipment> eqList = new TreeSet<SportsEquipment>();
					for(SportsEquipment s:SportsEquipment.values()){
						if(eqMap.get(s).isChecked())
							eqList.add(s);
					}
					if(!eqList.isEmpty())
						builder.neededTools(eqList);

					
					ExerciseType exType = builder.build();
					Log.i(TAG, "Created ExerciseType: " + exType.toString());

					boolean succ = DataManager.INSTANCE.saveExercise(exType);

					AlertDialog.Builder b = new AlertDialog.Builder(CreateExerciseActivity.this);
					b.setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
							finish();
						}
					});

					if (succ) {
						b.setMessage(R.string.success);
					} else {
						b.setMessage(R.string.no_success);
					}
					AlertDialog alert = b.create();
					alert.show();
				} catch (IllegalArgumentException ex) {
					AlertDialog.Builder builder = new AlertDialog.Builder(CreateExerciseActivity.this);
					builder.setMessage(ex.getMessage()).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
				}
				return true;
			}
		});
		// configure menu_item_cancel
		final MenuItem menu_item_cancel = (MenuItem) menu.findItem(R.id.menu_item_cancel);
		menu_item_cancel.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				finish();
				return true;
			}
		});

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.create_exercise);

		// add muscle checkboxes
		ViewGroup layout = (ViewGroup) findViewById(R.id.wrapper_muscles);
		for (Muscle m : Muscle.values()) {
			CheckBox b = new CheckBox(this);
			b.setText(m.toString());
			muscleMap.put(m, b);
			layout.addView(b);
		}

		// add equipment checkboxes
		layout = (ViewGroup) findViewById(R.id.wrapper_equipment);
		for (SportsEquipment e : SportsEquipment.values()) {
			CheckBox b = new CheckBox(this);
			b.setText(e.toString());
			eqMap.put(e, b);
			layout.addView(b);
		}

		// add tag checkboxes
		layout = (ViewGroup) findViewById(R.id.wrapper_exercisetags);
		for (ExerciseTag tag : ExerciseTag.values()) {
			CheckBox b = new CheckBox(this);
			b.setText(tag.toString());
			tagMap.put(tag, b);
			layout.addView(b);
		}
	}

}
