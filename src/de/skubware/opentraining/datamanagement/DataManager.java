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

import java.io.*;
import java.util.*;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Environment;

import de.skubware.opentraining.basic.*;

/**
 * This class is implemented as a Singleton, though it isn't the standard implementation of this pattern.
 * 
 * @author Christian Skubich
 *
 */
public enum DataManager {
	INSTANCE;

	private Workout workout;	
	private Map<ExerciseType, String> htmlMap;
	
	private String css = "trainingplan_modern.css";


	
	private DataManager(){
		htmlMap = new HashMap<ExerciseType, String>();
		
		if(!ExerciseType.listExerciseTypes().isEmpty())
			return;		
	}	
	
	
	
	public static File getAppFolder(){
		File appFolder = new File(Environment.getExternalStorageDirectory().toString() + "/OpenTraining");
		appFolder.mkdir();
        if(!appFolder.exists())
        	throw new AssertionError();
		return appFolder;
	}
	
	public static File getImageFolder(){
		File imageFolder = new File(getAppFolder().toString() + "/images");
		imageFolder.mkdir();
        if(!imageFolder.exists())
        	throw new AssertionError();
		return imageFolder;	
	}
	
	public static File getExerciseXMLFolder(){
		File exFolder = new File(getAppFolder().toString() + "/exercises");
		exFolder.mkdir();
        if(!exFolder.exists())
        	throw new AssertionError();
		return exFolder;	
	}
	
	public static File getHTMLFolder(){
		File htmlFolder = new File(getAppFolder().toString() + "/html");
		htmlFolder.mkdir();
        if(!htmlFolder.exists())
        	throw new AssertionError();
		return htmlFolder;
	}
	
	
	/**
	 * Returns a Drawable when an image with such a name is there.
	 * 
	 * @param name The name of the image
	 * @return The generated Drawable
	 */
    public Drawable getDrawable(String name){
    	FileInputStream fis = null;
    	Drawable img = null;
		try {
            File file = new File(DataManager.getImageFolder(),name);
	        fis = new FileInputStream(file);
			img = Drawable.createFromStream(fis, "icon");
			fis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return img;
    }

	

	/**
	 * Builds the HTML or returns a recently generated one.
	 * 
	 * @param ex The ExerciseType
	 * 
	 * @return A String that represents the content of a .html file.
	 */
	public String buildHTML(ExerciseType ex, Context context){
		if(htmlMap.containsKey(ex))
			return htmlMap.get(ex);
		
		
		String data = null;
		try {
			data = loadFile("template", Source.RAW_FOLDER, context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		data = data.replaceAll("EX_NAME", ex.getName());
		data = data.replaceAll("DESCRIPTION", ex.getDescription());
	
		if(!ex.getActivatedMuscles().isEmpty())
			data = data.replaceAll("ACTIVATED_MUSCLES", ex.getActivatedMuscles().first().toString());
		if(!ex.getRequiredEquipment().isEmpty())
			data = data.replaceAll("EQUIPMENT", ex.getRequiredEquipment().first().toString());
		if(!ex.getTags().isEmpty())		
			data = data.replaceAll("TAGS", ex.getTags().first().toString());
		if(!ex.getURLs().isEmpty())
			data = data.replaceAll("LINKS", ex.getURLs().get(0).toString());
				
		this.htmlMap.put(ex, data);		
		return data;
	}
	
	/**
	 * Removes all ExerciseTypes and reads them again.
	 * 
	 * @param context
	 */
	public void loadExercises(Context context){
		
		try{
			// next line is necessary to avaid current modification exception while iterating
			Set<ExerciseType> l = new HashSet<ExerciseType>(ExerciseType.listExerciseTypes());
			for(ExerciseType exType:l){
				ExerciseType.removeExerciseType(exType);
			}
			
		
			String[] files = DataManager.getExerciseXMLFolder().list();
		
			for(String f:files){
				// XML einlesen und neu speichern
				String xmlData = loadFile(DataManager.getExerciseXMLFolder().toString() + "/" + f, Source.FILE_SYSTEM, context);
				String FILENAME = "my_xml";

				FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
				fos.write(xmlData.getBytes());
				fos.close();
		
				ExTypeXMLParser parser = new ExTypeXMLParser();
				parser.read(context.getFileStreamPath("my_xml"));
			}
		}catch(IOException ioEx){
			ioEx.printStackTrace();
		}
	}
	
	
	public enum Source{
		RAW_FOLDER, ASSETS, FILE_SYSTEM;
	}
	
	//load file from apps res/raw folder or Assets folder  
	public String loadFile(String fileName, Source src, Context context) throws IOException{  
		Resources resources = context.getResources();
		
	    //Create a InputStream to read the file into  
	    InputStream iS;  

	    if (src==Source.RAW_FOLDER) {  
	        //get the resource id from the file name  
	        int rID = resources.getIdentifier("de.skubware.training_app:raw/" + fileName, null, null);  
	        //get the file as a stream  
	        iS = resources.openRawResource(rID);  
	    }  else if(src==Source.ASSETS)    {  
	        //get the file as a stream  
	        iS = resources.getAssets().open(fileName);  
	    }  else{ // FILE_SYSTEM
	    	
	    	iS = new FileInputStream(new File(fileName));
	    }

	    //create a buffer that has the same size as the InputStream  
	    byte[] buffer = new byte[iS.available()];  
	    //read the text file as a stream, into the buffer  
	    iS.read(buffer);  
	    //create a output stream to write the buffer into  
	    ByteArrayOutputStream oS = new ByteArrayOutputStream();  
	    //write this buffer to the output stream  
	    oS.write(buffer);  
	    //Close the Input and Output streams  
	    oS.close();  
	    iS.close();  

	    //return the output stream as a String  
	    return oS.toString();  
	}  



	public Workout getCurrentWorkout() {
		return workout;
	}
	

	public File writeFileToCache(String data, String name, Context context) {
		File destination = context.getCacheDir();
		
		File f = new File(destination.toString() + "/" + name);
		
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(f.toString(), Context.MODE_PRIVATE);
			fos.write(data.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			f=null;
			e.printStackTrace();
		} catch (IOException e) {
			f=null;
			e.printStackTrace();
		}

		return f;
	}
	
	public File writeFile(String data, String name, Context context, File destination) {		
		if(!destination.isDirectory())
			throw new IllegalArgumentException("No valid directory");
		
		File f = new File(destination + "/" + name);
        FileOutputStream fileOutput;
		try {
			fileOutput = new FileOutputStream(f.toString());
            fileOutput.write(data.getBytes());
            fileOutput.close();
		} catch (FileNotFoundException e) {
			f=null;
			e.printStackTrace();
		} catch (IOException e) {
			f=null;
			e.printStackTrace();
		}

		return f;
	}



	public void setWorkout(Workout workout) {
		this.workout = workout;
	}


	public void setCSSFile(String css){
		this.css = css;
	}

	public String getCSSFile() {
		return css;
	}


}
