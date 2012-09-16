package de.skubware.opentraining.activity.show_workout;

import java.util.HashSet;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.datamanagement.DataManager;

/** Tiny class for a listener that handles drag and drop */
class DragColumnListener implements View.OnDragListener {
	/**
	 * 
	 */
	private final ShowWorkoutActivity showWorkoutActivity;

	/**
	 * @param showWorkoutActivity
	 */
	DragColumnListener(ShowWorkoutActivity showWorkoutActivity) {
		this.showWorkoutActivity = showWorkoutActivity;
	}

	boolean containsDragable = false;
	boolean overWasteBasket = false;

	@Override
	public boolean onDrag(final View view, final DragEvent dragEvent) {
		int dragAction = dragEvent.getAction();
		final View dragView = (View) dragEvent.getLocalState();
		if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
			containsDragable = false;
			overWasteBasket = false;
			Log.d(ShowWorkoutActivity.TAG, "Action drag exited, containsDragable now false");
		} else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
			containsDragable = true;
			if (view.getClass().equals(ImageView.class))
				overWasteBasket = true;
			Log.d(ShowWorkoutActivity.TAG, "Action drag entered, containsDragable now true");
		} else if (dragAction == DragEvent.ACTION_DRAG_ENDED) {
			if (dropEventNotHandled(dragEvent)) {
				dragView.post(new Runnable() {
					@Override
					public void run() {
						dragView.setVisibility(View.VISIBLE);
					}
				});
				Log.d(ShowWorkoutActivity.TAG, "Action drag ended, set visible");
			}
		} else if (dragAction == DragEvent.ACTION_DROP && containsDragable && !overWasteBasket) {
			Log.d(ShowWorkoutActivity.TAG, "Action drop endend and contains dragable, set visible");
			dragView.post(new Runnable() {
				@Override
				public void run() {
					dragView.setVisibility(View.VISIBLE);
					Workout current = DataManager.INSTANCE.getCurrentWorkout();
					current.switchExercises(DragColumnListener.this.showWorkoutActivity.exerciseMap.get(view), DragColumnListener.this.showWorkoutActivity.exerciseMap.get(dragView));
					DragColumnListener.this.showWorkoutActivity.updateTable();
				}
			});
		} else if (dragAction == DragEvent.ACTION_DROP && containsDragable && overWasteBasket) {
			Log.i(ShowWorkoutActivity.TAG, "Drag and drop over WASTE BASKET");
			dragView.post(new Runnable() {
				@Override
				public void run() {
					dragView.setVisibility(View.VISIBLE);
					removeColumn((TextView) dragView);
				}
			});
		}
		return true;
	}

	private boolean dropEventNotHandled(DragEvent dragEvent) {
		return !dragEvent.getResult();
	}

	private void removeColumn(final TextView tw) {
		if (DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().size() < 2) {
			Toast.makeText(this.showWorkoutActivity.getApplicationContext(), this.showWorkoutActivity.getString(R.string.need_more_than_1), Toast.LENGTH_LONG).show();
			return;
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(this.showWorkoutActivity);
		builder.setMessage(this.showWorkoutActivity.getString(R.string.really_delete)).setPositiveButton(this.showWorkoutActivity.getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				int column = DragColumnListener.this.showWorkoutActivity.columnNumberMap.get(tw);
				DataManager.INSTANCE.getCurrentWorkout().removeFitnessExercise(DataManager.INSTANCE.getCurrentWorkout().getFitnessExercises().get(column - 1));

				// after removing a column, the map with columns should be
				// updated
				Set<TextView> tws = new HashSet<TextView>(DragColumnListener.this.showWorkoutActivity.columnNumberMap.keySet());
				for (TextView tw : tws) {
					int c = DragColumnListener.this.showWorkoutActivity.columnNumberMap.get(tw);
					if (c == column){
						DragColumnListener.this.showWorkoutActivity.columnNumberMap.remove(tw);
						DragColumnListener.this.showWorkoutActivity.exerciseMap.remove(tw);
					}
					if (c > column)
						DragColumnListener.this.showWorkoutActivity.columnNumberMap.put(tw, c - 1);
				}
				DragColumnListener.this.showWorkoutActivity.updateTable();
				Toast.makeText(DragColumnListener.this.showWorkoutActivity.getApplicationContext(), DragColumnListener.this.showWorkoutActivity.getString(R.string.exercise_was_removed), Toast.LENGTH_LONG).show();

			}
		}).setNegativeButton(this.showWorkoutActivity.getString(R.string.no), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(DragColumnListener.this.showWorkoutActivity.getApplicationContext(), DragColumnListener.this.showWorkoutActivity.getString(R.string.exerciser_wont_be_removed), Toast.LENGTH_LONG).show();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

}