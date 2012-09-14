package de.skubware.opentraining;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class ExerciseDetailFragment extends Fragment {

    public static final String ARG_ITEM_ID = "item_id";

    ExerciseType exercise;

    public ExerciseDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments().containsKey(ARG_ITEM_ID)) {
        	exercise = ExerciseType.getByName(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_exercise_detail, container, false);
        if (exercise != null) {
    		// Images
    		ImageView imageview = (ImageView) (ImageView) rootView.findViewById(R.id.imageview);
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


            //((TextView) rootView.findViewById(R.id.exercise_detail)).setText(exercise.getName());
        }
        return rootView;
    }
}
