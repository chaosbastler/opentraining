/**
 * 
 * This is OpenTraining, an Android application for planning your your fitness training.
 * Copyright (C) 2012-2014 Christian Skubich
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

package de.skubware.opentraining.activity.acra;


import org.acra.*;
import org.acra.annotation.*;

import android.app.Application;
import de.skubware.opentraining.BuildConfig;
import de.skubware.opentraining.R;

@ReportsCrashes(
    formKey = "", // This is required for backward compatibility but not used
    mode = ReportingInteractionMode.DIALOG,
    resDialogText = R.string.crash_dialog_text,
    resDialogCommentPrompt = R.string.crash_dialog_comment_prompt, // optional. when defined, adds a user text field input with this text resource as a label
    resDialogOkToast = R.string.crash_dialog_ok_toast // optional. displays a Toast message when the user accepts to send a report.

 )


public class OpenTrainingApplication  extends Application{
	 
	@Override
     public void onCreate() {
         super.onCreate();

         // The following line triggers the initialization of ACRA
         if (!BuildConfig.DEBUG){
        	 ACRA.init(this);
        	 ACRA.getErrorReporter().setReportSender(new ACRACrashReportMailer()); // default crash report sender
         }
	}

	
}
