package de.skubware.opentraining;

import com.actionbarsherlock.app.SherlockFragment;

import de.skubware.opentraining.basic.Workout;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A fragment representing a single Workout detail screen. This fragment is
 * either contained in a {@link WorkoutListActivity} in two-pane mode (on
 * tablets) or a {@link WorkoutDetailActivity} on handsets.
 */
public class WorkoutDetailFragment extends SherlockFragment {
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_WORKOUT = "workout";

	/**
	 * The {@link Workout} this fragment is presenting.
	 */
	private Workout mWorkout;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public WorkoutDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_WORKOUT)) {
			// Load the dummy content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mWorkout = (Workout) getArguments().getSerializable(ARG_WORKOUT);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_workout_detail, container, false);

		// Show the dummy content as text in a TextView.
		if (mWorkout != null) {
			((TextView) rootView.findViewById(R.id.workout_detail)).setText(mWorkout.getName());
		}

		return rootView;
	}
}
