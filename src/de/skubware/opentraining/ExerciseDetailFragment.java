package de.skubware.opentraining;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.skubware.opentraining.activity.CreateExerciseActivity;
import de.skubware.opentraining.activity.EditWorkoutActivity;
import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;

public class ExerciseDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	/** Currently selected exercise */
	ExerciseType exercise;



	private GestureDetector gestureScanner;

	
	public ExerciseDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		this.setHasOptionsMenu(true);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			exercise = ExerciseType.getByName(getArguments().getString(ARG_ITEM_ID));
		}
	}
	


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
		ImageView imageview = (ImageView) (ImageView) rootView.findViewById(R.id.imageview);
		
		this.gestureScanner = new GestureDetector(this.getActivity(), new ExerciseDetailOnGestureListener(this, imageview));
		
		if (exercise != null) {
			// Images
			if (!exercise.getImagePaths().isEmpty()) {
				imageview.setImageDrawable(DataManager.INSTANCE.getDrawable(exercise.getImagePaths().get(0).toString()));
			} else {
				imageview.setImageResource(R.drawable.defaultimage);
			}

			// Image license
			TextView image_license = (TextView) rootView.findViewById(R.id.textview_image_license);
			if (exercise.getImageLicenseMap().values().iterator().hasNext()) {
				image_license.setText(exercise.getImageLicenseMap().values().iterator().next());
			} else {
				image_license.setText("Keine Lizenzinformationen vorhanden");
			}
			
			rootView.setOnTouchListener(new View.OnTouchListener() {
	            @Override
	            public boolean onTouch(View v, MotionEvent event) {
	                return gestureScanner.onTouchEvent(event);
	            }
	        });

		}
		
		return rootView;

	}
	
	
    @Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.exercise_detail_menu, menu);
		
		
		// configure menu_item_add_exercise
		MenuItem menu_item_add_exercise = (MenuItem) menu.findItem(R.id.menu_item_add_exercise);
		menu_item_add_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (exercise == null) {

					Toast.makeText(ExerciseDetailFragment.this.getActivity(), getString(R.string.no_exercises_choosen), Toast.LENGTH_LONG).show();
					return true;
				}

				Workout w = DataManager.INSTANCE.getCurrentWorkout();
				if (w == null) {
					w = new Workout("My Plan", new FitnessExercise(exercise));
					DataManager.INSTANCE.setWorkout(w);
				} else {
					w.addFitnessExercise(new FitnessExercise(exercise));
				}

				CharSequence text = getString(R.string.exercise) + " " + exercise.getName() + " " + getString(R.string.has_been_added);
				int duration = Toast.LENGTH_LONG;
				Toast toast = Toast.makeText(ExerciseDetailFragment.this.getActivity(), text, duration);
				toast.show();

				return true;
			}
		});
		
		
		
    }	
	

}
