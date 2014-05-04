/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
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

package de.skubware.opentraining.activity.create_exercise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_exercise.ExerciseImageListAdapter.ImageData;
import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.basic.License.LicenseType;

public class EditImageMetadataDialog extends AlertDialog{

	/** Reference to original activity. */
	private Activity mActivity;
	


	public EditImageMetadataDialog(final Activity activity, final int position, final ExerciseImageListAdapter listAdapter) {
		super(activity);
		mActivity = activity;

		// set title and message
		this.setTitle(mActivity.getString(R.string.edit_image_metadata));

		// CheckBox
		LayoutInflater inflater = this.getLayoutInflater();


		View layout = inflater.inflate(R.layout.dialog_image, null);
		setView(layout);
		
		// get views with the important content
		final EditText edit_text_author = (EditText) layout.findViewById(R.id.edit_text_author);
		final Spinner license_spinner = (Spinner) layout.findViewById(R.id.license_spinner);
		final Spinner position_spinner = (Spinner) layout.findViewById(R.id.position_spinner);

		
		// get image data
		final ImageData imageData = (ImageData) listAdapter.getItem(position);
		
		// positive button
		this.setButton(BUTTON_POSITIVE, mActivity.getString(R.string.save), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// save changed data
				String author = edit_text_author.getText().toString();
				LicenseType licenseType = (LicenseType) license_spinner.getSelectedItem();
						
				imageData.imageLicense = new License(licenseType, author);
				
				listAdapter.moveItem(position, position_spinner.getSelectedItemPosition());
				
				dialog.dismiss();
			}
		});
			
		// neutral button
		this.setButton(BUTTON_NEUTRAL, mActivity.getString(R.string.delete_image), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// remove image
				listAdapter.remove(position);
				dialog.dismiss();
			}
		});
			
		// negative button
		this.setButton(BUTTON_NEGATIVE, activity.getString(R.string.cancel), new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// just ignore the changed data
				dialog.dismiss();
			}
		});
		
		

		// set author
		edit_text_author.setText(imageData.imageLicense.getAuthor());
		
		// set license
        ArrayAdapter<LicenseType> spinnerAdapter = new ArrayAdapter<LicenseType>(mActivity, android.R.layout.simple_spinner_item,LicenseType.values());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		license_spinner.setAdapter(spinnerAdapter);
		
		int licensePosition = 0;
		for(LicenseType license:License.LicenseType.values()){
			if(license.equals(imageData.imageLicense.getLicenseType())){
				license_spinner.setSelection(licensePosition);
				continue;
			}
			licensePosition++;
		}
		
		// set position
		Integer[] positions = new Integer[listAdapter.getCount()];
		for(int i = 0; i < listAdapter.getCount();i++){
			positions[i] = i+1;
		}
		
        ArrayAdapter<Integer> positionSpinnerAdapter = new ArrayAdapter<Integer>(mActivity, android.R.layout.simple_spinner_item, positions);
        positionSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		position_spinner.setAdapter(positionSpinnerAdapter);
		position_spinner.setSelection(position);
		
		// show the dialog
		show();

		}


	
}
