package de.skubware.opentraining.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;

/**
 * A class to handle I/O stuff that is not related to database or parsing .xml
 * files.
 * 
 */
public class DataHelper {

	/** Tag for logging */
	private static final String TAG = "DataHelper";

	private Context mContext;

	/**
	 * Constructor.
	 * 
	 * @param context
	 *            The current context.
	 */
	public DataHelper(Context context) {
		mContext = context;
	}

	/**
	 * Returns a {@code Drawable} when an image with such a name is there (in
	 * assets folder or the custom exercise folder).
	 * 
	 * @param name
	 *            The name of the image
	 * @return The generated Drawable or null if the image could not be found
	 */
	public Drawable getDrawable(String name) {
		Log.v(TAG, "Trying to get drawable " + name);
		if(!drawableExist(name)){
			return null;
		}
		
		InputStream is = null;
		Drawable img = null;
		try {
			is = mContext.getAssets().open(DataProvider.EXERCISE_FOLDER + "/" + name);
			img = Drawable.createFromStream(is, "icon");
			is.close();
		} catch (FileNotFoundException e) {			
			if(img == null){
				// Could not find drawable in assets folder. Will try to find image in custom image folder
				img = Drawable.createFromPath(mContext.getFilesDir().toString() + "/" + IDataProvider.CUSTOM_IMAGES_FOLDER + "/" + name);
			}
			
			if(img == null){
				// Could not find drawable in assets or custom image folder. Will try to find image in synced image folder
				img = Drawable.createFromPath(mContext.getFilesDir().toString() + "/" + IDataProvider.SYNCED_IMAGES_FOLDER + "/" + name);
			}

			if(img == null){
				Log.e(TAG, "Could not find drawable in assets, custom image folder or in synced image folder: " + name + "\n", e);
			}
			
		} catch (IOException e) {
			Log.e(TAG, "Could not find drawable :" + name + "\n", e);
		}
	
		
		return img;
	}
	
	public boolean drawableExist(String name){
		// check default exercises
		boolean imageInDefaultFolderExists = false;
		try{
			// assets have to be treated special
			// e.g. there are only lower case filenames
			String[] defaultImages = mContext.getAssets().list(DataProvider.EXERCISE_FOLDER);
			if(java.util.Arrays.asList(defaultImages).contains(name)){
				imageInDefaultFolderExists = true;
			}	
		}catch (IOException e) {
			Log.e(TAG, "IOException during searching for drawable: " + name + "\n", e);
		}
		
		// check custom and synced images
		File imgInCustomFolder = new File(mContext.getFilesDir().toString() + "/" + DataProvider.CUSTOM_IMAGES_FOLDER + "/" + name);
		File imgInSyncedFolder = new File(mContext.getFilesDir().toString() + "/" + DataProvider.SYNCED_IMAGES_FOLDER + "/" + name);
		
		boolean exactlyOneExists = imageInDefaultFolderExists ^ imgInCustomFolder.exists() ^ imgInSyncedFolder.exists();
		boolean atLeastOneExists =  imageInDefaultFolderExists || imgInCustomFolder.exists() || imgInSyncedFolder.exists();
		if(atLeastOneExists && !exactlyOneExists){
			Log.wtf(TAG, "There seems to be more than one image with the name: " + name + ". This should not happen.");
		}
		if(!atLeastOneExists){
			Log.v(TAG, "Drawable " + name + " does not exist (yet).");
		}
		return atLeastOneExists;
	}
	
	
	/**
	 * Copies the image with the Uri to the custom images folder.
	 * 
	 * @param source
	 *            The Uri of the image to copy
	 * 
	 * @return The name of the created image
	 */
	public String copyImageToCustomImageFolder(Uri source) {
		File destinationFolder = new File(mContext.getFilesDir().toString() + "/" + IDataProvider.CUSTOM_IMAGES_FOLDER);
		destinationFolder.mkdirs();
		
		String image_base_name = "img_";
		String image_name = image_base_name + "0.jpg";
		List<String> files = Arrays.asList(destinationFolder.list());
		for(Integer i = 0; files.contains(image_name); i++){
			image_name = image_base_name + i + ".jpg";
		}
		
		File destination  = new File(destinationFolder.toString() + "/"+ image_name);
		
		InputStream inputStream = null;
		OutputStream outputStream = null;

		try {
			ContentResolver content = mContext.getContentResolver();
			inputStream = content.openInputStream(source);

			outputStream = new FileOutputStream(destination);
			if (outputStream != null) {
				Log.e(TAG, "Output Stream Opened successfully");
			}

			byte[] buffer = new byte[1000];
			while ( inputStream.read(buffer, 0, buffer.length) >= 0) {
				outputStream.write(buffer, 0, buffer.length);
			}
			outputStream.close();
		} catch (Exception e) {
			Log.e(TAG, "Exception occurred during saving.", e);
			return null;
		}
		
		Log.d(TAG, "Copied image to " + destination.toString());
		
		return image_name;
	}
	

	/**
	 * Loads a file from the raw folder.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	public String loadFileFromRaw(String fileName) throws IOException {
		Resources resources = mContext.getResources();

		// Create a InputStream to read the file into
		int rID = resources.getIdentifier("de.skubware.opentraining:raw/" + fileName, null, null);

		// get the file as a stream
		InputStream is = resources.openRawResource(rID);

		return loadFile(is);
	}

	/**
	 * Loads a file from the assets folder.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	public String loadFileFromAssets(String fileName) throws IOException {
		Resources resources = mContext.getResources();
		InputStream is = resources.getAssets().open(fileName);
		return loadFile(is);
	}

	/**
	 * Loads a file from the assets folder.
	 * 
	 * @param fileName
	 *            The name/path of the file
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	public String loadFileFromFileSystem(String fileName) throws IOException {
		InputStream is = new FileInputStream(new File(fileName));
		return loadFile(is);
	}

	/**
	 * Loads the InputStream.
	 * 
	 * @param is
	 *            The InputStream to read
	 * 
	 * @return String with the content of the file
	 * 
	 * @throws IOException
	 *             if loading file fails
	 */
	public String loadFile(InputStream is) throws IOException {
		// create a buffer that has the same size as the InputStream
		byte[] buffer = new byte[is.available()];
		is.read(buffer);
		ByteArrayOutputStream oS = new ByteArrayOutputStream();
		oS.write(buffer);
		oS.close();
		is.close();

		// return the output stream as a String
		return oS.toString();
	}

}
