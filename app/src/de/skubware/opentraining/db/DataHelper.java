package de.skubware.opentraining.db;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
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
}
