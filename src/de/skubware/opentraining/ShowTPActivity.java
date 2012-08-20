package de.skubware.opentraining;
import java.io.File;

import de.skubware.opentraining.datamanagement.DataManager;
import de.skubware.opentraining.exporter.HTMLExporter;
import de.skubware.opentraining.exporter.WorkoutExporter;

import de.skubware.training_app.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import android.webkit.WebView;


public class ShowTPActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.show_tp);
		WebView webview = (WebView) findViewById(R.id.tpWebView);
		
		webview.getSettings().setBuiltInZoomControls(true);
		
		//TODO instead of 5 enter real columncount
		final WorkoutExporter exporter = new HTMLExporter( 5, this, webview, DataManager.INSTANCE.getCurrentWorkout());

		Button button_export = (Button) findViewById(R.id.button_export);
		button_export.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {

				//TODO: test this and fix bugs
				File f = exporter.exportWorkoutToFile(DataManager.INSTANCE.getCurrentWorkout());
				
				Toast.makeText(ShowTPActivity.this, "Workout erstellt: " + f.toString(), Toast.LENGTH_LONG).show();			
				
				
				/*Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND); 
				emailIntent.setType("image/jpeg");
				
				emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"christian.skubich@googlemail.com"}); 
			    emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,   "Test Subject"); 
			    emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,  "go on read the emails"); 
			    emailIntent.putExtra(Intent.EXTRA_STREAM, f);//Uri.parse(f.toString()));//"file://"+ sPhotoFileName));

			    startActivity(Intent.createChooser(emailIntent, "Send mail..."));
			    */
			}

		});
		

	}	
	


}
