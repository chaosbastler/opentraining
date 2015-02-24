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

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import android.support.v4.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;


import de.skubware.opentraining.R;
import de.skubware.opentraining.activity.create_exercise.ExerciseImageListAdapter.ImageData;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.License;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.basic.ExerciseType.ExerciseSource;
import de.skubware.opentraining.db.Cache;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
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
		

		return true;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		
		// clean up images
		ImageFragment imageFragment = (ImageFragment) mSectionsPagerAdapter.getItem(2);
		List<ImageData> images = imageFragment.getImages();
		IDataProvider dataProvider = new DataProvider(this);
		Cache.INSTANCE.updateCache(this);

		for(ImageData image:images){
			// remove not used/referenced images
			dataProvider.deleteCustomImage(image.name, true);
		}
		
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

	private static String makeFragmentName(int viewId, int index) {
	     return "android:switcher:" + viewId + ":" + index;
	}
	
	/** Saves the created exercise. */
	@SuppressWarnings("unchecked")
	private void saveExercise(){
		NameFragment nameFragment = (NameFragment)  getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,0));//  mSectionsPagerAdapter.getItem(0);
		DescriptionFragment descriptionFragment = (DescriptionFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,1));
		ImageFragment imageFragment = (ImageFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,2));
		SimpleDataFragment<Muscle> muscleDataFragment = (SpinnerDataFragment<Muscle>) getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,3));
		EquipmentDataFragment equipmentDataFragment = (EquipmentDataFragment) getSupportFragmentManager().findFragmentByTag(makeFragmentName(R.id.pager,4));

		DataProvider dataProvider = new DataProvider(this);

		
		// handle names
		Map<Locale, String> translationMap = nameFragment.getTranslationMap();
		
		if(translationMap.isEmpty()){
			Log.v(TAG, "User did not enter an exercise name.");
			Toast.makeText(this, getString(R.string.provide_name), Toast.LENGTH_LONG).show();
			
			return;
		}
		
		
		// handle description
		String description = descriptionFragment.getExerciseDescription();
			
		// handle muscle
		SortedSet<Muscle> muscleList = new TreeSet<Muscle>(muscleDataFragment.getChosenObjects());

		// handle equipment
		SortedSet<SportsEquipment> equipmentList = new TreeSet<SportsEquipment>(equipmentDataFragment.getChosenObjects());

		// save image
		List<ImageData> imageDataList = imageFragment.getImages();
		List<File> imageList = new ArrayList<File>();
		Map<File, License> imageLicenseMap = new HashMap<File, License>();
		for(ImageData image:imageDataList){
			File f = new File(image.name);
			imageList.add(f);
			imageLicenseMap.put(f, image.imageLicense);
		}

		
		ExerciseType.Builder exerciseBuilder = new ExerciseType.Builder(translationMap.values().iterator().next(), ExerciseSource.CUSTOM).description(description).translationMap(translationMap).activatedMuscles(muscleList).neededTools(equipmentList).imagePath(imageList).imageLicenseMap(imageLicenseMap);
		ExerciseType ex = exerciseBuilder.build();
		
		// save exercise
		boolean succ = dataProvider.saveCustomExercise(ex);
    
		
		if(!succ){
			Toast.makeText(this, getString(R.string.could_not_save_exercise), Toast.LENGTH_LONG).show();
		}else{
			Toast.makeText(this, getString(R.string.exercise_saved), Toast.LENGTH_LONG).show();
			finish();
		}
	}
	
	/**
	 * Show Dialog when user wants to go back.
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
			dialogBuilder.setTitle(getString(R.string.save_exercise))
			.setMessage(getString(R.string.should_exercise_be_saved))
			.setNegativeButton(getString(R.string.discard_exercise), new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					finish();
				}
			})
			.setPositiveButton(getString(R.string.save), new OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					saveExercise();
				}
			}).create().show();			
		}
		return super.onKeyDown(keyCode, event);
	}


	
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 0:
				return new NameFragment();
			case 1:
				return new DescriptionFragment();
			case 2:	
				return new ImageFragment();
			case 3:	
				return new MuscleDataFragment();
			case 4:
				return new EquipmentDataFragment();
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
	 */
	public void swipeToDismissAdvise(){
		Toast.makeText(this, getString(R.string.swipe_to_dismiss_advise), Toast.LENGTH_LONG).show();
	}
	
}
