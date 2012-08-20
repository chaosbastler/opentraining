package de.skubware.opentraining;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.training_app.R;
import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.StrictMode.ThreadPolicy;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class ManageDatabaseActivity extends Activity {	
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_database);        
    
        
        //TODO REMOVE BEFORE RELEASE!!!!!!!
        ThreadPolicy tp = ThreadPolicy.LAX;
        StrictMode.setThreadPolicy(tp);
        
        Button button_download = (Button) findViewById(R.id.button_download);
        button_download.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				boolean succ1 = delete(DataManager.getAppFolder());
		        boolean succ2 = download("http://skubware.de/osts/cc_exercises/list_files.php");
		         	    
		        //TODO:
		        /*
		         * public void onClick(View v) {
  new DownloadImageTask().execute("http://example.com/image.png");
}

private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
     protected Bitmap doInBackground(String... urls) {
         return loadImageFromNetwork(urls[0]);
     }

     protected void onPostExecute(Bitmap result) {
         mImageView.setImageBitmap(result);
     }
 }
		         */
				
				 //download("http://skubware.de/osts/cc_exercises/list_files.php");
				
			}

        });
        
        
    }
    
    private boolean delete(File dir){
    	if (dir.isDirectory()){
    		File[] files = dir.listFiles();
    		for (File aktFile: files){
    			delete(aktFile);
    		}
    	}
    	return dir.delete();
    }
    
    public boolean download(String url_string){
    	
    	
    	URL url;
    	InputStream is = null;
    	DataInputStream dis;
    	String line;
    	List<String> lines = new ArrayList<String>();

    	try {
    	    url = new URL(url_string);
    	    is = url.openStream();  // throws an IOException
    	    dis = new DataInputStream(new BufferedInputStream(is));

    	    while ((line = dis.readLine()) != null) {
    	    	lines.add(line);
    	    }
    	} catch (MalformedURLException mue) {
    	     mue.printStackTrace();
    	} catch (IOException ioe) {
    	     ioe.printStackTrace();
    	} finally {
    	    try {
    	        is.close();
    	    } catch (IOException ioe) {
    	        // nothing to see here
    	    }
    	}
    	
    	
    	boolean succ = true;
    	for(String name:lines){
    		File dest;
    		if(name.endsWith(".xml"))
    			dest = DataManager.getExerciseXMLFolder();
    		else
    			dest = DataManager.getImageFolder();
    		
    	    succ = download(dest, "http://skubware.de/osts/cc_exercises/" + name) & succ;
		    


    	}

    	return succ;

    }

    
    public boolean download(File destination, String url_string){
		
		try {
	        //set the download URL, a url that points to a file on the internet
	        //this is the file to be downloaded
	        URL url = new URL(url_string);
	        
	        //create the new connection
	        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

	        //set up some things on the connection
	        urlConnection.setRequestMethod("GET");
	        urlConnection.setDoOutput(true);

	        //and connect!
	        urlConnection.connect();
            

	        //create a new file, specifying the path, and the filename
	        //which we want to save the file as.
	        String[] split = url_string.split("/");
	        String fileName =split[split.length-1]; 
	        System.out.println(fileName);
	        
	        File file = new File(destination,fileName);

	        //this will be used to write the downloaded data into the file we created
	        FileOutputStream fileOutput = new FileOutputStream(file);

	        //this will be used in reading the data from the internet
	        InputStream inputStream = urlConnection.getInputStream();

	        //this is the total size of the file
	        int totalSize = urlConnection.getContentLength();
	        //variable to store total downloaded bytes
	        int downloadedSize = 0;

	        //create a buffer...
	        byte[] buffer = new byte[1024];
	        int bufferLength = 0; //used to store a temporary size of the buffer

	        //now, read through the input buffer and write the contents to the file
	        while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
	                //add the data in the buffer to the file in the file output stream (the file on the sd card
	                fileOutput.write(buffer, 0, bufferLength);
	                //add up the size so we know how much is downloaded
	                downloadedSize += bufferLength;
	                //this is where you would do something to report the prgress, like this maybe
	                updateProgress(downloadedSize, totalSize);

	        }
	        //close the output stream when done
	        fileOutput.close();
	        showMessage("Downloaded " + url_string + " to:"  +  destination.toString() +"\n");
	        

	        //catch some possible errors...
		} catch (MalformedURLException e) {
	        e.printStackTrace();
	        return false;
		} catch (IOException e) {
	        e.printStackTrace();
	        return false;
		}
		
		return true;
    }
    

	private void showMessage(String msg) {
		EditText edittext_information = (EditText) findViewById(R.id.edittext_information);
        edittext_information.append(msg);		
	}

	private void updateProgress(int downloadedSize, int totalSize) {
		ProgressBar progressbar = (ProgressBar) findViewById(R.id.progressbar);
		progressbar.setProgress(totalSize-downloadedSize);
		
		
		/**
		 * // Start lengthy operation in a background thread
         new Thread(new Runnable() {
             public void run() {
                 while (mProgressStatus < 100) {
                     mProgressStatus = doWork();

                     // Update the progress bar
                     mHandler.post(new Runnable() {
                         public void run() {
                             mProgress.setProgress(mProgressStatus);
                         }
                     });
                 }
             }
         }).start();
		 */
	}
    

    
    
	

}
