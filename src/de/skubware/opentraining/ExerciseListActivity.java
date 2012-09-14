package de.skubware.opentraining;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;

public class ExerciseListActivity extends FragmentActivity
        implements ExerciseListFragment.Callbacks {

    private boolean mTwoPane;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_list);

        if (findViewById(R.id.exercise_detail_container) != null) {
            mTwoPane = true;
            ((ExerciseListFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.exercise_list))
                    .setActivateOnItemClick(true);
        }
    }

    @Override
    public void onItemSelected(String id) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putString(ExerciseDetailFragment.ARG_ITEM_ID, id);
            ExerciseDetailFragment fragment = new ExerciseDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.exercise_detail_container, fragment)
                    .commit();

        } else {
            Intent detailIntent = new Intent(this, ExerciseDetailActivity.class);
            detailIntent.putExtra(ExerciseDetailFragment.ARG_ITEM_ID, id);
            startActivity(detailIntent);
        }
    }
}
