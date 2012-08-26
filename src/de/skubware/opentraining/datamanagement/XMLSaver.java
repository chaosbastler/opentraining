package de.skubware.opentraining.datamanagement;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;

import javax.xml.parsers.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;

import de.skubware.opentraining.basic.*;
import de.skubware.opentraining.basic.FSet.Category;
//TODO write UNITTests

/**
 * A class to save plans and exercises as XML files.
 * 
 * @author Christian Skubich
 * 
 */
public class XMLSaver {

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
			destination = new File(destination.toString() + "/plan.xml");
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

			for (FitnessExercise fEx : w.getFitnessExercises()) {
				// create element for FitnessExercise
				Element fE = doc.createElement("FitnessExercise");

				// create element for ExerciseType
				Element exTypeE = doc.createElement("ExerciseType");
				exTypeE.setAttribute("name", fEx.getExType().getName());
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

				wE.appendChild(fE);
			}

			// append root element
			doc.appendChild(wE);

			// save to file
			TransformerFactory tf = TransformerFactory.newInstance();

			//tf.setAttribute("indent-number", 3);
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
	 *            The destination folder. The file name will be '$exercisename.xml'.
	 * 
	 * @return true, if writing was successful, false otherwise
	 */
	public static boolean writeExerciseType(ExerciseType ex, File destination) {
		boolean success = true;
		// write the tp to an xml file with DOM
		DocumentBuilder docBuilder;

		try {
			docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc;

			doc = docBuilder.newDocument();

			// create root element
			Element exE = doc.createElement("ExerciseType");
			exE.setAttribute("name", ex.getName()); 
			
			// add root element
			doc.appendChild(exE);

			Element desE = doc.createElement("Description");
			desE.setAttribute("text", ex.getDescription());
			exE.appendChild(desE);

			for (SportsEquipment eq : ex.getRequiredEquipment()) {
				Element wE = doc.createElement("SportsEquipment");
				wE.setAttribute("name", eq.getName());
				exE.appendChild(wE);
			}

			for (Muscle m : ex.getActivatedMuscles()) {
				Element mE = doc.createElement("Muscle");
				mE.setAttribute("name", m.getName());
				mE.setAttribute("level", Integer.toString(ex.getActivationMap().get(m).getLevel()));
				exE.appendChild(mE);
			}

			for (ExerciseTag t : ex.getTags()) {
				Element tagE = doc.createElement("Tag");
				tagE.setAttribute("name", t.getName());
				tagE.setAttribute("description", t.getDescription());
				exE.appendChild(tagE);
			}
			for (URL url : ex.getURLs()) {
				Element urlE = doc.createElement("URL");
				urlE.setAttribute("url", url.toString());
				exE.appendChild(urlE);
			}

			for(File im:ex.getImagePaths()){
				Element imgE = doc.createElement("Image");
				imgE.setAttribute("path", im.toString());
				imgE.setAttribute("imageLicenseText", ex.getImageLicenseMap().get(im));
				exE.appendChild(imgE);
			}

			// save
			TransformerFactory 	tf = TransformerFactory.newInstance();

			tf.setAttribute("indent-number", 3);
			Transformer t = tf.newTransformer();

			// set parameters
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			// t.setOutputProperty(OutputKeys.METHOD, "xml");
			// t.setOutputProperty(OutputKeys.MEDIA_TYPE, "text/xml");
			// encoding t.setOutputProperty(OutputKeys.ENCODING, "utf8");

			FileWriter fw = new FileWriter(destination.toString() + "/" + ex.getName() + ".xml");

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
}
