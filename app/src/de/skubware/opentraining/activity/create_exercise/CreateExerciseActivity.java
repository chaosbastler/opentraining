/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2013 Christian Skubich
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;


import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;
import android.widget.Toast;

/**
 * An activity for creating new {@link ExerciseType}s.
 * 
 */
public class CreateExerciseActivity extends ActionBarActivity implements
		ActionBar.TabListener {
	/** Tag for logging*/
	private final String TAG = "CreateExerciseActivity";
	
	/** Static String for preferences. */
	public final static String PREFERENCE_SHOW_SWIPE_TO_DISMISS_ADVISE = "SHOW_SWIPE_TO_DISMISS_ADVISE";

	
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;



	/** Static int for onActivityResult */
	static final int TAKE_PICTURE = 447;
	
	/** Static int for onActivityResult */
	static final int CHOSE_PICTURE = 555;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_exercise);

		// Set up the action bar.
		final ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setOffscreenPageLimit(mSectionsPagerAdapter.getCount());		
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(this));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_create_exercise, menu);
		

		// configure menu_create_exercise_info
		MenuItem menu_create_exercise_info = (MenuItem) menu.findItem(R.id.menuitem_create_exercise_info);
		menu_create_exercise_info.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				AlertDialog.Builder builder = new AlertDialog.Builder(CreateExerciseActivity.this);
				builder.setTitle(getString(R.string.title_about_creating_exercise));
				builder.setMessage(getString(R.string.text_about_creating_exercise));
				
				builder.create().show();


				return true;
			}
		});
		
		// configure menuitem_save_exercise
		MenuItem menuitem_save_exercise = (MenuItem) menu.findItem(R.id.menuitem_save_exercise);
		menuitem_save_exercise.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {				
				saveExercise();
				return true;
			}
		});
		
		return true;
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	/** Saves the created exercise. */
	private void saveExercise(){
		NameFragment nameFragment = (NameFragment) mSectionsPagerAdapter.getItem(0);
		DescriptionFragment descriptionFragment = (DescriptionFragment) mSectionsPagerAdapter.getItem(1);
		ImageFragment imageFragment = (ImageFragment) mSectionsPagerAdapter.getItem(2);
		SimpleDataFragment<Muscle> muscleDataFragment = (SpinnerDataFragment) mSectionsPagerAdapter.getItem(3);
		EquipmentDataFragment equipmentDataFragment = (EquipmentDataFragment) mSectionsPagerAdapter.getItem(4);

		DataProvider dataProvider = new DataProvider(this);

		
		// handle names
		Map<Locale, String> translationMap = nameFragment.getTranslationMap();
		//String ex_name_english = basicDataFragment.getExerciseNameEnglish();
		//String ex_name_german = basicDataFragment.getExerciseNameGerman();
		
		if(translationMap.isEmpty()){
			Log.v(TAG, "User did not enter an exercise name.");
			Toast.makeText(this, getString(R.string.provide_name), Toast.LENGTH_LONG).show();
			
			return;
		}
		
		/*if(ex_name_english.equals("") && ex_name_german.equals("")){
			Log.v(TAG, "User did not enter an exercise name.");
			Toast.makeText(this, getString(R.string.provide_name), Toast.LENGTH_LONG).show();
			
			return;
		}
		
		
		
		if(!ex_name_english.equals("")){
			if(dataProvider.getExerciseByName(ex_name_english) != null){
				Toast.makeText(this, getString(R.string.name_already_used), Toast.LENGTH_LONG).show();
				return;
			}
			
			translationMap.put(Locale.ENGLISH, ex_name_english);
		}
			
		if(!ex_name_german.equals("")){
			if(dataProvider.getExerciseByName(ex_name_german) != null){
				Toast.makeText(this, getString(R.string.name_already_used), Toast.LENGTH_LONG).show();
				return;
			}
			
			translationMap.put(Locale.GERMAN, ex_name_german);
		}*/
		
		// handle description
		String description = descriptionFragment.getExerciseDescription();
			
		// handle muscle
		SortedSet<Muscle> muscleList = new TreeSet<Muscle>(muscleDataFragment.getChosenObjects());

		// handle equipment
		SortedSet<SportsEquipment> equipmentList = new TreeSet<SportsEquipment>(equipmentDataFragment.getChosenObjects());

		// save image
		Map<String,Bitmap> imageMap = imageFragment.getImages();
		List<File> imageList = new ArrayList<File>();
		
		for(String imgName:imageMap.keySet()){
			imageList.add(new File(imgName));
		}

		
		
		ExerciseType.Builder exerciseBuilder = new ExerciseType.Builder(translationMap.values().iterator().next()).description(description).translationMap(translationMap).activatedMuscles(muscleList).neededTools(equipmentList).imagePath(imageList);
		ExerciseType ex = exerciseBuilder.build();
		
		// save exercise
		boolean succ = dataProvider.saveCustomExercise(ex);
		

    
		
		if(!succ){
			Toast.makeText(this, getString(R.string.could_not_save_exercise), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, getString(R.string.exercise_saved), Toast.LENGTH_LONG).show();
		}
	}


	
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		NameFragment mNameFragment = new NameFragment();
		DescriptionFragment mDescriptionFragment = new DescriptionFragment();
		ImageFragment mImageFragment= new ImageFragment();
		MuscleDataFragment mMuscleDataFragment = new MuscleDataFragment();
		EquipmentDataFragment mEquipmentDataFragment = new EquipmentDataFragment();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 0:
				return mNameFragment;
			case 1:
				return mDescriptionFragment;
			case 2:	
				return mImageFragment;
			case 3:	
				return mMuscleDataFragment;
			case 4:
				return mEquipmentDataFragment;
			}
			
			throw new IllegalStateException("No fragment for position: " + position);
		}

		@Override
		public int getCount() {
			return 5;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_name_fragment).toUpperCase(Locale.GERMANY);
			case 1:
				return getString(R.string.title_description_fragment).toUpperCase(Locale.GERMANY);
			case 2:	
				return getString(R.string.title_image_fragment).toUpperCase(Locale.GERMANY);
			case 3:	
				return getString(R.string.title_muscle_data_fragment).toUpperCase(Locale.GERMANY);
			case 4:
				return getString(R.string.title_equipment_data_fragment).toUpperCase(Locale.GERMANY);
			}	
			return null;
		}
	}


	/**
	 * Shows a toast message that explains "swipe-to-dismiss".
	 * Will only be shown once.
	 */
	public void swipeToDismissAdvise(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		/*boolean showAdvise = sharedPrefs.getBoolean(PREFERENCE_SHOW_SWIPE_TO_DISMISS_ADVISE, true);
		if(!showAdvise){
			Log.v(TAG, "Will not show swipe-to-dismiss-advise");
			return;
		}*/
			
		Toast.makeText(this, getString(R.string.swipe_to_dismiss_advise), Toast.LENGTH_LONG).show();
		
		/*Log.v(TAG, "Show swipe-to-dismiss-advise has been shown once, will not be shown again.");

		Editor editor = sharedPrefs.edit();
		editor.putBoolean(PREFERENCE_SHOW_SWIPE_TO_DISMISS_ADVISE, false);
		editor.commit();*/
	}
	
}
