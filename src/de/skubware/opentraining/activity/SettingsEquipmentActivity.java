package de.skubware.opentraining.activity;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.SportsEquipment;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.app.Activity;
import android.graphics.drawable.Drawable;

public class SettingsEquipmentActivity extends Activity {
	


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_equipment);

		
		final ImageView view = (ImageView) findViewById(R.id.imageview_equipment);
		
		// add equipment checkboxes
		ViewGroup layout = (ViewGroup) findViewById(R.id.wrapper_equipment);
		for (SportsEquipment e : SportsEquipment.values()) {
			CheckBox b = new CheckBox(this);
			b.setTextSize( 20);
			b.setText(e.toString());
			final Drawable image = e.getImage();
			b.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					if(image==null)
						view.setImageResource(R.drawable.defaultimage);
					else
						view.setImageDrawable(image);
				}
			});
			layout.addView(b);
		}

	}

}
