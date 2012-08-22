/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package de.skubware.opentraining;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.training_app.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SelectExercisesActivity extends Activity implements OnGestureListener {

	private List<ExerciseType> exerciseList = new ArrayList<ExerciseType>();
	private Map<Muscle, Boolean> muscleMap = new HashMap<Muscle, Boolean>();
	
	private ExerciseType currentExercise = ExerciseType.listExerciseTypes().first();
	private int currentImage = 0;

	private GestureDetector gestureScanner = new GestureDetector(this);




	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.select_exercises_menu, menu);

		// configure menu_item_add_exercise
		MenuItem menu_item_add_exercise = (MenuItem) menu.findItem(R.id.menu_item_add_exercise);
		menu_item_add_exercise
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						// Exercise Name
						TextView btn_ex_name = (TextView) findViewById(R.id.textview_exercise_name);
						String exName = btn_ex_name.getText().toString();
						SelectExercisesActivity.this.exerciseList
								.add(ExerciseType.getByName(exName));

						CharSequence text = "Die Übung " + exName
								+ " wurde hinzugefügt.";
						int duration = Toast.LENGTH_LONG;
						Toast toast = Toast.makeText(
								SelectExercisesActivity.this, text, duration);
						toast.show();

						return true;
					}
				});

		// configure menu_item_next
		final MenuItem menu_item_next = (MenuItem) menu
				.findItem(R.id.menu_item_next);
		menu_item_next
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {
					public boolean onMenuItemClick(MenuItem item) {
						if (exerciseList.isEmpty()) {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									SelectExercisesActivity.this);
							builder.setMessage(
									"Es wurden noch keine Übungen ausgewählt.")
									.setPositiveButton(
											"OK",
											new DialogInterface.OnClickListener() {
												public void onClick(
														DialogInterface dialog,
														int id) {
													dialog.cancel();
												}
											});
							AlertDialog alert = builder.create();
							alert.show();
							return true;
						} else {
							List<FitnessExercise> fEx = new ArrayList<FitnessExercise>();
							for (ExerciseType ex : exerciseList) {
								fEx.add(new FitnessExercise(ex));
							}
							DataManager.INSTANCE.setWorkout(
									new Workout("Mein Trainingsplan", fEx));
							startActivity(new Intent(
									SelectExercisesActivity.this,
									EditWorkoutActivity.class));
						}

						return true;
					}

				});

		// confiugre muscle drop down menu
		final List<MenuItem> muscleItems = new ArrayList<MenuItem>();
		for (Muscle m : Muscle.values()) {
			MenuItem item = menu.add(m.toString());
			muscleItems.add(item);
			item.setCheckable(true);
			item.setChecked(true);
			item.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					item.setChecked(!item.isChecked());
					muscleMap.put(Muscle.getByName(item.getTitle().toString()),
							item.isChecked());
					updateExList();

					return true;
				}
			});
		}
		MenuItem menuitem_uncheck_all = menu.add("Alle abw�hlen");
		menuitem_uncheck_all.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem menuitem){
				for(MenuItem item:muscleItems){
					item.setChecked(false);
					muscleMap.put(Muscle.getByName(item.getTitle().toString()),item.isChecked());
					updateExList();
				}
				return true;
			}
		});
		MenuItem menuitem_check_all = menu.add("Alle w�hlen");
		menuitem_check_all.setOnMenuItemClickListener(new OnMenuItemClickListener(){
			public boolean onMenuItemClick(MenuItem menuitem){
				for(MenuItem item:muscleItems){
					item.setChecked(true);
					muscleMap.put(Muscle.getByName(item.getTitle().toString()),item.isChecked());
					updateExList();
				}
				return true;
			}
		});

		return true;
	}

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// fill muscle map
		for (Muscle m : Muscle.values()) {
			muscleMap.put(m, true);
		}
		
		setContentView(R.layout.select_exercises);

		final ListView exListView = (ListView) findViewById(R.id.exListView);

		// Update ExList
		updateExList();

		// Show first exercise
		assert (!ExerciseType.listExerciseTypes().isEmpty());
		this.showExercise(ExerciseType.listExerciseTypes().first());

		// Set action onclick for exercise list
		exListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				String name = exListView.getAdapter().getItem(arg2).toString();
				ExerciseType ex = ExerciseType.getByName(name);
				showExercise(ex);
			}

		});


	}

	private void updateExList() {
		final ListView exListView = (ListView) findViewById(R.id.exListView);

		ArrayList<String> exes = new ArrayList<String>();
		for (ExerciseType exType : ExerciseType.listExerciseTypes()) {
			boolean shouldAdd = false;
			for (Muscle m : exType.getActivatedMuscles()) {
				if (muscleMap.get(m)) {
					shouldAdd = true;
					break;
				}
			}
			if (shouldAdd || exType.getActivatedMuscles().isEmpty())
				exes.add(exType.getName());
		}

		ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, exes);
		exListView.setAdapter(listAdapter);
		
		String name = exListView.getAdapter().getItem(0).toString();
		ExerciseType ex = ExerciseType.getByName(name);
		showExercise(ex);
	}

	private void showExercise(ExerciseType ex) {
		this.currentExercise = ex;
		// Description
		// EditText description = (EditText)
		// findViewById(R.id.edittext_description);
		// description.setText(ex.getDescription());

		// Exercise Name
		TextView exerciseName = (TextView) findViewById(R.id.textview_exercise_name);
		exerciseName.setText(ex.getName());

		// TODO: refactor this code, duplicate code can propably be replaced by
		// a generic one
		// Muscles
		{
			Collection<Muscle> muscles = ex.getActivatedMuscles();
			Iterator<Muscle> it = muscles.iterator();
			
			TextView textview_muscle = (TextView) findViewById(R.id.textview_muscle);
			if(muscles.isEmpty())
				textview_muscle.setVisibility(View.GONE);
			else
				textview_muscle.setVisibility(View.VISIBLE);
				
			TextView textview_muscle0 = (TextView) findViewById(R.id.textview_muscle0);
			TextView textview_muscle1 = (TextView) findViewById(R.id.textview_muscle1);

			if (!it.hasNext())
				textview_muscle0.setText("");
			else
				textview_muscle0.setText(it.next().toString());
			if (!it.hasNext())
				textview_muscle1.setText("");
			else
				textview_muscle1.setText(it.next().toString());
			

		}

		// Equipment
		{	
			Collection<SportsEquipment> eq = ex.getRequiredEquipment();
			Iterator<SportsEquipment> it = eq.iterator();
			
			TextView textview_equipment = (TextView) findViewById(R.id.textview_equipment);
			if(eq.isEmpty())
				textview_equipment.setVisibility(View.GONE);
			else
				textview_equipment.setVisibility(View.VISIBLE);
			
			TextView textview_equipment0 = (TextView) findViewById(R.id.textview_equipment0);
			TextView textview_equipment1 = (TextView) findViewById(R.id.textview_equipment1);

			if (!it.hasNext())
				textview_equipment0.setText("");
			else
				textview_equipment0.setText(it.next().toString());
			if (!it.hasNext())
				textview_equipment1.setText("");
			else
				textview_equipment1.setText(it.next().toString());
		}

		// Tags
		{
			Collection<ExerciseTag> tags = ex.getTags();
			Iterator<ExerciseTag> it = tags.iterator();
			
			TextView textview_tag = (TextView) findViewById(R.id.textview_tag);
			if(tags.isEmpty())
				textview_tag.setVisibility(View.GONE);
			else
				textview_tag.setVisibility(View.VISIBLE);
			
			TextView textview_tag0 = (TextView) findViewById(R.id.textview_tag0);
			TextView textview_tag1 = (TextView) findViewById(R.id.textview_tag1);

			if (!it.hasNext())
				textview_tag0.setText("");
			else
				textview_tag0.setText(it.next().toString());
			if (!it.hasNext())
				textview_tag1.setText("");
			else
				textview_tag1.setText(it.next().toString());
		}
		
		// Hints
		{
			Collection<String> hints = ex.getHints();
			Iterator<String> it = hints.iterator();
			
			TextView textview_hint = (TextView) findViewById(R.id.textview_hint);
			if(hints.isEmpty())
				textview_hint.setVisibility(View.GONE);
			else
				textview_hint.setVisibility(View.VISIBLE);
			
			TextView textview_hint0 = (TextView) findViewById(R.id.textview_hint0);
			TextView textview_hint1 = (TextView) findViewById(R.id.textview_hint1);

			if (!it.hasNext())
				textview_hint0.setText("");
			else
				textview_hint0.setText(it.next().toString());
			if (!it.hasNext())
				textview_hint1.setText("");
			else
				textview_hint1.setText(it.next().toString());
		}

		// Image License
		TextView image_license = (TextView) findViewById(R.id.textview_image_license);
		if (ex.getImageLicenseMap().values().iterator().hasNext()) {
			image_license.setText(ex.getImageLicenseMap().values().iterator()
					.next());
		} else {
			image_license.setText("Keine Lizenzinformationen vorhanden");
		}

		// Images
		// TODO remove dummy code
		ImageView imageview = (ImageView) findViewById(R.id.imageview);
		if(!ex.getImagePaths().isEmpty()){
			imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(ex.getImagePaths().get(0).toString()));
		}else{
			imageview.setImageResource(R.drawable.defaultimage);
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		return gestureScanner.onTouchEvent(me);
	}

	public boolean onDown(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {

		final ViewConfiguration vc = ViewConfiguration
				.get(SelectExercisesActivity.this);
		final int swipeMinDistance = vc.getScaledTouchSlop();
		final int swipeMaxDistance = vc.getScaledMaximumFlingVelocity();
		final int swipeThresholdVelocity = vc.getScaledMinimumFlingVelocity();

		try {
			if (Math.abs(e1.getY() - e2.getY()) > swipeMaxDistance)
				return false;
			// right to left swipe
			if (e1.getX() - e2.getX() > swipeMinDistance
					&& Math.abs(velocityX) > swipeThresholdVelocity) {
				//Toast.makeText(SelectExercisesActivity.this, "Left Swipe",	Toast.LENGTH_SHORT).show();
				ImageView imageview = (ImageView) findViewById(R.id.imageview);
				
				currentImage --;
				if(currentImage<0)
					currentImage = currentExercise.getImagePaths().size()-1;
				//TODO find a better solution than exception
				try{
					imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(currentExercise.getImagePaths().get(currentImage).toString()));
				}catch(IndexOutOfBoundsException ex){
					
				}
				
			} else if (e2.getX() - e1.getX() > swipeMinDistance
					&& Math.abs(velocityX) > swipeThresholdVelocity) {
				//Toast.makeText(SelectExercisesActivity.this, "Right Swipe",			Toast.LENGTH_SHORT).show();
				ImageView imageview = (ImageView) findViewById(R.id.imageview);
				
				currentImage ++;
				if(currentImage>=currentExercise.getImagePaths().size())
					currentImage = 0;
				//TODO find a better solution than exception
				try{
					imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(currentExercise.getImagePaths().get(currentImage).toString()));
				}catch(IndexOutOfBoundsException ex){
					
				}
			}
		} catch (Exception e) {
			// nothing
		}

		return true;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onShowPress(MotionEvent e) {
		// TODO Auto-generated method stub

	}

	public boolean onSingleTapUp(MotionEvent e) {
		// TODO Auto-generated method stub
		return false;
	}

}
