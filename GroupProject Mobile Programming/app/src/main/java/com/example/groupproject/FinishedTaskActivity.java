package com.example.groupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.adapter.TaskAdapter;
import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.Task;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.TaskService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FinishedTaskActivity extends AppCompatActivity {

    TaskService taskService;
    Context context;
    RecyclerView fTaskList;
    TaskAdapter adapter;
    Task task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_finished_task);

        context = this; // get current activity context

        // get reference to the RecyclerView taskList
        fTaskList = findViewById(R.id.finishedTaskList);

        //register for context menu
        registerForContextMenu(fTaskList);

        // update listview
        finishedListView();


    }

    /**
     * Fetch data for ListView
     */
    private void finishedListView() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        //String stat = task.getStatus();

        //if(stat=="Finished")
        //{
            // execute the call. send the user token when sending the query
            taskService.getFinishedTask(user.getToken()).enqueue(new Callback<List<Task>>() {
                @Override
                public void onResponse(Call<List<Task>> call, Response<List<Task>> response) {
                    // for debug purpose
                    Log.d("MyApp:", "Response: " + response.raw().toString());

                    // token is not valid/expired
                    if (response.code() == 401) {
                        displayAlert("Session Invalid");
                    }

                    // Get list of book object from response
                    List<Task> tasks = response.body();

                    // initialize adapter
                    adapter = new TaskAdapter(context, tasks);

                    // set adapter to the RecyclerView
                    fTaskList.setAdapter(adapter);

                    // set layout to recycler view
                    fTaskList.setLayoutManager(new LinearLayoutManager(context));

                    // add separator between item in the list
                    DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(fTaskList.getContext(),
                            DividerItemDecoration.VERTICAL);
                    fTaskList.addItemDecoration(dividerItemDecoration);
                }

                @Override
                public void onFailure(Call<List<Task>> call, Throwable t) {
                    Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                    displayAlert("Error [" + t.getMessage() + "]");
                    Log.e("MyApp:", t.getMessage());
                }
            });
        }

        //}

        /**
         * Displaying an alert dialog with a single button
         * @param message - message to be displayed
         */
        public void displayAlert(String message){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }

}