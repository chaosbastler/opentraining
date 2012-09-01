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

package de.skubware.opentraining.activity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.*;
import de.skubware.opentraining.datamanagement.DataManager.CSSFile;

import de.skubware.training_app.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This activity shows the plan(table) with the selected exercises. The user can
 * select the number of empty rows and change the name of the plan.
 * and finally export it.
 * 
 * 
 * @author Christian Skubich
 * 
 */
public class EditWorkoutActivity extends Activity {
	/** Tag for logging */
	private static final String TAG = "EditWorkoutActivity";
	
	/** Contains the current column number of each TextView */
	private Map<TextView, Integer> columnNumberMap = new HashMap<TextView, Integer>();

	/** Contains the exercise that belongs to a textview */
	private Map<TextView, FitnessExercise> exerciseMap = new HashMap<TextView, FitnessExercise>();

	/** number of rows, can be changed, must be positive */
	private int emptyRowCount;

	// some attributes for the style/design of the table
	private final static int COLUMN_PADDING = 5;
	private final static int ROW_PADDING = 5;
	
	private final static int ROW_HEIGHT = 90;
	private final static int COLUMN_WIDTH = 200;
	private final static int DATE_COLUMN_WIDTH = 100;

	/**
	 * Configures the menu actions.
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.edit_workout_activity_menu, menu);

		// configure menu_item_save_plan
		final MenuItem menu_item_save_plan = (MenuItem) menu.findItem(R.id.menu_item_save_plan);
		menu_item_save_plan.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				boolean success = DataManager.INSTANCE.savePlan();
				AlertDialog.Builder builder = new AlertDialog.Builder(EditWorkoutActivity.this);
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
			public boolean onMenuItemClick(MenuItem item) {
				final CharSequence[] items = CSSFile.items;

				AlertDialog.Builder builder = new AlertDialog.Builder(EditWorkoutActivity.this);
				builder.setTitle(getString(R.string.choose_design));
				builder.setItems(items, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						String css = CSSFile.items[item].toString();
						CSSFile cssFile = CSSFile.valueOf(CSSFile.class, css);

						DataManager.INSTANCE.setCSSFile(cssFile);
						startActivity(new Intent(EditWorkoutActivity.this, ShowTPActivity.class));
					}
				});
				AlertDialog dialog = builder.create();
				dialog.show();
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

		// init variables
		this.emptyRowCount = DataManager.INSTANCE.getCurrentWorkout().getEmptyRows();

		setContentView(R.layout.edit_workout);

		// workout name
		EditText edittext_name = (EditText) findViewById(R.id.edittext_workout_name);
		edittext_name.setText(DataManager.INSTANCE.getCurrentWorkout().getName());

		// button + row
		Button btn_add_row = (Button) findViewById(R.id.btn_add_row);
		btn_add_row.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				addRow();
			}
		});

		// button - row
		Button btn_remove_row = (Button) findViewById(R.id.btn_remove_row);
		btn_remove_row.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				removeRow();
			}
		});

		// finally show the current workout
		this.updateTable();

	}

	/** Increases the number of rows. */
	private void addRow() {
		this.emptyRowCount++;
		DataManager.INSTANCE.getCurrentWorkout().setEmptyRows(emptyRowCount);

		this.updateTable();
	}

	/** Decreases the number of rows (if >1). */
	private void removeRow() {
		if (this.emptyRowCount > 1) {
			this.emptyRowCount--;
			DataManager.INSTANCE.getCurrentWorkout().setEmptyRows(emptyRowCount);
		}
		this.updateTable();
	}

	/**
	 * Updates the workout table.
	 */
	private void updateTable() {
		// workout name
		EditText edittext_name = (EditText) findViewById(R.id.edittext_workout_name);
		String new_name = edittext_name.getText().toString();
		if (new_name != null && !new_name.isEmpty()) {
			Workout newWorkout = new Workout(new_name, DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises());
			DataManager.INSTANCE.setWorkout(newWorkout);
		}

		TableLayout table = (TableLayout) findViewById(R.id.table);
		table.removeAllViews();

		this.buildFirstRow();
		this.buildEmptyRows();
	}

	public TextView getStyledTextView(String text) {
		TextView tw = new TextView(this);
		tw.setTextColor(Color.BLACK);
		tw.setText(text);
		tw.setTypeface(null, Typeface.BOLD);
		tw.setTextSize(22);
		tw.setPadding(15, 15, 15, 15);
		tw.setGravity(Gravity.CENTER_HORIZONTAL);
		tw.setHeight(ROW_HEIGHT);
		tw.setWidth(COLUMN_WIDTH);


		tw.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_white));

		return tw;

	}

	private void addColumPadding(TableRow row, int paddingwidht) {
		TextView tw = new TextView(this);
		tw.setHeight(ROW_HEIGHT);
		tw.setWidth(paddingwidht);

		row.addView(tw);
	}

	private void buildFirstRow() {
		TableLayout table = (TableLayout) findViewById(R.id.table);

		TableRow firstrow = new TableRow(this);
		firstrow.setPadding(0, ROW_PADDING, ROW_PADDING, 0);

		// Date
		TextView date = this.getStyledTextView(getString(R.string.date));
		date.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_yellow));
		date.setWidth(DATE_COLUMN_WIDTH);

		firstrow.addView(date);

		// for space between columns empty tw
		this.addColumPadding(firstrow, COLUMN_PADDING);

		int i = 1;
		for (FitnessExercise fEx : DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises()) {
			TextView tw = this.getStyledTextView(fEx.toString());
			tw.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.btn_yellow));

			firstrow.addView(tw);

			// add touch action, anonymous class did not work (because the
			// argument is needed)
			tw.setOnLongClickListener(new ColumnListener(tw));
			tw.setOnTouchListener(new TouchColumnListener());
			tw.setOnDragListener(new DragColumnListener());
			this.columnNumberMap.put(tw, i);
			this.exerciseMap.put(tw, fEx);

			// for space between colums empty tw
			this.addColumPadding(firstrow, COLUMN_PADDING);

			i++;
		}

		table.addView(firstrow);

	}
	
	
	
	/** Tiny class for a listener that handles on touch. */
	class TouchColumnListener implements View.OnTouchListener{
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

				ClipData clipData = ClipData.newPlainText("", "");

				View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);

				view.startDrag(clipData, dsb, view, 0);

				view.setVisibility(View.INVISIBLE);
				
				return true;
			} else {
				return false;
			}
		}
	}
	
	
	/** Tiny class for a listener that handles drag and drop */
	class DragColumnListener implements View.OnDragListener{
        boolean containsDragable = false;

		@Override
		    public boolean onDrag(final View view, final DragEvent dragEvent) {
		        int dragAction = dragEvent.getAction();
		        final View dragView = (View) dragEvent.getLocalState();
		        if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
		            containsDragable = false;
					Log.d(TAG, "Action drag exited, containsDragable now false");
		        } else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
		            containsDragable = true;
					Log.d(TAG, "Action drag entered, containsDragable now true");
		        } else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
		            if (dropEventNotHandled(dragEvent)) {
		        	dragView.post(new Runnable(){
						@Override
						public void run() {
			        		dragView.setVisibility(View.VISIBLE);
						}
		        	    });
						Log.d(TAG, "Action drag ended, set visible");
		            }
		        } else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
					Log.d(TAG, "Action drop endend and contains dragable, set visible");
		        	dragView.post(new Runnable(){
						@Override
						public void run() {
			        		dragView.setVisibility(View.VISIBLE);
							Workout current = DataManager.INSTANCE.getCurrentWorkout();
							current.switchExercises(exerciseMap.get(view), exerciseMap.get(dragView));
							updateTable();
						}
		        	    });		        
		        	}
		        return true;
		    }
		 
		    private boolean dropEventNotHandled(DragEvent dragEvent) {
		        return !dragEvent.getResult();
		    }

	}

	/** Tiny class for a listener that creates a dialog. */
	class ColumnListener implements View.OnLongClickListener {
		private TextView tw;

		public ColumnListener(TextView tw) {
			this.tw = tw;
		}


		public boolean onLongClick(View arg0) {
			if (DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().size() < 2) {
				Toast.makeText(getApplicationContext(), getString(R.string.need_more_than_1), Toast.LENGTH_LONG).show();
				return true;
			}

			AlertDialog.Builder builder = new AlertDialog.Builder(EditWorkoutActivity.this);
			builder.setMessage(getString(R.string.really_delete)).setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					int column = columnNumberMap.get(tw);
					DataManager.INSTANCE.getCurrentWorkout().removeFitnessExercise(DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().get(column - 1));

					// after removing a column, the map with columns should be
					// updated
					Set<TextView> tws = new HashSet<TextView>(columnNumberMap.keySet());
					for (TextView tw : tws) {
						int c = columnNumberMap.get(tw);
						if (c == column)
							columnNumberMap.remove(tw);
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
			return true;
		}
	}

	private void buildEmptyRows() {
		TableLayout table = (TableLayout) findViewById(R.id.table);

		for (int i = 0; i < this.emptyRowCount; i++) {
			TableRow row = new TableRow(this);
			row.setPadding(0, ROW_PADDING, ROW_PADDING, 0);

			for (int k = 0; k <= DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().size(); k++) {
				TextView emptyTW = this.getStyledTextView("");
				if(k==0)
					emptyTW.setWidth(DATE_COLUMN_WIDTH);
				// for performance reasons one could cache the ColumnListeners
				// in a Map
				emptyTW.setOnLongClickListener(new ColumnListener(emptyTW));
				this.columnNumberMap.put(emptyTW, k);

				row.addView(emptyTW);
				this.addColumPadding(row, COLUMN_PADDING);
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
		startActivity(new Intent(EditWorkoutActivity.this, SelectExercisesActivity.class));
		return;
	}

}
