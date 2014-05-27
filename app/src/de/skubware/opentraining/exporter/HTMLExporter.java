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

package de.skubware.opentraining.exporter;

import java.io.IOException;

import android.content.Context;
import android.util.Log;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;
import de.skubware.opentraining.db.DataHelper;


/**
 * Implementation of {@link WorkoutExporter} that exports {@link Workout} to
 * .html files.
 * 
 */
public class HTMLExporter extends WorkoutExporter {
	/** Tag for logging */
	public static final String TAG = "HTMLExporter";
	
	/** The template .html file */
	public final static String TEMPLATE_FILE = "trainingplan_template.html";

	/** The (default) CSS file */
	public final static String CSS_FILE = "trainingplan_green.css";


	/**
	 * Default constructor.
	 * 
	 * @param context
	 *            The current context.
	 */
	public HTMLExporter(Context context) {
		super(context);
	}



	
	/**
	 * {@inheritDoc}
	 * 
	 * {@link FSet}s are currently ignored and will not be exported. 
	 */
	@Override
	public String exportWorkoutToString(Workout w) {
		DataHelper dataHelper = new DataHelper(mContext);

		// StringBuilder would be more efficient, but does not support methods
		// like replaceAll()
		String data = "";

		try {
			data = dataHelper.loadFileFromAssets(TEMPLATE_FILE);
		} catch (IOException e) {
			Log.e(TAG, "Error loading template .html file", e);
			return "";
		}

		data = data.replaceAll("<!--WORKOUT_NAME-->", w.getName());

		// Exercises
		StringBuilder exes = new StringBuilder();
		for (FitnessExercise fEx : w.getFitnessExercises()) {
			exes.append("\t\t <th>");
			exes.append(fEx.toString());
			exes.append("</th>\n");
		}
		data = data.replaceAll("<!--EXERCISES-->", exes.toString());

		StringBuilder emptyRow = new StringBuilder();
		emptyRow.append("\t<tr>\n"); // an empty row
		for (int i = 0; i <= w.getFitnessExercises().size(); i++) {
			emptyRow.append("\t\t<td></td>\n");
		}
		emptyRow.append("\t</tr>\n");

		StringBuilder emptyRowOdd = new StringBuilder();
		emptyRowOdd.append("\t<tr class=\"alt\">\n"); // an empty row
		for (int i = 0; i <= w.getFitnessExercises().size(); i++) {
			emptyRowOdd.append("\t\t<td></td>\n");
		}
		emptyRowOdd.append("\t</tr>\n");

		StringBuilder emptyCells = new StringBuilder();
		boolean even = false;
		for (int i = 0; i < w.getEmptyRows(); i++) {
			if (even)
				emptyCells.append(emptyRowOdd.toString());
			else
				emptyCells.append(emptyRow.toString());
			even = !even;
		}

		data = data.replaceAll("<!--EMPTY_CELLS-->", emptyCells.toString());

		String cssFileAsString;
		try {
			cssFileAsString = dataHelper.loadFileFromAssets(CSS_FILE);
		} catch (IOException e) {
			Log.e(TAG, "Error loading template .html file", e);
			return data;
		}
		data = data.replaceAll("<!--CSS-->", cssFileAsString);

		return data;

	}

}
