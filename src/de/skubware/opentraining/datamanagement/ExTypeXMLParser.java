/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012 Christian Skubich
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

package de.skubware.opentraining.datamanagement;


import javax.xml.parsers.*;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import de.skubware.opentraining.basic.*;

import java.io.*;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * An implementation of a SaxParser for parsing .xml files to 
 * a ExerciseType object
 * 
 * @author Christian Skubich
 */
public class ExTypeXMLParser extends DefaultHandler {

	private SAXParser parser = null;
	private File sourceFile = null;
	
	private ExerciseType exType;
	private String name;																			// required
	
	private String description;																		// optional
	private List<File> imagePaths = new ArrayList<File>();																			// optional
	private Map<File,String> imageLicenseMap = new HashMap<File,String>();																// optional
	private SortedSet<SportsEquipment> requiredEquipment = new TreeSet<SportsEquipment>();			// optional	
	private SortedSet<Muscle> activatedMuscles = new TreeSet<Muscle>();								// optional
	private Map<Muscle, ActivationLevel> activationMap = new HashMap<Muscle, ActivationLevel>();	// optional
	private SortedSet<ExerciseTag> exerciseTag = new TreeSet<ExerciseTag>();						// optional
	private List<URL> relatedURL = new ArrayList<URL>();											// optional
    private List<String> hints = new ArrayList<String>();										// optional
    private File iconPath = null;																	// optional

	
  
  public ExTypeXMLParser() {
     // Parser instanziieren
     try {
        SAXParserFactory fac = SAXParserFactory.newInstance();
        parser = fac.newSAXParser(); 
     } catch(Exception e) {
        e.printStackTrace(); 
     }
        
  }
  
  /** 
   * Parsen einer XML-Datei
   * @param              Einzulesende Datei
   */
  public ExerciseType read(File f) {
     try {
        // Dokument parsen
        parser.parse(f, this);
        
        return this.exType;
     } catch(SAXException e) {
        e.printStackTrace(); 
     } catch(Exception e) {
        e.printStackTrace(); 
     }
     
     return null;
  }
  
  /** 
   * Parsen einer XML-Datei
   * @param              Einzulesende Datei
   */
  public ExerciseType read(InputStream stream){
	  try {
	        // Dokument parsen
	        parser.parse(stream, this);

	        return this.exType;
	     } catch(SAXException e) {
	        e.printStackTrace(); 
	     } catch(Exception e) {
	        e.printStackTrace(); 
	     }
	     
	     return null;
  }


  /**
   * Wird aufgerufen, wenn ein Element beginnt
   * @param uri          Namensraum-Pr�fix
   * @param name         Name des Elements
   * @param qname        Voll qualifizierter Name mit uri und name
   * @param attributes   Attribute
   * @throws SAXException
   */
  public void startElement(String uri, String name, String qname, Attributes attributes) throws SAXException {
	  // Ausgeben des Elementnamens
	  if(qname.equals("ExerciseType")){ 
		  this.name = attributes.getValue("name");
	  }
	  if(qname.equals("SportsEquipment")){
		  SportsEquipment eq = SportsEquipment.getByName(attributes.getValue("name"));
		  if(eq==null){
			  throw new NullPointerException("The SportsEquipment couldn't be found.");
		  }
		  this.requiredEquipment.add(eq);
	  }
	  if(qname.equals("Muscle")){
		  Muscle muscle = Muscle.getByName(attributes.getValue("name"));
		  if(muscle==null){
			  throw new NullPointerException("The muscle couldn't be found.");
		  }
		  
		  this.activatedMuscles.add(muscle);
		  
		  int level = ActivationLevel.MEDIUM.getLevel();
		  try{
			  level = Integer.parseInt( attributes.getValue("level"));
		  }catch(Throwable  t){
			  t.printStackTrace();
		  }		  
		  ActivationLevel actLevel = ActivationLevel.getByLevel(level);
		  this.activationMap.put(muscle, actLevel);
	  }
	  if(qname.equals("Description")){		  
		  this.description = attributes.getValue("text");

	  }
	  if(qname.equals("Image")){
		  File im = new File(attributes.getValue("path"));
		  this.imagePaths.add( im );
		  this.imageLicenseMap.put(im, attributes.getValue("imageLicenseText") );
	  }
	  if(qname.equals("RelatedURL")){
		  try {
			this.relatedURL.add( new URL(attributes.getValue("url")) );
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	  }
	  if(qname.equals("Tag")){
		  ExerciseTag tag = ExerciseTag.getTagByValue(attributes.getValue("name"));
		  if(tag==null){
			  throw new NullPointerException("The Tag couldn't be found: " + attributes.getValue("name"));
		  }
		  this.exerciseTag.add(tag);
	  }
	  if(qname.equals("Hint")){
		  String hint = attributes.getValue("text");

		  this.hints.add(hint);
	  }
	  if(qname.equals("Icon")){
		  File iconpath = new File(attributes.getValue("path"));

		  this.iconPath = iconpath;
	  }
	  
  }

  /**
   * Wird aufgerufen, wenn ein Text-Element behandelt wird
   * 
   * @param chars   Komplettes Dokument
   * @param start   Beginn des Textes
   * @param end     Ende des Textes
   * @throws SAXException
   */
  public void characters(char[] chars, int start, int end)
                                                  throws SAXException {
     // Text in String casten und führende bzw. folgende
     // Leerzeichen entfernen
     //String text = new String(chars, start, end).trim();
     //System.out.println("Found text: " + text);
  }
  
  /**
   * Calculates and returns the MD5 hash of the .xml file of the exercise.
   * 
   * @return The MD5 hash of the .xml file of the exercise
   */
  //TODO fix this
  @SuppressWarnings("unused")
private String calculateMd5(){
	  MessageDigest digest = null;
	  InputStream is = null;
	  try{
		  digest = MessageDigest.getInstance("MD5");
		  is = new FileInputStream(this.sourceFile);				
		  byte[] buffer = new byte[8192];
		  int read = 0;
			
		  while( (read = is.read(buffer)) > 0) {
			  digest.update(buffer, 0, read);
		  }		
			
		  byte[] md5sum = digest.digest();
		  BigInteger hash = new BigInteger(1, md5sum);
		  
		  return hash.toString(16);
	  }catch(NoSuchAlgorithmException e1){
		  e1.printStackTrace();
	  }catch(IOException ioE) {
		  throw new IllegalStateException("Error during MD5-Hash calculation", ioE);
	  }finally{
		  try{ 
			  is.close();
		  }catch(IOException ioE){
			  throw new IllegalStateException("Unable to close input stream for MD5 calculation", ioE);
		  }
	  }
	  
	  // this shouldn't happen
	  throw new IllegalStateException("Error during MD5-Hash calculation");
  }  	
  
  
  
  @Override
  public void endElement(String uri, String localName, String qName){
	  if(qName.equals("ExerciseType")){
		  //TODO Fix md5
		  this.exType = new ExerciseType.Builder(this.name).activatedMuscles(this.activatedMuscles).activationMap(this.activationMap).description(this.description).exerciseTags(this.exerciseTag).imagePath(this.imagePaths).neededTools(this.requiredEquipment).relatedURL(this.relatedURL).imageLicenseText(this.imageLicenseMap).hints(hints).iconPath(iconPath)/*.md5(this.calculateMd5())*/.build();

		  
		  this.name = null;																			// required
			
		  this.description = null;																		// optional
		  this.imagePaths = new ArrayList<File>();														// optional
		  this.imageLicenseMap = null;
		  this.requiredEquipment = new TreeSet<SportsEquipment>();										// optional	
		  this.activatedMuscles = new TreeSet<Muscle>();												// optional
		  this.activationMap = new HashMap<Muscle, ActivationLevel>();									// optional
		  this.exerciseTag = new TreeSet<ExerciseTag>();												// optional
		  this.relatedURL = new ArrayList<URL>();														// optional
		  this.hints = new ArrayList<String>();															// optional
		  this.iconPath = null;																			// optional
			
	  }
	  
  }

}