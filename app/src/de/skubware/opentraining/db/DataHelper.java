package de.skubware.opentraining.db;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;

/**
 * A class to handle I/O stuff that is not related to database or parsing .xml
 * files.
 * 
 * @author Christian Skubich
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
	 * assets folder).
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

		return img;
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
