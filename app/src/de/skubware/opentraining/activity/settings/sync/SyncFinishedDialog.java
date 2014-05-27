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

package de.skubware.opentraining.activity.settings.sync;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A dialog that is shown after the exercises have been downloaded. The user can
 * choose which exercises should be saved.
 * 
 */
public class SyncFinishedDialog extends AlertDialog.Builder {
	/** Tag for logging*/
	private final String TAG = "SyncFinishedDialog";
	
	private Context mContext;
	
	private ArrayList<ExerciseType> mAllExercisesList;
	private ArrayList<ExerciseType> mExerciseToSaveList;
	private TextView mExerciseCountTextView;

	/** Indicates whether only exercises with images should be saved. */
	private boolean withImagesOnly = false;
	/** Indicates whether only exercises with images should be saved. */

	private boolean withDescriptionOnly = false;
	/** Exercises with one of this Locale will be saved. */
	private Set<Locale> localesToSave = new HashSet<Locale>();
	
	public SyncFinishedDialog(final Context context,
			ArrayList<ExerciseType> newExerciseList) {
		super(context);
		mContext = context;
		mAllExercisesList = new ArrayList<ExerciseType>(newExerciseList);
     
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		View v = inflater.inflate(R.layout.dialog_sync_finished, null);
		this.setView(v);

		mExerciseCountTextView = (TextView) v.findViewById(R.id.textview_exercise_count);

		this.setPositiveButton(context.getString(R.string.save),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
						saveExercises();
					}
				});
		
		this.setNegativeButton(context.getString(android.R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.dismiss();
					}
				});
		
		
		// show exercises in ListView
		final ListView exerciseListView = (ListView) v.findViewById(R.id.new_exercise_list);
		Set<Locale> localeSet= new HashSet<Locale>();
		for(ExerciseType ex:mAllExercisesList){
			localeSet.addAll(ex.getTranslationMap().keySet());
		}
		final List<Locale> localeList = new ArrayList<Locale>(localeSet);
		// create String list with the full language-String(e.g. not 'en' but'English')
		final List<String> localeStringList = new ArrayList<String>();
		for(Locale locale:localeList){
			localeStringList.add(locale.getDisplayLanguage(locale));
		}
		
		final ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(
				context, android.R.layout.simple_list_item_multiple_choice,
				localeStringList);
		exerciseListView.setAdapter(listAdapter);
		exerciseListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		
		// update the mExerciseCountTextView when items are (un)checked
		exerciseListView.setOnItemClickListener(new OnItemClickListener(){
			@Override
			public void onItemClick(AdapterView<?> adapter, View view, int wich,
					long id) {
				// get checked Locales
				SparseBooleanArray checkedItemPositions = exerciseListView.getCheckedItemPositions();
				localesToSave = new HashSet<Locale>();
				for(int i = 0; i<localeList.size(); i++){
					// add checked Locales to Set
					if(checkedItemPositions.get(i))
						localesToSave.add(localeList.get(i));
				}
				
				updateExercisesToSave();				
			}
		});
		
		// set listeners for CheckBoxes
		CheckBox checkbox_only_with_description = (CheckBox) v.findViewById(R.id.checkbox_only_with_description);
		CheckBox checkbox_only_with_images = (CheckBox) v.findViewById(R.id.checkbox_only_with_images);
		checkbox_only_with_description.setOnCheckedChangeListener( new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				withDescriptionOnly = isChecked;
				updateExercisesToSave();
			}
		});
		checkbox_only_with_images.setOnCheckedChangeListener( new OnCheckedChangeListener(){
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				withImagesOnly = isChecked;
				updateExercisesToSave();
			}
		});

	}
	
	/**
	 * Updates the list with exercises that should be saved. Also updates the
	 * number of exercises displayed on the GUI.
	 */
	private void updateExercisesToSave() {
		Log.v(TAG, "updateExercisesToSave(); withImagesOnly=" + withImagesOnly + ", withDescriptionOnly=" + withDescriptionOnly);
		mExerciseToSaveList = new ArrayList<ExerciseType>(mAllExercisesList);

		for (ExerciseType exercise : mAllExercisesList) {
			// remove exercises without images
			if (withImagesOnly && exercise.getImagePaths().isEmpty()) {
				mExerciseToSaveList.remove(exercise);
				continue;
			}
			
			if(withDescriptionOnly && (exercise.getDescription()==null || exercise.getDescription().equals(""))){
				mExerciseToSaveList.remove(exercise);
				continue;
			}

			// remove exercises with wrong localization
			boolean keepExercise = false;
			for (Locale localeToSave : localesToSave) {
				for (Locale exerciseLocale : exercise.getTranslationMap()
						.keySet()) {
					// only language needs to be compared
					if (localeToSave.getLanguage().equals(
							exerciseLocale.getLanguage())) {
						keepExercise = true;
					}
				}
			}
			if (!keepExercise) {
				mExerciseToSaveList.remove(exercise);
				continue;
			}
		}

		mExerciseCountTextView.setText(Integer.toString(mExerciseToSaveList
				.size()));
		Log.v(TAG, "There are " + mExerciseToSaveList
				.size() + " exercises that should be saved.");
	}
	
	private void saveExercises(){
		// create ProgessDialog
		final ProgressDialog mProgressDialog = new ProgressDialog(mContext);
		mProgressDialog.setTitle(mContext.getString(R.string.sync_in_progess));
		mProgressDialog.setMessage(mContext
				.getString(R.string.saving_exercises));
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Toast.makeText(mContext,
						mContext.getString(R.string.sync_canceled),
						Toast.LENGTH_LONG).show();
				dialog.dismiss();
			}
		});
		mProgressDialog.show();
		
		// finally save the exercises
		IDataProvider dataProvider = new DataProvider(mContext);
		dataProvider.saveSyncedExercises(mExerciseToSaveList);
		// close dialog when finished
		mProgressDialog.dismiss();
	}
}
