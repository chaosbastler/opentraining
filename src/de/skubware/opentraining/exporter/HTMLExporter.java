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

package de.skubware.opentraining.exporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

import android.content.Context;
import android.graphics.*;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.PictureDrawable;
import android.webkit.WebView;


import de.skubware.opentraining.datamanagement.*;
import de.skubware.opentraining.datamanagement.DataManager.Source;
import de.skubware.opentraining.basic.FitnessExercise;
import de.skubware.opentraining.basic.Workout;

public class HTMLExporter extends WorkoutExporter {
	private WebView webview;
	
	public HTMLExporter(int rowCount, Context context, WebView webview, Workout w) {
		super(rowCount, context);
		this.webview = webview;

		this.loadWorkout(w);

	}
	
	private void loadWorkout(Workout w){
		File f = new File( DataManager.getHTMLFolder().toString() + "/trainingplan.html" );
		DataManager.INSTANCE.writeFile(this.exportWorkoutToString(w), "trainingplan.html", context, DataManager.getHTMLFolder());
		// assert, that the style sheets are there
		
		try {
			webview.loadUrl(f.toURL().toString());//.loadData(f.toURL(), "text/html", "utf-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	/**
	 * Exports a workout and returns the generated file.
	 * 
	 * @param w The workout to export
	 * 
	 * @return The File with the exported workout
	 * 
	 * @throws UnsupportedOperationException If not supported
	 */
	public File exportWorkoutToFile(Workout w){
		try{
			DataManager.INSTANCE.writeFile(this.exportWorkoutToString(w), "trainingplan.html", context, DataManager.getHTMLFolder());
			
			// render HTML to Picture
			Picture picture = webview.capturePicture();
			
			
			// create fos
			//File cache = DataManager.INSTANCE.getAppFolder();//context.getCacheDir();
			File folder = DataManager.getAppFolder();
			File f = new File(folder.toString() + "/" + w.getName() + ".jpg");
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(f);
			} catch (FileNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			// write Picture to fos
			PictureDrawable pictureDrawable = new PictureDrawable(picture);
	        
			
			//Bitmap bitmap = Bitmap.createBitmap(pictureDrawable.getIntrinsicWidth(),pictureDrawable.getIntrinsicHeight(), Config.ARGB_8888);
			//Bitmap bitmap = Bitmap.createBitmap(webview.getHeight(),webview.getWidth(), Config.ARGB_8888);
			Bitmap bitmap = Bitmap.createBitmap(1189*2,1682*2, Config.ARGB_8888);

			
			Canvas canvas = new Canvas(bitmap);
	        canvas.drawPicture(pictureDrawable.getPicture());
	        
	        
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos); 
			
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// Write file to cache
			
			
			assert(f!=null);
			
			return f;
		}catch(UnsupportedOperationException unsupported){
			// may happen when String export doesn't work
			throw unsupported;
		}
		
	}
		

	
	@Override
	public String exportWorkoutToString(Workout w){
		String data = "";
	
		try {
			data = DataManager.INSTANCE.loadFile("trainingplan_template", Source.RAW_FOLDER, context);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		data = data.replaceAll("<!--WORKOUT_NAME-->", w.getName());
		
		// Exercises
		StringBuilder exes = new StringBuilder();
		for(FitnessExercise fEx:w.getFitnessExercises()){
			exes.append("\t\t <th>");
			exes.append(fEx.getExType().getName());
			exes.append("</th>\n");
		}
		data = data.replaceAll("<!--EXERCISES-->", exes.toString());
	
		
		//TODO: FSETS
		
		StringBuilder emptyRow = new StringBuilder();
		emptyRow.append("\t<tr>\n");
		// an empty row
		for(int i = 0; i<=w.getFitnessExercises().size(); i++){
			emptyRow.append("\t\t<td></td>\n");
		}
		emptyRow.append("\t</tr>\n");

		StringBuilder emptyCells = new StringBuilder();
		for(int i = 0; i< rowCount; i++){
			emptyCells.append(emptyRow.toString());
		}
		
		data = data.replaceAll("<!--EMPTY_CELLS-->", emptyCells.toString() );
		
		

		data = data.replaceAll("<!--CSS-->", DataManager.INSTANCE.getCSSString(context));
		
		
		
		return data;	
		
	}

}
