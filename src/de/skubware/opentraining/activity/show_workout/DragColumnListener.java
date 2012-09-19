package de.skubware.opentraining.activity.show_workout;

import android.util.Log;
import android.view.DragEvent;
import android.view.View;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.datamanagement.DataManager;

/** Tiny class for a listener that handles drag and drop */
class DragColumnListener implements View.OnDragListener {
	
	/** Reference to original activity */
	private final ShowWorkoutActivity showWorkoutActivity;

	/** */
	boolean containsDragable = false;


	/**
	 * Constructor.
	 * 
	 * @param showWorkoutActivity a reference to the original activity
	 */
	DragColumnListener(ShowWorkoutActivity showWorkoutActivity) {
		this.showWorkoutActivity = showWorkoutActivity;
	}


	@Override
	public boolean onDrag(final View view, final DragEvent dragEvent) {
		int dragAction = dragEvent.getAction();
		final View dragView = (View) dragEvent.getLocalState();
		if (dragAction == DragEvent.ACTION_DRAG_EXITED) {
			containsDragable = false;
		} else if (dragAction == DragEvent.ACTION_DRAG_ENTERED) {
			containsDragable = true;
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
		} else if (dragAction == DragEvent.ACTION_DROP && containsDragable) {
			Log.d(ShowWorkoutActivity.TAG, "Action drop endend and contains dragable, set visible. Start switiching exercises");
			dragView.post(new Runnable() {
				@Override
				public void run() {
					dragView.setVisibility(View.VISIBLE);
					Workout current = DataManager.INSTANCE.getCurrentWorkout();
					current.switchExercises(DragColumnListener.this.showWorkoutActivity.exerciseMap.get(view), DragColumnListener.this.showWorkoutActivity.exerciseMap.get(dragView));
					DragColumnListener.this.showWorkoutActivity.updateTable();
				}
			});
		} 
		return true;
	}

	private boolean dropEventNotHandled(DragEvent dragEvent) {
		return !dragEvent.getResult();
	}

	

}