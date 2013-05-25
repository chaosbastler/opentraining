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

import com.actionbarsherlock.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.MenuItem.OnMenuItemClickListener;

import de.skubware.opentraining.activity.manage_workouts.RenameWorkoutDialogFragment;
import de.skubware.opentraining.basic.Muscle;
import de.skubware.opentraining.basic.SportsEquipment;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreateExerciseActivity extends SherlockFragmentActivity implements
		ActionBar.TabListener {
	/** Tag for logging*/
	private final String TAG = "CreateExerciseActivity";

	
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
				builder.setTitle("About creating exercises");
				String msg = "You have to provide an exercise name, everything else is optional. If you want to you can even publish your exercise.";
				builder.setMessage(msg);
				
				builder.create().show();


				return true;
			}
		});
		
		// configure menu_item_rename_workout
		MenuItem menuitem_save_exercise = (MenuItem) menu.findItem(R.id.menuitem_save_exercise);
		menu_create_exercise_info.setOnMenuItemClickListener(new OnMenuItemClickListener() {
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
				return new BasicDataFragment();
			case 1:
				return new ExtendedDataFragment();
			case 2:
				return new PublishFragment();
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
				return "Basic data";//getString(R.string.title_section1).toUpperCase();
			case 1:
				return "Extended data";//getString(R.string.title_section2).toUpperCase();
			case 2:
				return "Publish";
			}	
			return null;
		}
	}


	
	
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class BasicDataFragment extends Fragment {
		/** Tag for logging*/
		private final String TAG = "BasicDataFragment";
		
		/** The ImageView with the exercise image */
		private ImageView mImageView;
		
		/** Uri of the image that is returned by the Intent */
		private Uri mTempImageUri;
		
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
					Toast.makeText(getActivity(), "You did not provide an image.",
							Toast.LENGTH_SHORT).show();
				}
			}
		}
		
		
	}
	
	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class ExtendedDataFragment extends Fragment {


		public ExtendedDataFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.fragment_create_exercise_extended_data, container, false);

			IDataProvider dataProvider = new DataProvider(getActivity());
			
			Spinner spinner_muscle = (Spinner) layout.findViewById(R.id.spinner_muscle);
			ArrayAdapter<Muscle> madapter = new ArrayAdapter<Muscle>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, dataProvider.getMuscles());
			spinner_muscle.setAdapter(madapter);
			
			Spinner spinner_equipment = (Spinner) layout.findViewById(R.id.spinner_equipment);
			ArrayAdapter<SportsEquipment> eqadapter = new ArrayAdapter<SportsEquipment>(getActivity(), android.R.layout.simple_spinner_dropdown_item, android.R.id.text1, dataProvider.getEquipment());
			spinner_equipment.setAdapter(eqadapter);
			
			return layout;
		}
	}
	

	public static class PublishFragment extends Fragment {

		public PublishFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View layout = inflater.inflate(R.layout.fragment_create_exercise_publish, container, false);

			return layout;
		}
	}
}
