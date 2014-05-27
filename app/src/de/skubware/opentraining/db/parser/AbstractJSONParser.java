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

package de.skubware.opentraining.db.parser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import android.util.Log;

/**
 * Superclass for JSON Parser that implements {@link IParser}.
 * 
 * @author Christian Skubich
 *
 * @param <T> The type that can be parsed with this class.
 */
public abstract class AbstractJSONParser<T> implements IParser<T> {
	/** Tag for logging */
	public static final String TAG = "AbstractJSONParser<>";
	
	protected String streamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	@Override
	public abstract T parse(String data);

	@Override
	public final T parse(File file){
		InputStream stream;
		try {
			stream = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			Log.e(TAG, "Error during parsing JSON File.", e);
			return null;
		}
		return parse(stream);
	}

	@Override
	public final T parse(InputStream is){
		try {
			return parse(streamToString(is));
		} catch (IOException e) {
			Log.e(TAG, "Error during parsing JSON File.", e);
			return null;
		}
	}

}
