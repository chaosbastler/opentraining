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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import android.util.Log;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.basic.FSet.Category;


/**
 * A class to save plans and exercises as XML files.
 * 
 * @author Christian Skubich
 * 
 */
public class XMLSaver {
	/** Tag for logging */
	public static final String TAG = "XMLSaver";
	
	
	/**
	 * Saves a Workout to the given destination.
	 * 
	 * @param w
	 *            The workout to write
	 * @param destination
	 *            The destination file. If destination is a folder, the file
	 *            name will be 'plan.xml'.
	 * 
	 * @return true, if writing was successful, false otherwise
	 */
	public static boolean writeTrainingPlan(Workout w, File destination) {
		// check arguments
		if (destination.isDirectory()) {
			String filename = w.getName();
			if(filename == null || filename.equals("")){
				filename = "plan";
				Log.w(TAG, "Warning: Trying to save Workout, but did not find a name. Workout: " + w.toDebugString());
			}
			
			destination = new File(destination.toString() + "/" + filename + ".xml");
		}

		boolean success = true;
		// write the Workout to an .xml file with DOM
		DocumentBuilder docBuilder;

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			// create root element
			Element wE = doc.createElement("Workout");
			wE.setAttribute("name", w.getName());
			wE.setAttribute("rows", Integer.toString(w.getEmptyRows()));

			for (FitnessExercise fEx : w.getFitnessExercises()) {
				// create element for FitnessExercise
				Element fE = doc.createElement("FitnessExercise");
				fE.setAttribute("customname", fEx.toString());

				// create element for ExerciseType
				Element exTypeE = doc.createElement("ExerciseType");
				exTypeE.setAttribute("name", fEx.getExType().getUnlocalizedName());
				// append ExerciseType
				fE.appendChild(exTypeE);

				for (FSet set : fEx.getFSetList()) {
					Element fSetE = doc.createElement("FSet");

					for (Category c : set.getCategories()) {
						Element catE = doc.createElement("Category");
						catE.setAttribute("name", c.getName());
						catE.setAttribute("value", Integer.toString(c.getValue()));
						fSetE.appendChild(catE);
					}

					// append FitnessExercise
					fE.appendChild(fSetE);
				}

				for (TrainingEntry entry : fEx.getTrainingEntryList()) {
					Element entryE = doc.createElement("TrainingEntry");
					
					// save date
					if(entry.getDate()!=null){
						SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
						entryE.setAttribute("date", format.format(entry.getDate()));
					}else{
						entryE.setAttribute("date", "null");
					}


					for (TrainingSubEntry subEntry : entry.getSubEntryList()) {
						Element subEntryE = doc.createElement("TrainingSubEntry");
						subEntryE.setAttribute("content", subEntry.getContent());
						entryE.appendChild(subEntryE);
					}

					// append TrainingEntry
					fE.appendChild(entryE);
				}
				
				wE.appendChild(fE);
			}

			// append root element
			doc.appendChild(wE);

			// save to file
			TransformerFactory tf = TransformerFactory.newInstance();

			// tf.setAttribute("indent-number", 3);
			Transformer t = tf.newTransformer();

			// set parameters
			// indent('einrücken')
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.setOutputProperty(OutputKeys.METHOD, "xml");
			t.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			t.setOutputProperty(OutputKeys.ENCODING, "utf8");

			FileWriter fw = new FileWriter(destination);
			t.transform(new DOMSource(doc), new StreamResult(fw));

		} catch (ParserConfigurationException e1) {
			success = false;
			e1.printStackTrace();
		} catch (TransformerConfigurationException e) {
			success = false;
			e.printStackTrace();
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		} catch (TransformerException e) {
			success = false;
			e.printStackTrace();
		}

		return success;
	}

	/**
	 * Saves an ExerciseType to the given destination.
	 * 
	 * @param ex
	 *            The ExerciseType to write
	 * @param destination
	 *            The destination folder. The file name will be
	 *            '$exercisename.xml'.
	 * 
	 * @return true, if writing was successful, false otherwise
	 */
	// TODO before adding again: add support to save translations for different
	// languages
	/*
	 * public static boolean writeExerciseType(ExerciseType ex, File
	 * destination) {
	 * 
	 * boolean success = true; // write the tp to an xml file with DOM
	 * DocumentBuilder docBuilder;
	 * 
	 * try { docBuilder =
	 * DocumentBuilderFactory.newInstance().newDocumentBuilder(); Document doc;
	 * 
	 * doc = docBuilder.newDocument();
	 * 
	 * // create root element Element exE = doc.createElement("ExerciseType");
	 * exE.setAttribute("name", ex.getUnlocalizedName());
	 * 
	 * // add root element doc.appendChild(exE);
	 * 
	 * Element desE = doc.createElement("Description");
	 * desE.setAttribute("text", ex.getDescription()); exE.appendChild(desE);
	 * 
	 * for (SportsEquipment eq : ex.getRequiredEquipment()) { Element wE =
	 * doc.createElement("SportsEquipment"); wE.setAttribute("name",
	 * eq.toString()); exE.appendChild(wE); }
	 * 
	 * for (Muscle m : ex.getActivatedMuscles()) { Element mE =
	 * doc.createElement("Muscle"); mE.setAttribute("name", m.toString());
	 * mE.setAttribute("level",
	 * Integer.toString(ex.getActivationMap().get(m).getLevel()));
	 * exE.appendChild(mE); }
	 * 
	 * for (ExerciseTag t : ex.getTags()) { Element tagE =
	 * doc.createElement("Tag"); tagE.setAttribute("name", t.getName());
	 * tagE.setAttribute("description", t.getDescription());
	 * exE.appendChild(tagE); } for (URL url : ex.getURLs()) { Element urlE =
	 * doc.createElement("URL"); urlE.setAttribute("url", url.toString());
	 * exE.appendChild(urlE); }
	 * 
	 * for(File im:ex.getImagePaths()){ Element imgE =
	 * doc.createElement("Image"); imgE.setAttribute("path", im.toString());
	 * imgE.setAttribute("imageLicenseText", ex.getImageLicenseMap().get(im));
	 * exE.appendChild(imgE); }
	 * 
	 * // save TransformerFactory tf = TransformerFactory.newInstance();
	 * 
	 * //tf.setAttribute("indent-number", 3); Transformer t =
	 * tf.newTransformer();
	 * 
	 * // set parameters t.setOutputProperty(OutputKeys.INDENT, "yes"); //
	 * t.setOutputProperty(OutputKeys.METHOD, "xml"); //
	 * t.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml"); // encoding
	 * t.setOutputProperty(OutputKeys.ENCODING, "utf8");
	 * 
	 * FileWriter fw = new FileWriter(destination.toString() + "/" +
	 * ex.getUnlocalizedName() + ".xml");
	 * 
	 * t.transform(new DOMSource(doc), new StreamResult(fw));
	 * 
	 * } catch (ParserConfigurationException e1) { success = false;
	 * e1.printStackTrace(); } catch (TransformerConfigurationException e) {
	 * success = false; e.printStackTrace(); } catch (IOException e) { success =
	 * false; e.printStackTrace(); } catch (TransformerException e) { success =
	 * false; e.printStackTrace(); } return success; }
	 */
}
