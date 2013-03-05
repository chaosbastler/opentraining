/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Christian Skubich
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

import java.util.Calendar;
import java.util.List;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_workout.ExerciseTypeListActivity;
import de.skubware.opentraining.activity.manage_workouts.WorkoutListActivity;
import de.skubware.opentraining.activity.start_training.FExListActivity;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.Cache;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends SherlockFragmentActivity {
	/** Tag for logging */
	public static final String TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setUpList();
		
		
		// load data/parse .xml files in background
		final Context mContext = this;
				new Thread() {
			@Override
			public void run() {
					Cache.INSTANCE.updateCache(mContext);		
			}
		}.start();

		
		
		// show disclaimer
		SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
		Boolean showDisclaimer = settings.getBoolean(DisclaimerDialog.PREFERENCE_SHOW_DISCLAIMER, true);
		if(showDisclaimer){
			new DisclaimerDialog(this);
		}
		
	}
	

	/**
	 * Configures the ListView for this activity.
	 */
	private void setUpList() {
		ListView listview = (ListView) this.findViewById(R.id.activity_main_listview);

		String[] values = new String[] { getString(R.string.create_workout), getString(R.string.manage_workouts), getString(R.string.start_training) };

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, values);

		listview.setAdapter(adapter);

		listview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(MainActivity.this, ExerciseTypeListActivity.class));
					break;
				case 1:
					startActivity(new Intent(MainActivity.this, WorkoutListActivity.class));
					break;
				case 2:
					showSelectWorkoutDialog();
					break;
				default:
					Log.wtf(TAG, "This item should not exist.");
				}
			}
		});
	}
	
	/** Shows a dialog for choosing a {@link Workout} */
	private void showSelectWorkoutDialog() {

		AlertDialog.Builder builder_workoutchooser = new AlertDialog.Builder(MainActivity.this);
		builder_workoutchooser.setTitle(getString(R.string.choose_workout));

		IDataProvider dataProvider = new DataProvider(this);

		// get all Workouts and add them to the adapter
		final List<Workout> workoutList = dataProvider.getWorkouts();
		final ArrayAdapter<Workout> adapter = new ArrayAdapter<Workout>(MainActivity.this, android.R.layout.select_dialog_singlechoice,
				workoutList);

		builder_workoutchooser.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				// close dialog and show TrainingEntryDialog
				dialog.dismiss();
				Workout mWorkout = adapter.getItem(item);

				// DialogFragment.show() will take care of adding the fragment
				// in a transaction. We also want to remove any currently
				// showing
				// dialog, so make our own transaction and take care of that
				// here.
				/*FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
				Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
				if (prev != null) {
					ft.remove(prev);
				}
				ft.addToBackStack(null);

				// Create and show the dialog.
				DialogFragment newFragment = DialogStartTraining.newInstance(mWorkout);
				newFragment.show(ft, "dialog");*/
				

				// add arguments to intent
				Intent intent = new Intent(MainActivity.this, FExListActivity.class);
				mWorkout.addTrainingEntry(Calendar.getInstance().getTime());
				intent.putExtra(FExListActivity.ARG_WORKOUT, mWorkout);
				// start activity
				MainActivity.this.startActivity(intent);

			}

		});

		Log.d(TAG, "Number of Workouts: " + workoutList.size());
		switch (workoutList.size()) {
		// show error message, if there is no Workout
		case 0:
			Toast.makeText(MainActivity.this, "No workout found.", Toast.LENGTH_LONG).show();
			break;
		// no need to choose Workout if there is only one
		/*case 1:
		 	//TODO implement
			this.showTrainingEntryDialog(workoutList.get(0));
			break;*/
		// choose Workout, if there is more than one Workout
		default:
			builder_workoutchooser.create().show();
		}

	}
	


}
