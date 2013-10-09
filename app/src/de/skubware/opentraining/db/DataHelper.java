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
	 * @return The generated Drawable
	 */
	public Drawable getDrawable(String name) {
		Log.v(TAG, "Trying to get drawable " + name);
		InputStream is = null;
		Drawable img = null;
		try {
			is = mContext.getAssets().open(DataProvider.EXERCISE_FOLDER + "/" + name);
			img = Drawable.createFromStream(is, "icon");
			is.close();
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Could not find drawable: " + name + "\n", e);
		} catch (IOException e) {
			Log.e(TAG, "Could not find drawable: " + name + "\n", e);
		}
		
		if(img == null){
			Log.d(TAG, "Could not find drawable: " + name + " in assets folder. Will try to find image in custom image folder.");
			img = Drawable.createFromPath(mContext.getFilesDir().toString() + "/" + IDataProvider.CUSTOM_IMAGES_FOLDER + "/" + name);
		}

		if(img == null){
			Log.e(TAG, "Could not find drawable in custom image folder: " + name + "\n");
		}
		
		return img;
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
