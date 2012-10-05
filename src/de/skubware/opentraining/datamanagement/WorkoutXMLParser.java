package de.skubware.opentraining.datamanagement;

import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import android.util.Log;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.basic.FSet.Category;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * An implementation of a SaxParser for parsing .xml files to a Workout object
 * 
 * @author Christian Skubich
 */

public class WorkoutXMLParser extends DefaultHandler {
	/** Tag for logging */
	static final String TAG = "WorkoutXMLParser";

	private SAXParser parser = null;

	private Workout w;
	private String wName;
	private List<FitnessExercise> fList = new ArrayList<FitnessExercise>();;
	private ExerciseType exType;
	private List<FSet> fSets = new ArrayList<FSet>();
	private String customName;
	private List<Category> cat = new ArrayList<Category>();;
	private String catName;
	private Integer catValue;
	private Integer rowCount;

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
	public Workout read(File f) {
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
	public void startElement(String uri, String name, String qname, Attributes attributes) throws SAXException {
		// Ausgeben des Elementnamens
		if (qname.equals("Workout")) {
			this.wName = attributes.getValue("name");
			String r = attributes.getValue("rows");
			if (r != null){
				this.rowCount = Integer.parseInt(r);
			}
		}
		if (qname.equals("FitnessExercise")) {
			this.customName = attributes.getValue("customname");
		}
		if (qname.equals("ExerciseType")) {
			String exName = attributes.getValue("name");
			this.exType = ExerciseType.getByName(exName);
			if (exType == null) {
				throw new NullPointerException("The exercise '" + exName + "' of the TrainingPlan couldn't be found in the database.");
			}
		}
		if (qname.equals("Category")) {
			this.catName = attributes.getValue("name");
			this.catValue = Integer.parseInt(attributes.getValue("value"));
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
				Log.i(TAG, "No rows were set");
			}
			this.rowCount = null;
			this.wName = null;
		}
		if (qName.equals("FitnessExercise")) {
			FitnessExercise fEx = new FitnessExercise(this.exType, this.fSets.toArray(new FSet[0]));
			if(this.customName != null){
				fEx.setCustomName(customName);
			}
			this.fList.add(fEx);
			
			this.customName = null;
			this.exType = null;
			this.fSets = new ArrayList<FSet>();
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
	}

}
