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

package de.skubware.opentraining.activity.create_workout;

import java.util.List;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;

/**
 * A dialog for selecting the {@link Muscle}s and {@link SportsEquipment} that
 * should be shown.
 * 
 */
public class DialogFilterMusclesAndEquipment extends Dialog {
	/** Tag for logging */
	public static final String TAG = "DialogSearchExercise";

	/** Reference to the Activity that started this dialog. */
	private Context mContext;

	public DialogFilterMusclesAndEquipment(Context context) {
		super(context);

		mContext = context;

		setTitle(mContext.getString(R.string.settings));
		setContentView(R.layout.dialog_filter_muscle_equipment);

		TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
		tabHost.setup();

		// init data provider
		IDataProvider dataProvider = new DataProvider(mContext);

		// create muscleTab
		TabHost.TabSpec muscleTab = tabHost.newTabSpec("tab1");
		muscleTab.setIndicator(mContext.getString(R.string.muscles), mContext.getResources().getDrawable(R.drawable.icon_muscle));
		muscleTab.setContent(R.id.listview_muscles);
		tabHost.addTab(muscleTab);

		// fill muscleTab
		ListView listViewMuscles = (ListView) findViewById(R.id.listview_muscles);
		final List<Muscle> muscleList = dataProvider.getMuscles();

		simulatePreference(muscleList, listViewMuscles);

		// create equipmentTab
		TabHost.TabSpec equipmentTab = tabHost.newTabSpec("tab2");
		equipmentTab.setIndicator(mContext.getString(R.string.equipment), mContext.getResources().getDrawable(R.drawable.icon_equipment));
		equipmentTab.setContent(R.id.listview_equipment);
		tabHost.addTab(equipmentTab);

		// fill equipmentTab
		ListView listViewEquipment = (ListView) findViewById(R.id.listview_equipment);
		final List<SportsEquipment> equipmentList = dataProvider.getEquipment();

		simulatePreference(equipmentList, listViewEquipment);
	}

	/**
	 * Makes a ListView do the same things as a PreferenceActivity would do.
	 * That means that the last settings are loaded from the SharedPreferences
	 * and that every change(check/uncheck) will be immediatelly saved back to
	 * the preferences. The ListView will be set to the choice mode
	 * ListView.CHOICE_MODE_MULTIPLE.
	 * 
	 * @param list
	 *            The list of things that should be shown in the ListView.
	 * @param listView
	 *            The ListView that should show the things the user can choose
	 */
	private <T> void simulatePreference(final List<T> list, ListView listView) {
		final SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);

		listView.setAdapter(new ArrayAdapter<T>(mContext, android.R.layout.simple_list_item_multiple_choice, android.R.id.text1, list));
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

		for (T e : list) {
			boolean shouldBeChecked = sharedPrefs.getBoolean(e.toString(), true);
			listView.setItemChecked(list.indexOf(e), shouldBeChecked);
		}

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View arg1, int position, long arg3) {

				String selectedThing = list.get(position).toString();
				Boolean prev = sharedPrefs.getBoolean(selectedThing, true);
				Log.d(TAG, "selected: " + selectedThing + "; prev: " + prev + ", new val: " + !prev);

				Editor editor = sharedPrefs.edit();
				editor.putBoolean(list.get(position).toString(), !prev);
				editor.commit();
			}
		});
	}

}