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

package de.skubware.opentraining.activity.start_training;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_workout.ExerciseTypeListActivity;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * An activity representing a single Exercise detail screen. This activity is
 * only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a {@link FExListActivity}
 * .
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link FExDetailFragment}.
 */
public class FExDetailActivity extends SherlockFragmentActivity implements DialogFragmentAddEntry.Callbacks{
	
	private Workout mWorkout;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fex_detail);

		// Don't show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

		
		mWorkout = (Workout) getIntent().getSerializableExtra(FExDetailFragment.ARG_WORKOUT);

		
		// savedInstanceState is non-null when there is fragment state
		// saved from previous configurations of this activity
		// (e.g. when rotating the screen from portrait to landscape).
		// In this case, the fragment will automatically be re-added
		// to its container so we don't need to manually add it.
		// For more information, see the Fragments API guide at:
		//
		// http://developer.android.com/guide/components/fragments.html
		//
		if (savedInstanceState == null) {
			// Create the detail fragment and add it to the activity
			// using a fragment transaction.
			Bundle arguments = new Bundle();
			arguments.putSerializable(FExDetailFragment.ARG_FEX, getIntent().getSerializableExtra(FExDetailFragment.ARG_FEX));
			arguments.putSerializable(FExDetailFragment.ARG_WORKOUT, getIntent().getSerializableExtra(FExDetailFragment.ARG_WORKOUT));
			FExDetailFragment fragment = new FExDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.exercise_detail_container, fragment).commit();
		}
	}

	@Override
	public void onEntryEdited(FitnessExercise fitnessExercise) {		
		Intent i = new Intent();
		mWorkout.updateFitnessExercise(fitnessExercise);
		i.putExtra(FExListActivity.ARG_WORKOUT, mWorkout);
		this.setResult(Activity.RESULT_OK, i);		
	}

	/*@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			NavUtils.navigateUpTo(this, new Intent(this, FExListActivity.class));
			return true;
		}
		return super.onOptionsItemSelected(item);
	}*/
}
