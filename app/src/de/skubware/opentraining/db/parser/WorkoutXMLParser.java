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


package de.skubware.opentraining.db.parser;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import android.content.Context;
import android.util.Log;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.basic.FSet.Category;
import de.skubware.opentraining.db.DataProvider;
import de.skubware.opentraining.db.IDataProvider;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * An implementation of a SaxParser for parsing .xml files to a {@link Workout} object.
 * 
 * @author Christian Skubich
 */
public class WorkoutXMLParser extends DefaultHandler {
	/** Tag for logging */
	static final String TAG = "WorkoutXMLParser";
	
	private Context mContext;

	private SAXParser parser = null;

	private Workout w;

	// Workout stuff
	/** Name of the {@link Workout} */
	private String wName;

	/** Integer for the number of rows of the workout. */
	private Integer rowCount;

	// FitnessExercise stuff
	/** List to store the {@link FitnessExercise}s */
	private List<FitnessExercise> fList = new ArrayList<FitnessExercise>();

	/** The last {@link ExerciseType} that was parsed */
	private ExerciseType exType;

	/** The custom name of the {@link FitnessExercise} */
	private String customName;

	// TrainingEntry stuff
	/** A Map for the TrainingEntrys of a FitnessExercise. */
	private List<TrainingEntry> trainingEntryList = new ArrayList<TrainingEntry>();

	/** Date for the last parsed {@link TrainingEntry} */
	private TrainingEntry trainingEntry;

	/** List for the {@link TrainingSubEntry}s */
	//private List<String> trainingSubEntryContentList = new ArrayList<String>();

	/** Content of the last parsed {@link TrainingSubEntry} */
	//private String trainingSubEntryContent;

	// FSet stuff
	/** List for the {@link FSet}s */
	private List<FSet> fSets = new ArrayList<FSet>();

	/** List of the {@link Category}s */
	private List<Category> cat = new ArrayList<Category>();

	/** Name of the last parsed {@link Category} */
	private String catName;

	/** Value of the last parsed {@link Category} */
	private Integer catValue;

	public WorkoutXMLParser() {
		// Parser instanziieren
		try {
			SAXParserFactory fac = SAXParserFactory.newInstance();
			parser = fac.newSAXParser();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Parsen einer XML-Datei
	 * 
	 * @param Einzulesende
	 *            Datei
	 */
	public Workout read(File f, Context context) {
		mContext = context;
		
		try {
			// Dokument parsen
			parser.parse(f, this);

			return this.w;
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Wird aufgerufen, wenn ein Element beginnt
	 * 
	 * @param uri
	 *            Namensraum-Pr�fix
	 * @param name
	 *            Name des Elements
	 * @param qname
	 *            Voll qualifizierter Name mit uri und name
	 * @param attributes
	 *            Attribute
	 * @throws SAXException
	 */
	@SuppressWarnings("deprecation") // because using constructor of TrainingEntry
	public void startElement(String uri, String name, String qname, Attributes attributes) throws SAXException {
		// Ausgeben des Elementnamens
		if (qname.equals("Workout")) {
			this.wName = attributes.getValue("name");
			String r = attributes.getValue("rows");
			if (r != null) {
				this.rowCount = Integer.parseInt(r);
			}
		}
		if (qname.equals("FitnessExercise")) {
			this.customName = attributes.getValue("customname");
		}
		if (qname.equals("ExerciseType")) {
			String exName = attributes.getValue("name");
			IDataProvider dataProvider = new DataProvider(mContext);
			this.exType = dataProvider.getExerciseByName(exName);
			if (exType == null) {
				throw new NullPointerException("The exercise '" + exName + "' of the TrainingPlan couldn't be found in the database.");
			}
		}
		if (qname.equals("Category")) {
			this.catName = attributes.getValue("name");
			this.catValue = Integer.parseInt(attributes.getValue("value"));
		}
		if (qname.equals("TrainingEntry")) {
			String dateString = attributes.getValue("date");
			
			Date trainingEntryDate;
			if(dateString == null || dateString.equals("") || dateString.equals("null")){
				trainingEntryDate = null;
			}else{
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

				try {
					trainingEntryDate = format.parse(dateString);
				} catch (ParseException e) {
					Log.e(TAG, "Error parsing date: " + dateString, e);
					trainingEntryDate = null;
				}
			}
			
			this.trainingEntry = new TrainingEntry(trainingEntryDate);

		}
		if (qname.equals("TrainingSubEntry")) {
			String content = attributes.getValue("content");
			this.trainingEntry.add(content);
		}

	}


	/**
	 * Wird aufgerufen, wenn ein Text-Element behandelt wird
	 * 
	 * @param chars
	 *            Komplettes Dokument
	 * @param start
	 *            Beginn des Textes
	 * @param end
	 *            Ende des Textes
	 * @throws SAXException
	 */
	public void characters(char[] chars, int start, int end) throws SAXException {
		// Text in String casten und f�hrende bzw. folgende
		// Leerzeichen entfernen
		// String text = new String(chars, start, end).trim();
		// System.out.println("Found text: " + text);
	}

	@Override
	public void endElement(String uri, String localName, String qName) {
		if (qName.equals("Workout")) {
			this.w = new Workout(this.wName, this.fList.toArray(new FitnessExercise[0]));
			if (this.rowCount != null) {
				this.w.setEmptyRows(this.rowCount);
			} else {
				Log.d(TAG, "No rows were set");
			}

			
			
			this.rowCount = null;
			this.wName = null;
		}
		if (qName.equals("FitnessExercise")) {
			FitnessExercise fEx = new FitnessExercise(this.exType, this.fSets.toArray(new FSet[0]));
			
			// set custom name
			if(this.customName != null){
				fEx.setCustomName(customName);
				Log.d(TAG, "customName=" + customName);
			}else{
				Log.d(TAG, "No customName");
			}
			
			
			// now add TrainingEntrys
			//List<TrainingEntry> originalList = fEx.getTrainingEntryList();
			//originalList.addAll(this.trainingEntryList);

			
			this.fList.add(fEx);
			
			this.customName = null;
			this.exType = null;
			this.fSets = new ArrayList<FSet>();
			this.trainingEntryList = new ArrayList<TrainingEntry>();

		}
		if (qName.equals("FSet")) {
			this.fSets.add(new FSet(this.cat.toArray(new Category[1])));
			this.cat = new ArrayList<Category>();
		}
		if (qName.equals("Category")) {
			boolean created = false;
			if (this.catName.equals(new Category.Weight(1).getName())) {
				this.cat.add(new Category.Weight(this.catValue));
				created = true;
			}
			if (this.catName.equals(new Category.Repetition(1).getName())) {
				this.cat.add(new Category.Repetition(this.catValue));
				created = true;
			}
			if (this.catName.equals(new Category.Duration(1).getName())) {
				this.cat.add(new Category.Duration(this.catValue));
				created = true;
			}
			if (!created) {
				throw new IllegalStateException();
			}
			catName = null;
			catValue = null;
		}
		if (qName.equals("TrainingEntry")){
			this.trainingEntryList.add(this.trainingEntry);
			this.trainingEntry = null;
		}
		if (qName.equals("TrainingSubEntry")){
			// to nothing
		}
		
	}
}
