package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.Task;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.TaskService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDetailActivity extends AppCompatActivity {

    TaskService taskService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // get task id sent by TaskListActivity, -1 if not found
        Intent intent = getIntent();
        int id = intent.getIntExtra("task_id", -1);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get book service instance
        taskService = ApiUtils.getTaskService();

        // execute the API query. send the token and book id
        taskService.getTask(user.getToken(), id).enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {
                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // get book object from response
                Task task = response.body();

                // get references to the view elements
                TextView tvTitle = findViewById(R.id.tvTitle);
                TextView tvDesc = findViewById(R.id.tvDesc);
                TextView tvDueDate = findViewById(R.id.tvDueDate);
                TextView tvStatus = findViewById(R.id.tvStatus);

                // set values
                tvTitle.setText(task.getTitle());
                tvDesc.setText(task.getDescription());
                tvDueDate.setText(task.getDuedate());
                tvStatus.setText(task.getStatus());
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });

    }
}