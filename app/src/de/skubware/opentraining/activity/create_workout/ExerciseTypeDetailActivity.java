/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
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

package de.skubware.opentraining.activity.create_workout;

import de.skubware.opentraining.R;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * An activity representing a single ExerciseType detail screen. This activity
 * is only used on handset devices. On tablet-size devices, item details are
 * presented side-by-side with a list of items in a
 * {@link ExerciseTypeListActivity}.
 * <p>
 * This activity is mostly just a 'shell' activity containing nothing more than
 * a {@link ExerciseTypeDetailFragment}.
 */
public class ExerciseTypeDetailActivity extends ActionBarActivity {
	/** Tag for logging */
	public static final String TAG = ExerciseTypeDetailActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_exercisetype_detail);

		// Show the Up button in the action bar.
		getSupportActionBar().setDisplayHomeAsUpEnabled(false);

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
			arguments.putSerializable(ExerciseTypeDetailFragment.ARG_EXERCISE,
					getIntent().getSerializableExtra(ExerciseTypeDetailFragment.ARG_EXERCISE));
			arguments.putSerializable(ExerciseTypeDetailFragment.ARG_WORKOUT,
					getIntent().getSerializableExtra(ExerciseTypeDetailFragment.ARG_WORKOUT));

			ExerciseTypeDetailFragment fragment = new ExerciseTypeDetailFragment();
			fragment.setArguments(arguments);
			getSupportFragmentManager().beginTransaction().add(R.id.exercisetype_detail_container, fragment).commit();
		}

	}

	/*
	 * @Override public boolean onOptionsItemSelected(MenuItem item) { switch
	 * (item.getItemId()) { case android.R.id.home: NavUtils.navigateUpTo(this,
	 * new Intent(this, ExerciseTypeListActivity.class)); return true; } return
	 * super.onOptionsItemSelected(item); }
	 */

}
