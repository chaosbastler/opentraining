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
	  
	  @GET("/equipment/")
	  public ServerModel.Equipment[] getEquipment();
	  
	  @GET("/exercisecategory/")
	  public ServerModel.MuscleCategory[] getMuscles();
	  
	  @GET("/language/")
	  public ServerModel.Language[] getLanguages();
}