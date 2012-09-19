package de.skubware.opentraining.activity.show_workout;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

/** Tiny class for a listener that handles on touch. */
class TouchColumnListener implements View.OnTouchListener {
	
	/**
	 * 
	 */
	private final ShowWorkoutActivity showWorkoutActivity;

	/**
	 * @param showWorkoutActivity
	 */
	TouchColumnListener(ShowWorkoutActivity showWorkoutActivity) {
		this.showWorkoutActivity = showWorkoutActivity;
	}
	

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

			ClipData clipData = ClipData.newPlainText("", "");

			View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);

			view.startDrag(clipData, dsb, view, 0);

			view.setVisibility(View.INVISIBLE);
			
			
			if (showWorkoutActivity.mActionMode != null) {
				return false;
			}
			
			// Start the CAB using the ActionMode.Callback defined above
			showWorkoutActivity.mActionMode = showWorkoutActivity.startActionMode(showWorkoutActivity.mActionModeCallback);			
			showWorkoutActivity.lastTouched = (TextView) view;
			
			return true;
		} else {
			return false;
		}
	}
}