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

package de.skubware.opentraining;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.actionbarsherlock.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import de.skubware.opentraining.activity.start_training.SwipeDismissListViewTouchListener;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataHelper;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * An activity for creating new {@link ExerciseType}s.
 * 
 */
public class CreateExerciseActivity extends SherlockFragmentActivity implements
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
	private static final int TAKE_PICTURE = 447;
	
	
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
		getSupportMenuInflater().inflate(R.menu.activity_create_exercise, menu);
		

		// configure menu_item_rename_workout
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
		
		// configure menu_item_rename_workout
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
		BasicDataFragment basicDataFragment = (BasicDataFragment) mSectionsPagerAdapter.getItem(0);
		MuscleDataFragment muscleDataFragment = (MuscleDataFragment) mSectionsPagerAdapter.getItem(1);
		EquipmentDataFragment equipmentDataFragment = (EquipmentDataFragment) mSectionsPagerAdapter.getItem(2);

		// handle names
		Map<Locale, String> translationMap = new HashMap<Locale, String>();
		String ex_name_english = basicDataFragment.getExerciseNameEnglish();
		String ex_name_german = basicDataFragment.getExerciseNameGerman();
		
		if(ex_name_english.equals("") && ex_name_german.equals("")){
			Log.v(TAG, "User did not enter an exercise name.");
			Toast.makeText(this, getString(R.string.provide_name), Toast.LENGTH_LONG).show();
			
			return;
		}
		
		if(!ex_name_english.equals(""))
			translationMap.put(Locale.ENGLISH, ex_name_english);

		if(!ex_name_german.equals(""))
			translationMap.put(Locale.GERMAN, ex_name_german);
		
		// handle muscle
		SortedSet<Muscle> muscleList = new TreeSet<Muscle>(muscleDataFragment.getMuscles());

		// handle equipment
		SortedSet<SportsEquipment> equipmentList = new TreeSet<SportsEquipment>(equipmentDataFragment.getSportsEquipment());

		// save image
		Uri image = basicDataFragment.getImage();
		List<File> imageList = new ArrayList<File>();

		if(image != null){
			DataHelper dataHelper = new DataHelper(this);
			String image_name = dataHelper.copyImageToCustomImageFolder(image);
			imageList.add(new File(image_name));
		}

		
		
		ExerciseType.Builder exerciseBuilder = new ExerciseType.Builder(translationMap.values().iterator().next()).translationMap(translationMap).activatedMuscles(muscleList).neededTools(equipmentList).imagePath(imageList);
		ExerciseType ex = exerciseBuilder.build();
		
		// save exercise
		DataProvider dataProvider = new DataProvider(this);
		boolean succ = dataProvider.saveExercise(ex);
		

    
		
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
		
		BasicDataFragment mBasicDataFragment = new BasicDataFragment();
		MuscleDataFragment mMuscleDataFragment = new MuscleDataFragment();
		EquipmentDataFragment mEquipmentDataFragment = new EquipmentDataFragment();

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {

			switch (position) {
			case 0:
				return mBasicDataFragment;
			case 1:
				return mMuscleDataFragment;
			case 2:
				return mEquipmentDataFragment;
			}
			
			throw new IllegalStateException("No fragment for position: " + position);
		}

		@Override
		public int getCount() {
			return 3;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
			case 0:
				return getString(R.string.title_basic_data_fragment).toUpperCase(Locale.GERMANY);
			case 1:
				return getString(R.string.title_muscle_data_fragment).toUpperCase(Locale.GERMANY);
			case 2:
				return getString(R.string.title_equipment_data_fragment).toUpperCase(Locale.GERMANY);
			}	
			return null;
		}
	}


	
	

	public static class BasicDataFragment extends Fragment {
		/** Tag for logging*/
		private final String TAG = "BasicDataFragment";
		
		/** The ImageView with the exercise image */
		private ImageView mImageView;
		
		/** Uri of the image that is returned by the Intent */
		private Uri mTempImageUri = null;
		
		private TextView mTextViewExerciseNameEnglish;
		private TextView mTextViewExerciseNameGerman;

		
		public BasicDataFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.fragment_create_exercise_basic_data, container, false);

			mImageView = (ImageView) layout.findViewById(R.id.imageview_exercise_image);
			mImageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					takePhoto(v);					
				}
			});
			
			mTextViewExerciseNameEnglish = (TextView) layout.findViewById(R.id.edittext_exercise_name_english);
			mTextViewExerciseNameGerman = (TextView) layout.findViewById(R.id.edittext_exercise_name_german);
			
			return layout;
		}
		

		public void takePhoto(View view) {
		    Intent intent = new Intent(	android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		    File photo = new File(Environment.getExternalStorageDirectory(),  "temp_pic.jpg");
		    intent.putExtra(MediaStore.EXTRA_OUTPUT,
		            Uri.fromFile(photo));
		    mTempImageUri = Uri.fromFile(photo);
		    startActivityForResult(intent, TAKE_PICTURE);
		}
		
		

		@Override
		public void onActivityResult(int requestCode, int resultCode,
				Intent data) {
			Log.v(TAG, "onActivityResult(), requestCode=" + requestCode
					+ ", resultCode=" + resultCode);
			super.onActivityResult(requestCode, resultCode, data);
			switch (requestCode) {
			case TAKE_PICTURE:
				if (resultCode == Activity.RESULT_OK) {
					Uri selectedImage = mTempImageUri;
					getActivity().getContentResolver().notifyChange(
							selectedImage, null);
					ContentResolver cr = getActivity().getContentResolver();
					Bitmap bitmap;
					try {
						bitmap = android.provider.MediaStore.Images.Media
								.getBitmap(cr, selectedImage);

						mImageView.setImageBitmap(bitmap);
						Toast.makeText(getActivity(), selectedImage.toString(),
								Toast.LENGTH_LONG).show();
					} catch (Exception e) {
						Toast.makeText(getActivity(), "Failed to load",
								Toast.LENGTH_SHORT).show();
						Log.e(TAG, e.toString(), e);
					}
				}else{
					Toast.makeText(getActivity(), getString(R.string.did_not_provide_image),
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		public String getExerciseNameEnglish(){
			return mTextViewExerciseNameEnglish.getText().toString();
		}
		
		public String getExerciseNameGerman(){
			return mTextViewExerciseNameGerman.getText().toString();
		}
		
		public Uri getImage(){
			return mTempImageUri;
		}
	}
	


	public static class MuscleDataFragment extends Fragment implements OnItemSelectedListener{
		private Spinner mMuscleSpinner;
		
		private ListView mMuscleListView;
		private ArrayAdapter<Muscle> mListAdapter;
		private List<Muscle> mMuscleList = new ArrayList<Muscle>();


		public MuscleDataFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.fragment_create_exercise_muscle_data, container, false);

			IDataProvider dataProvider = new DataProvider(getActivity());
			
			mMuscleSpinner = (Spinner) layout.findViewById(R.id.spinner_muscle);
			ArrayAdapter<Muscle> madapter = new ArrayAdapter<Muscle>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, dataProvider.getMuscles());
			mMuscleSpinner.setAdapter(madapter);
			// if you dont post a runnable, the first item will be added to the mListAdapter on activity start
			mMuscleSpinner.post(new Runnable() {
				public void run() {
					mMuscleSpinner
							.setOnItemSelectedListener(MuscleDataFragment.this);
					;
				}
			});
			
			mMuscleListView = (ListView) layout.findViewById(R.id.listview_ex_muscle);
			mListAdapter = new ArrayAdapter<Muscle>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, mMuscleList);
			mMuscleListView.setAdapter(mListAdapter);
			
			
			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
					mMuscleListView,
					new SwipeDismissListViewTouchListener.OnDismissCallback() {
						@Override
						public void onDismiss(ListView listView,
								int[] reverseSortedPositions) {
							for (int position : reverseSortedPositions) {
								mListAdapter.remove((Muscle)(mListAdapter.getItem(position)));
							}
							mListAdapter.notifyDataSetChanged();
						}
					});
			mMuscleListView.setOnTouchListener(touchListener);			
			
			return layout;
		}
		
		public List<Muscle> getMuscles(){
			return mMuscleList;
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			Muscle selectedItem = (Muscle) mMuscleSpinner.getItemAtPosition(position);
			if(mMuscleList.contains(selectedItem)){
				Toast.makeText(getActivity(), getString(R.string.muscle_already_in_list), Toast.LENGTH_LONG).show();
				return;
			}
			
			
			mListAdapter.add(selectedItem);
			((CreateExerciseActivity) getActivity()).swipeToDismissAdvise();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}		
	}
	


	public static class EquipmentDataFragment extends Fragment implements OnItemSelectedListener{
		private Spinner mEquipmentSpinner;
	
		private ListView mEquipmentListView;
		private ArrayAdapter<SportsEquipment> mListAdapter;
		private List<SportsEquipment> mEquipmentList = new ArrayList<SportsEquipment>();
		
		public EquipmentDataFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.fragment_create_exercise_equipment_data, container, false);
			
			IDataProvider dataProvider = new DataProvider(getActivity());

			mEquipmentSpinner = (Spinner) layout.findViewById(R.id.spinner_equipment);
			ArrayAdapter<SportsEquipment> eqadapter = new ArrayAdapter<SportsEquipment>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, dataProvider.getEquipment());
			mEquipmentSpinner.setAdapter(eqadapter);
			// if you dont post a runnable, the first item will be added to the mListAdapter on activity start
			mEquipmentSpinner.post(new Runnable() {
				public void run() {
					mEquipmentSpinner
							.setOnItemSelectedListener(EquipmentDataFragment.this);
					;
				}
			});
			
			mEquipmentListView = (ListView) layout.findViewById(R.id.listview_ex_equipment);
			mListAdapter = new ArrayAdapter<SportsEquipment>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, mEquipmentList);
			mEquipmentListView.setAdapter(mListAdapter);
			
			
			SwipeDismissListViewTouchListener touchListener = new SwipeDismissListViewTouchListener(
					mEquipmentListView,
					new SwipeDismissListViewTouchListener.OnDismissCallback() {
						@Override
						public void onDismiss(ListView listView,
								int[] reverseSortedPositions) {
							for (int position : reverseSortedPositions) {
								mListAdapter.remove((SportsEquipment)(mListAdapter.getItem(position)));
							}
							mListAdapter.notifyDataSetChanged();
						}
					});
			mEquipmentListView.setOnTouchListener(touchListener);			
			
			
			return layout;
		}
		
		
		public List<SportsEquipment> getSportsEquipment(){
			return mEquipmentList;
		}

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			SportsEquipment selectedItem = (SportsEquipment) mEquipmentSpinner.getItemAtPosition(position);
			if(mEquipmentList.contains(selectedItem)){
				Toast.makeText(getActivity(), getString(R.string.equipment_already_in_list), Toast.LENGTH_LONG).show();
				return;
			}
			
			mListAdapter.add(selectedItem);
			((CreateExerciseActivity) getActivity()).swipeToDismissAdvise();
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			
		}
	}
	
	/**
	 * Shows a toast message that explains "swipe-to-dismiss".
	 * Will only be shown once.
	 */
	public void swipeToDismissAdvise(){
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		boolean showAdvise = sharedPrefs.getBoolean(PREFERENCE_SHOW_SWIPE_TO_DISMISS_ADVISE, true);
		if(!showAdvise){
			Log.v(TAG, "Will not show swipe-to-dismiss-advise");
			return;
		}
			
		Toast.makeText(this, getString(R.string.swipe_to_dismiss_advise), Toast.LENGTH_LONG).show();
		
		Log.v(TAG, "Show swipe-to-dismiss-advise has been shown once, will not be shown again.");

		Editor editor = sharedPrefs.edit();
		editor.putBoolean(PREFERENCE_SHOW_SWIPE_TO_DISMISS_ADVISE, false);
		editor.commit();
	}
	
}
