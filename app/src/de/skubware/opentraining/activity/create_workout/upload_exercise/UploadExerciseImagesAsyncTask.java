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

package de.skubware.opentraining.activity.create_workout.upload_exercise;

import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.db.rest.ExerciseImageGSONSerializer;

public class UploadExerciseImagesAsyncTask extends AsyncTask<ExerciseType, Void, Throwable> {
	private final Context mContext;
	private final ProgressDialog mDialog;

	/**
	 * @param exerciseTypeDetailFragment
	 */
	public UploadExerciseImagesAsyncTask(Context context) {
		mContext = context;
		mDialog = new ProgressDialog(mContext);
	}

	protected void onPreExecute() {
		this.mDialog.setMessage("Uploading images ...");
		this.mDialog.show();
	}

	/**
	 * @return Null if everything went fine, the original exception otherwise.
	 */
	@Override
	protected Throwable doInBackground(ExerciseType... exercise) {



		RestAdapter restAdapter = UploadExerciseAsyncTask.getRestAdapter(mContext, new ExerciseImageGSONSerializer());

		WgerRestService service = restAdapter.create(WgerRestService.class);



		try {
			service.createExerciseImage(exercise[0]);
		} catch (RetrofitError retEr) {
			if (retEr.getCause() != null)
				return retEr.getCause();
			else
				return retEr;
		}

		return null;
	}

	@Override
	protected void onPostExecute(Throwable ex) {
		mDialog.dismiss();

		AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
		String msg;
		String title;
		if (ex == null) {
			// everything went fine
			title = "Upload successfull";
			msg = "Upload finished";
		} else {
			title = "Upload failed";
			if (ex instanceof RetrofitError) {
				// show server response to user
				Response response = ((RetrofitError) ex).getResponse();
				msg = response.getReason() + ": " + UploadExerciseAsyncTask.getBodyString(response);
			} else {
				// show custom error message if problem is known
				msg = ex.getMessage();
			}
		}
		alertDialog.setMessage(msg);
		alertDialog.setTitle(title);
		alertDialog.create().show();
	}

	@Override
	protected void onProgressUpdate(Void... values) {
	}

	
}