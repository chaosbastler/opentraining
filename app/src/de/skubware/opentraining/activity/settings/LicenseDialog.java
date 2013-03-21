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

package de.skubware.opentraining.activity.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import de.skubware.opentraining.R;

/**
 * A short dialog that displays the licenses.
 * 
 */
public class LicenseDialog extends AlertDialog {


	/**
	 * Constructor. The Dialog will be shown
	 * automatically, there is no need to use {@link #show()}.
	 */
	public LicenseDialog(final Activity activity) {
		super(activity);


		LayoutInflater inflater = this.getLayoutInflater();
		View rootView = inflater.inflate(R.layout.dialog_license, null);
		this.setView(rootView);

		// create linkified message for text_view_open_training_license
		SpannableString ot = new SpannableString(activity.getString(R.string.disclaimer) + "\n\n http://www.gnu.org/licenses/gpl-3.0.html");
		Linkify.addLinks(ot, Linkify.ALL);

		TextView text_view_open_training_license = (TextView) rootView.findViewById(R.id.text_view_open_training_license);
		text_view_open_training_license.setText(ot);

		// create linkified message for text_view_actionbarsherlock
		SpannableString abs = new SpannableString(activity.getString(R.string.license_text_action_bar_sherlock));
		Linkify.addLinks(abs, Linkify.ALL);

		TextView text_view_actionbarsherlock = (TextView) rootView.findViewById(R.id.text_view_actionbarsherlock);
		text_view_actionbarsherlock.setText(abs);

		// positive button
		this.setButton(BUTTON_POSITIVE, activity.getString(android.R.string.ok), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		// show the dialog
		this.show();

		// make textviews clickable, must be called after show()
		((TextView) this.findViewById(R.id.text_view_open_training_license)).setMovementMethod(LinkMovementMethod.getInstance());
		((TextView) this.findViewById(R.id.text_view_actionbarsherlock)).setMovementMethod(LinkMovementMethod.getInstance());

	}

}
