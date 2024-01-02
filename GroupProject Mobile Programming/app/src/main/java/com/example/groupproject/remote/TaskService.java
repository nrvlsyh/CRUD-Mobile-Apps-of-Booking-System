package com.example.groupproject.remote;

import com.example.groupproject.model.DeleteResponse;
import com.example.groupproject.model.Task;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface TaskService {

    @GET("api/task/?order=status&orderType=asc")
    Call<List<Task>> getAllTasks(@Header("api-key") String api_key);
    @GET("api/task/{task_id}")
    Call<Task> getTask(@Header("api-key") String api_key, @Path("task_id") int id);

    @GET("api/task/?status[in]=Finished")
    Call<List<Task>> getFinishedTask(@Header("api-key") String api_key);

    @GET("api/task/?status[in]=In Progress")
    Call<List<Task>> getPendingTask(@Header("api-key") String api_key);

    /**
     * Add Task by sending a single Task JSON
     * @return book object
     */
    @POST("api/task")
    Call<Task> addTask(@Header ("api-key") String apiKey, @Body Task task);

    /**
     * Delete task based on the id
     * @return DeleteResponse object
     */
    @POST("api/task/delete/{task_id}")
    Call<DeleteResponse> deleteTask(@Header ("api-key") String apiKey, @Path("task_id") int id);

    /**
     * Update book by sending a single Book JSON
     * @return book object
     */
    @POST("api/task/update")
    Call<Task> updateTask(@Header ("api-key") String apiKey, @Body Task task);
}
