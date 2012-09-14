package de.skubware.opentraining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import de.skubware.opentraining.activity.CreateExerciseActivity;
import de.skubware.opentraining.activity.EditWorkoutActivity;
import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.datamanagement.DataManager;

public class ExerciseDetailFragment extends Fragment {

	public static final String ARG_ITEM_ID = "item_id";

	/** Currently selected exercise */
	private ExerciseType exercise;

	/** Map to store, which muscles should be shown */
	private Map<Muscle, Boolean> muscleMap = new HashMap<Muscle, Boolean>();

	public ExerciseDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		for (Muscle m : Muscle.values()) {
			muscleMap.put(m, true);
		}

		setHasOptionsMenu(true);
		if (getArguments().containsKey(ARG_ITEM_ID)) {
			exercise = ExerciseType.getByName(getArguments().getString(ARG_ITEM_ID));
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
		}
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

		// MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.select_exercises_menu, menu);

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

		// configure menu_item_next
		final MenuItem menu_item_next = (MenuItem) menu.findItem(R.id.menu_item_next);
		menu_item_next.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				if (DataManager.INSTANCE.getCurrentWorkout() == null) {
					AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseDetailFragment.this.getActivity());
					builder.setMessage(getString(R.string.no_exercises_choosen)).setPositiveButton("OK", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
					AlertDialog alert = builder.create();
					alert.show();
					return true;
				} else {

					startActivity(new Intent(ExerciseDetailFragment.this.getActivity(), EditWorkoutActivity.class));
					getActivity().finish();
				}

				return true;
			}

		});

		// configure menu_item_create_new_exercise
		final MenuItem menu_item_create_new_exercise = (MenuItem) menu.findItem(R.id.menu_item_create_new_exercise);
		menu_item_create_new_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent(ExerciseDetailFragment.this.getActivity(), CreateExerciseActivity.class));
				return true;
			}
		});

		MenuItem menu_item_select_muscles = (MenuItem) menu.findItem(R.id.menu_item_select_muscles);
		menu_item_select_muscles.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem menuitem) {
				final CharSequence[] items = new CharSequence[Muscle.values().length];
				final boolean[] states = new boolean[Muscle.values().length];
				int i = 0;
				for (Muscle m : Muscle.values()) {
					items[i] = m.toString();
					states[i] = muscleMap.get(m);
					i++;
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseDetailFragment.this.getActivity());
				builder.setTitle(getString(R.string.select_muscles));
				builder.setMultiChoiceItems(items, states, new DialogInterface.OnMultiChoiceClickListener() {
					public void onClick(DialogInterface dialogInterface, int item, boolean state) {
						muscleMap.put(Muscle.getByName(items[item].toString()), state);

						if (!muscleMap.values().contains(Boolean.TRUE)) {
							Toast.makeText(ExerciseDetailFragment.this.getActivity(), getString(R.string.please_select_muscle), Toast.LENGTH_LONG).show();
						}
					}
				});
				builder.setNeutralButton(getString(R.string.select_all), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						int i = 0;
						for (Muscle m : Muscle.values()) {
							items[i] = m.toString();
							states[i] = true;
							muscleMap.put(m, true);
							i++;
						}
						updateExList();
						dialog.dismiss();
					}
				});
				builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!muscleMap.values().contains(Boolean.TRUE)) {
							muscleMap.put(Muscle.values()[0], true);
							Toast.makeText(ExerciseDetailFragment.this.getActivity(), Muscle.values()[0].toString() + " " + getString(R.string.was_choosen), Toast.LENGTH_LONG)
									.show();

						}
						updateExList();
					}
				});
				builder.setIcon(R.drawable.icon_attention);
				builder.create().show();

				return true;
			}
		});

		super.onCreateOptionsMenu(menu, inflater);

	}

	private void updateExList() {
		ExerciseListFragment list = (ExerciseListFragment) this.getActivity().getSupportFragmentManager().findFragmentById(R.id.exercise_list);

		ArrayList<ExerciseType> exList = new ArrayList<ExerciseType>();
		for (ExerciseType exType : ExerciseType.listExerciseTypes()) {
			boolean shouldAdd = false;
			for (Muscle m : exType.getActivatedMuscles()) {
				if (muscleMap.get(m)) {
					shouldAdd = true;
					break;
				}
			}
			if (shouldAdd || exType.getActivatedMuscles().isEmpty())
				exList.add(exType);
		}

		ListAdapter adapter = new ArrayAdapter<ExerciseType>(getActivity(), android.R.layout.simple_list_item_activated_1, android.R.id.text1, exList);
		list.setListAdapter(adapter);
	}

}
