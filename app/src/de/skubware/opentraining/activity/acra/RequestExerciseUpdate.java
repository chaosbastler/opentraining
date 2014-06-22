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

import org.acra.ACRA;

import android.content.Context;

import de.skubware.opentraining.R;
import de.skubware.opentraining.basic.ExerciseType;

/**
 * 
 * This class adds an easy way to get user feedback.
 * 
 * The user can simply enter a short feedback text and
 * a call of 'ACRA.getErrorReporter().handleException(new RequestExerciseUpdate(...));'
 * will send the report to the developer.
 * 
 */
public class RequestExerciseUpdate extends Exception{
	
	private static final long serialVersionUID = -4382910029697955724L;

	
	public enum ExerciseUpdateReason{
		MISC("Miscellaneous", R.string.misc),
		WRONG_INFORMATION("Wrong information", R.string.wrong_information),
		WRONG_IMAGE("Wrong image",R.string.wrong_image),
		EXERCISE_DUPLICATE("Exercise duplicate", R.string.exercise_duplicate);
		
		private String mLongName;
		private String mTranslatedName = null;

		private int mResID;
		
		ExerciseUpdateReason(String longName, int resID){
			mLongName = longName;
			mResID = resID;
		}
		
		@Override
		public String toString(){
			if(mTranslatedName == null)
				return mLongName;
			else
				return mTranslatedName;
		}
		
		public String getUnlocalizedName(){
			return mLongName;
		}
		
		public static void translateEnums(Context context){
			for(ExerciseUpdateReason reason:ExerciseUpdateReason.values()){
				reason.mTranslatedName = context.getString(reason.mResID);
			}
		}
	}
	
	
	public RequestExerciseUpdate(ExerciseType ex, ExerciseUpdateReason reason, String userMsg){
        ACRA.getErrorReporter().putCustomData("Exercise ", ex.getUnlocalizedName());
        ACRA.getErrorReporter().putCustomData("Reason ", reason.getUnlocalizedName());
        ACRA.getErrorReporter().putCustomData("User message ", userMsg);
	}
	
}
