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

import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import de.skubware.opentraining.basic.ExerciseType;
import de.skubware.opentraining.db.rest.ServerModel;

public interface WgerRestService {		
	  @POST("/exercise/")
	  public Response createExercise(@Body ExerciseType exercise);
	  
	  @POST("/exerciseimage/")
	  public Response createExerciseImage(@Body ExerciseType exercise);
	  
	  @GET("/equipment/")
	  public ServerModel.Equipment[] getEquipment();
	  
	  @GET("/exercisecategory/")
	  public ServerModel.MuscleCategory[] getMuscles();
	  
	  @GET("/language/")
	  public ServerModel.Language[] getLanguages();
}