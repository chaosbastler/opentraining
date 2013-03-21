/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
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

package de.skubware.opentraining.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import de.skubware.opentraining.R;

/**
 * A short dialog that displays the disclaimer message.
 * 
 * @author Christian Skubich
 * 
 */
public class DisclaimerDialog extends AlertDialog {

	/** Static string for preferences. */
	public static final String PREFERENCE_SHOW_DISCLAIMER = "show_disclaimer";

	/** Reference to original activity. */
	private Activity mActivity;

	/**
	 * Constructor. The Dialog has two buttons, if the user does not accept the
	 * terms of usage, the app will be closed. The Dialog will be shown
	 * automatically, there is no need to use {@link #show()}.
	 */
	public DisclaimerDialog(final Activity activity) {
		super(activity);
		this.mActivity = activity;

		// create linkified message
		SpannableString s = new SpannableString(activity.getString(R.string.disclaimer) + "http://www.gnu.org/licenses/gpl-3.0.html");
		Linkify.addLinks(s, Linkify.ALL);

		// set title and message
		this.setTitle(activity.getString(R.string.license));
		this.setMessage(s);

		// CheckBox
		LayoutInflater inflater = this.getLayoutInflater();

		View wrapper = inflater.inflate(R.layout.dialog_disclaimer_checkbox, null);
		final CheckBox checkbox_dont_show_again = (CheckBox) wrapper.findViewById(R.id.checkbox_dont_show_again);
		this.setView(wrapper);

		// positive button
		this.setButton(BUTTON_POSITIVE, activity.getString(R.string.accept), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (checkbox_dont_show_again.isChecked()) {
					SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mActivity);
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean(DisclaimerDialog.PREFERENCE_SHOW_DISCLAIMER, false);
					editor.commit();
				}

				dialog.dismiss();
			}
		});

		// negative button
		this.setButton(BUTTON_NEGATIVE, activity.getString(R.string.not_accept), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				activity.finish();
			}
		});

		// show the dialog
		this.show();

		// make textview clickable, must be called after show()
		((TextView) this.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			mActivity.finish();
			return true;
		}

		return false;
	}

}
