package de.skubware.opentraining.activity.show_workout;

import android.content.ClipData;
import android.view.MotionEvent;
import android.view.View;

/** Tiny class for a listener that handles on touch. */
class TouchColumnListener implements View.OnTouchListener {
	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

			ClipData clipData = ClipData.newPlainText("", "");

			View.DragShadowBuilder dsb = new View.DragShadowBuilder(view);

			view.startDrag(clipData, dsb, view, 0);

			view.setVisibility(View.INVISIBLE);

			return true;
		} else {
			return false;
		}
	}
}