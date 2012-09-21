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

package de.skubware.opentraining.activity.show_workout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.skubware.opentraining.activity.ShowTPActivity;
import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.*;
import de.skubware.opentraining.datamanagement.ContentProvider.CSSFile;

import de.skubware.opentraining.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity shows the plan(table) with the selected exercises. The user can
 * select the number of empty rows, change the name of the plan and finally
 * export it.
 * 
 * 
 * @author Christian Skubich
 * 
 */
public class ShowWorkoutActivity extends Activity {
	/** Tag for logging */
	static final String TAG = "EditWorkoutActivity";

	/** Contains the current column number of each TextView */
	Map<TextView, Integer> columnNumberMap = new HashMap<TextView, Integer>();

	/** Contains the exercise that belongs to a textview */
	Map<TextView, FitnessExercise> exerciseMap = new HashMap<TextView, FitnessExercise>();
	
	TextView lastTouched;

	// some attributes for the style/design of the table
	private int max_height;
	private int max_width;

	/**
	 * Configures the menu actions.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.show_workout_menu, menu);

		// configure menu_item_save_plan
		final MenuItem menu_item_save_plan = (MenuItem) menu.findItem(R.id.menu_item_save_plan);
		menu_item_save_plan.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				boolean success = ContentProvider.INSTANCE.savePlan();
				AlertDialog.Builder builder = new AlertDialog.Builder(ShowWorkoutActivity.this);
				if (success) {
					builder.setMessage(getString(R.string.success));
				} else {
					builder.setMessage(getString(R.string.no_success));

				}
				builder.setNeutralButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
				AlertDialog alert = builder.create();
				alert.show();
				return true;
			}
		});

		// configure menu_item_export_plan
		final MenuItem menu_item_export_plan = (MenuItem) menu.findItem(R.id.menu_item_export_plan);
		menu_item_export_plan.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				final CharSequence[] items = CSSFile.items;

				AlertDialog.Builder builder = new AlertDialog.Builder(ShowWorkoutActivity.this);
				builder.setTitle(getString(R.string.choose_design));
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						String css = CSSFile.items[item].toString();
						CSSFile cssFile = CSSFile.valueOf(CSSFile.class, css);

						ContentProvider.INSTANCE.setCSSFile(cssFile);
						Log.d(TAG, "Starting export of workout, row count: " + ContentProvider.INSTANCE.getCurrentWorkout().getEmptyRows());
						startActivity(new Intent(ShowWorkoutActivity.this, ShowTPActivity.class));
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
				return true;
			}
		});
		
		
		// configure menu_item_add_row
		final MenuItem menu_item_add_row = (MenuItem) menu.findItem(R.id.menu_item_add_row);
		menu_item_add_row.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				addRow();
				return true;
			}
		});

		// configure menu_item_remove_row
		final MenuItem menu_item_remove_row = (MenuItem) menu.findItem(R.id.menu_item_remove_row);
		menu_item_remove_row.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem item) {
				removeRow();
				return true;
			}
		});
		
		
		return true;
	}

	/**
	 * Initializes the variables, updates the UI, sets the action for export
	 * button.
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_workout);



		// waste basket
		//ImageView imageview_waste_basket = (ImageView) findViewById(R.id.imageview_waste_basket);
		//imageview_waste_basket.setOnDragListener(new DragColumnListener(this));

		// finally show the current workout
		this.updateTable();

	}

	/** Increases the number of rows. */
	private void addRow() {
		ContentProvider.INSTANCE.getCurrentWorkout().setEmptyRows(ContentProvider.INSTANCE.getCurrentWorkout().getEmptyRows() + 1);

		this.updateTable();
	}

	/** Decreases the number of rows (if >1). */
	private void removeRow() {
		if (ContentProvider.INSTANCE.getCurrentWorkout().getEmptyRows() > 1) {
			ContentProvider.INSTANCE.getCurrentWorkout().setEmptyRows(ContentProvider.INSTANCE.getCurrentWorkout().getEmptyRows() - 1);
		}
		this.updateTable();
	}

	/**
	 * Updates the workout table.
	 */
	void updateTable() {

		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();

		this.buildFirstRow();
		this.buildEmptyRows();
	}

	public TextView getStyledTextView(String text) {
		TextView tw = new TextView(this);

		tw.setGravity(Gravity.CENTER);
		tw.setPadding(5, 5, 5, 5);
		tw.setBackgroundResource(R.drawable.border);
		tw.setTextAppearance(this, R.style.textview_firstrow);
		tw.setText(text);
		tw.setMovementMethod(ScrollingMovementMethod.getInstance());

		return tw;

	}

	private void buildFirstRow() {
		TableLayout table = (TableLayout) findViewById(R.id.table);

		TableRow firstrow = new TableRow(this);
		firstrow.setBackgroundColor(0xFF7FAF7F);
		firstrow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// Date
		TextView date = this.getStyledTextView(getString(R.string.date));
		firstrow.addView(date);

		// store all textviews to calculate max with / height
		List<TextView> textviewList = new ArrayList<TextView>();

		int i = 1;
		for (FitnessExercise fEx : ContentProvider.INSTANCE.getCurrentWorkout().getFitnessExercises()) {
			TextView tw = this.getStyledTextView(fEx.toString());
			// tw.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_yellow));

			firstrow.addView(tw);
			textviewList.add(tw);

			// add touch action, anonymous class did not work (because the
			// argument is needed)
			tw.setOnTouchListener(new TouchColumnListener(this));
			tw.setOnDragListener(new DragColumnListener(this));
			this.columnNumberMap.put(tw, i);
			this.exerciseMap.put(tw, fEx);

			i++;
		}

		table.addView(firstrow);

		// set minimum values
		this.max_height = 50;
		this.max_width = 100;

		
		for (TextView tw : textviewList) {
			tw.measure(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			if (tw.getMeasuredHeight() > this.max_height)
				this.max_height = tw.getMeasuredHeight();
			if (tw.getMeasuredWidth() > this.max_width)
				this.max_width = tw.getMeasuredWidth();
		}

		Log.d(TAG, "max_width = " + this.max_width);
		Log.d(TAG, "max_height = " + this.max_height);

		for (TextView tw : textviewList) {
			tw.setHeight(this.max_height);
			tw.setWidth(this.max_width);
		}
		date.setHeight(this.max_height);
	}

	private void buildEmptyRows() {
		TableLayout table = (TableLayout) findViewById(R.id.table);

		for (int i = 0; i < ContentProvider.INSTANCE.getCurrentWorkout().getEmptyRows(); i++) {
			TableRow row = new TableRow(this);
			row.setBackgroundColor(0xFFDFDFDF);

			for (int k = 0; k <= ContentProvider.INSTANCE.getCurrentWorkout().getFitnessExercises().size(); k++) {
				TextView emptyTW = this.getStyledTextView("");
				emptyTW.setTextAppearance(this, R.style.textview_emptyrow);
				emptyTW.setTextColor(0xFF7F7F7F);

				// for performance reasons one could cache the ColumnListeners
				// in a Map
				this.columnNumberMap.put(emptyTW, k);

				row.addView(emptyTW);
				// this.addColumPadding(row, COLUMN_PADDING);
			}
			table.addView(row);
		}
	}
	

	/**
	 * Redefine 'backbutton'. User should always return to
	 * SelectExerciseActivity.
	 */
	@Override
	public void onBackPressed() {
		finish();
		startActivity(new Intent(ShowWorkoutActivity.this, de.skubware.opentraining.activity.select_exercises.ExerciseListActivity.class));
		return;
	}
	
	
	ActionMode mActionMode= null;
	ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

	    // Called when the action mode is created; startActionMode() was called
	    @Override
	    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
	        // Inflate a menu resource providing context menu items
	        MenuInflater inflater = mode.getMenuInflater();
	        inflater.inflate(R.menu.show_workout_context_menu, menu);
	        
	        return true;
	    }

	    // Called each time the action mode is shown. Always called after onCreateActionMode, but
	    // may be called multiple times if the mode is invalidated.
	    @Override
	    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

	        return false; // Return false if nothing is done
	    }

	    // Called when the user selects a contextual menu item
	    @Override
	    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
	        switch (item.getItemId()) {
	            case R.id.menu_item_waste_basket:
	            	if(lastTouched!=null)
	            		removeColumn(lastTouched);
	            	lastTouched=null;
	                //shareCurrentItem();
	                mode.finish(); // Action picked, so close the CAB
	                return true;
	            default:
	                return false;
	        }
	    }

	    // Called when the user exits the action mode
	    @Override
	    public void onDestroyActionMode(ActionMode mode) {
	        mActionMode = null;
	    }
	};
	
	 void removeColumn(final TextView tw) {
		if (ContentProvider.INSTANCE.getCurrentWorkout().getFitnessExercises().size() < 2) {
			Toast.makeText(this.getApplicationContext(), this.getString(R.string.need_more_than_1), Toast.LENGTH_LONG).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(this.getString(R.string.really_delete)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int column = columnNumberMap.get(tw);
				ContentProvider.INSTANCE.getCurrentWorkout().removeFitnessExercise(ContentProvider.INSTANCE.getCurrentWorkout().getFitnessExercises().get(column - 1));

				// after removing a column, the map with columns should be
				// updated
				Set<TextView> tws = new HashSet<TextView>(columnNumberMap.keySet());
				for (TextView tw : tws) {
					int c = columnNumberMap.get(tw);
					if (c == column){
						columnNumberMap.remove(tw);
						exerciseMap.remove(tw);
					}
					if (c > column)
						columnNumberMap.put(tw, c - 1);
				}
				updateTable();
				Toast.makeText(getApplicationContext(), getString(R.string.exercise_was_removed), Toast.LENGTH_LONG).show();

			}
		}).setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(getApplicationContext(), getString(R.string.exerciser_wont_be_removed), Toast.LENGTH_LONG).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}
