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

import java.io.*;

import de.skubware.opentraining.basic.*;

import android.content.Context;

/**
 * A LaTeX Exporter class.
 * 
 * @author Christian Skubich
 * 
 */
public class LaTeXExporter extends WorkoutExporter {

	public LaTeXExporter(Context context) {
		super(context);
	}

	/**
	 * Creates the PDF File to the Path that was set in the BestandsDaten
	 * 
	 * @param pdfSettings
	 *            The settings for the creation
	 * 
	 * @throws FileNotFoundException
	 *             if the path could not be used
	 */
	public String exportWorkoutToString(Workout w) {

		Settings settings = new Settings();

		// public void create(Settings settings) throws FileNotFoundException{
		final String NEW = System.getProperty("line.separator");

		int colums = w.getFitnessExercises().size();
		int rowCount = settings.getRowCount();
		int rowSize = 3;
		int downFactor = settings.getDownFactor();
		int columnWidth = settings.getColumnWidth();
		System.out.println("Creating PDF " + w.getName() + " with the downFactor=" + downFactor + ", rowCount=" + rowCount);

		// build textfile
		StringBuilder tex = new StringBuilder();
		tex.append("\\documentclass[a4paper]{scrartcl}");
		tex.append(NEW);
		tex.append("\\usepackage{rotating}");
		tex.append(NEW);
		tex.append("\\usepackage{multirow}");
		tex.append(NEW);
		tex.append(NEW);

		tex.append("% deutsche Silbentrennung");
		tex.append(NEW);
		tex.append("\\usepackage[ngerman]{babel}");
		tex.append(NEW);

		tex.append("% wegen deutschen Umlauten");
		tex.append(NEW);
		tex.append("\\usepackage[utf8]{inputenc}");
		tex.append(NEW);
		tex.append(NEW);

		tex.append("% fuer zentrierte Tabellenzeilen �berschriften");
		tex.append(NEW);
		tex.append("\\usepackage{tabularx}");
		tex.append(NEW);
		tex.append("\\newcolumntype{C}[1]{>{\\centering\\arraybackslash}p{#1}} % zentriert mit Breitenangabe");
		tex.append(NEW);
		tex.append(NEW);

		tex.append("\\begin{document}");
		tex.append(NEW);
		tex.append("\\pagestyle{empty}");
		tex.append(NEW);
		tex.append("\\begin{sidewaystable}");
		tex.append(NEW);
		tex.append("\\centering");
		tex.append(NEW);
		tex.append("\\Large");
		tex.append(NEW);
		tex.append(NEW);

		tex.append("%Korrekturfaktor, Verschiebung nach unten");
		tex.append(NEW);
		tex.append("\\begin{addmargin}{-" + downFactor + "cm}");
		tex.append(NEW);
		tex.append(NEW);

		tex.append("%Ab hier beginnt die eigentliche Tabelle");
		tex.append(NEW);
		tex.append("\\begin{tabular}{ |l||");
		for (int i = 0; i < colums; i++) {
			tex.append("C{" + columnWidth + "mm}|");
		}
		tex.append("}");
		tex.append(NEW);
		tex.append(NEW);

		// Erste Zeile
		tex.append("\\hline");
		tex.append(NEW);
		tex.append("Datum ");
		for (FitnessExercise fEx : w.getFitnessExercises()) {
			tex.append("& " + fEx.getExType().getLocalizedName() + " ");
		}
		tex.append("\\\\");
		tex.append(NEW);
		tex.append(NEW);

		// S�tze
		int maxset = 0;
		for (FitnessExercise fEx : w.getFitnessExercises()) {
			if (fEx.getFSetList().size() > maxset)
				maxset = fEx.getFSetList().size();
		}
		tex.append("\\hline");
		tex.append(NEW);
		tex.append("\\hline");
		tex.append(NEW);
		tex.append("\\multirow{" + maxset + "}{14mm}");
		tex.append(NEW);

		// Satz Reihen Strings erzeugen
		String[][] set = new String[colums][maxset];
		int colum = 0;
		for (FitnessExercise fEx : w.getFitnessExercises()) {
			int row = 0;
			for (FSet s : fEx.getFSetList()) {
				set[colum][row] = s.toString();
				row++;
			}
			colum++;
		}

		for (int i = 0; i < maxset; i++) {
			for (int k = 0; k < colums; k++) {
				if (set[k][i] != null) {
					tex.append(" & " + set[k][i]);
				} else {
				}
			}
			tex.append("\\\\");
			tex.append(NEW);

		}
		// if(maxset>0){
		tex.append("\\hline");
		tex.append(NEW);
		// }

		// Freie Zeilen
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < colums; i++) {
			b.append("& ");
		}
		b.append("\\\\");
		b.append(NEW);

		// multiple bs for larger rows
		for (int i = 1; i < rowSize; i++) {
			b.append(b);
		}
		b.append("\\hline");
		b.append(NEW);

		String NEWCOLUM = b.toString();

		for (int i = 0; i < rowCount; i++) {
			tex.append(NEWCOLUM);
		}

		tex.append("\\end{tabular}");
		tex.append("% Tabellen Ende");
		tex.append(NEW);
		tex.append("\\end{addmargin}");
		tex.append(NEW);
		tex.append(NEW);

		tex.append("\\end{sidewaystable}");
		tex.append(NEW);
		tex.append("\\end{document}");
		tex.append(NEW);

		return tex.toString();
	} // end create()

	/**
	 * A class for the BestandsDaten of the OSTS
	 * 
	 * @author Christian Skubich
	 */
	public class Settings {

		// PDF settings
		/** How many mm each column of the pdf should be */
		private int columnWidth = 33;
		/** The number of rows for the pdf file */
		private int rowCount = 10;
		/** The size of the empty cells */
		private int cellSize = 3;
		/** A factor to correct the centering of the table on the sheet */
		private int downFactor = 3;

		// other settings
		/** The maximum width of an image of the generated html pages */
		private int maxImageWidth = 450;

		/**
		 * Constructor which fills the equipment map.
		 */
		public Settings() {
		}

		// Getter and Setter

		/**
		 * Setter for columnWidth
		 * 
		 * @param columnWidth
		 *            The columnWidth
		 * 
		 * @throws IllegalArgumentException
		 *             if the argument is negative
		 */
		public void setColumnWidth(int columnWidth) {
			if (columnWidth < 0)
				throw new IllegalArgumentException("No negative values allowed");
			this.columnWidth = columnWidth;
		}

		/**
		 * Getter for columnWidth
		 * 
		 * @return The columnWidth
		 */
		public int getColumnWidth() {
			return columnWidth;
		}

		/**
		 * Setter for rowCount
		 * 
		 * @param rowCount
		 *            The rowCount
		 * 
		 * @throws IllegalArgumentException
		 *             if the argument is negative
		 */
		public void setRowCount(int rowCount) {
			if (rowCount < 0)
				throw new IllegalArgumentException("No negative values allowed");
			this.rowCount = rowCount;
		}

		/**
		 * Getter for rowCount
		 * 
		 * @return The rowCount
		 */
		public int getRowCount() {
			return rowCount;
		}

		/**
		 * Setter for downFactor
		 * 
		 * @param downFactor
		 *            The downFactor
		 */
		public void setDownFactor(int downFactor) {
			this.downFactor = downFactor;
		}

		/**
		 * Getter for downFactor
		 * 
		 * @return The downFactor
		 */
		public int getDownFactor() {
			return this.downFactor;
		}

		/**
		 * Setter for cellSize
		 * 
		 * @param cellSize
		 *            The cellSize
		 */
		public void setCellSize(int cellSize) {
			this.cellSize = cellSize;
		}

		/**
		 * Getter for cellSize
		 * 
		 * @return The cellSize
		 */
		public int getCellSize() {
			return this.cellSize;
		}

		/**
		 * Setter for maxImageWidth
		 * 
		 * @param maxImageWidth
		 *            the maxImageWidth to set
		 */
		public void setMaxImageWidth(int maxImageWidth) {
			this.maxImageWidth = maxImageWidth;
		}

		/**
		 * Getter for maxImageWidth
		 * 
		 * @return the maxImageWidth
		 */
		public int getMaxImageWidth() {
			return maxImageWidth;
		}

	}
}