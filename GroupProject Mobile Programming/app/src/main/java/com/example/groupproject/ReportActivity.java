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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.groupproject.adapter.TaskAdapter;
import com.example.groupproject.model.DeleteResponse;
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

public class ReportActivity extends AppCompatActivity {

    TaskService taskService;
    Context context;
    RecyclerView taskList;
    TaskAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_task_list);
        context = this;

        // get reference to the RecyclerView bookList
        taskList = findViewById(R.id.reportTaskList);

        //register for context menu
        registerForContextMenu(taskList);

        // update listview
        updateListView();

        FloatingActionButton fabHome = findViewById(R.id.fabHome);
        fabHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to Home Page
                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * Fetch data for ListView
     */
    private void updateListView() {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // execute the call. send the user token when sending the query
        taskService.getAllTasks(user.getToken()).enqueue(new Callback<List<Task>>() {
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
                taskList.setAdapter(adapter);

                // set layout to recycler view
                taskList.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(taskList.getContext(),
                        DividerItemDecoration.VERTICAL);
                taskList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // get the menu inflater
        MenuInflater inflater = super.getMenuInflater();
        // inflate the menu using our XML menu file id, options_menu
        inflater.inflate(R.menu.options_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.finished:
                // user clicked finished menu item
                // call method to display dialog box
                doFinishedTask();
                return true;

            case R.id.progress:
                doProgressTask();
                return true;

            case R.id.all:
                updateListView();
                return true;
        }
        return false;
    }

    public void doProgressTask()
    {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

        // execute the call. send the user token when sending the query
        taskService.getPendingTask(user.getToken()).enqueue(new Callback<List<Task>>() {
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
                taskList.setAdapter(adapter);

                // set layout to recycler view
                taskList.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(taskList.getContext(),
                        DividerItemDecoration.VERTICAL);
                taskList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }

    public void doFinishedTask(){

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // get task service instance
        taskService = ApiUtils.getTaskService();

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
                taskList.setAdapter(adapter);

                // set layout to recycler view
                taskList.setLayoutManager(new LinearLayoutManager(context));

                // add separator between item in the list
                DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(taskList.getContext(),
                        DividerItemDecoration.VERTICAL);
                taskList.addItemDecoration(dividerItemDecoration);
            }

            @Override
            public void onFailure(Call<List<Task>> call, Throwable t) {
                Toast.makeText(context, "Error connecting to the server", Toast.LENGTH_LONG).show();
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu_report, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        Task selectedTask = adapter.getSelectedItem();
        Log.d("MyApp", "selected "+selectedTask.toString());
        switch (item.getItemId()) {

            case R.id.menu_delete://should match the id in the context menu file
                doDeleteTask(selectedTask);
                break;
        }
        return super.onContextItemSelected(item);
    }

    /**
     * Delete task record. Called by contextual menu "Delete"
     * @param selectedTask - task selected by user
     */
    private void doDeleteTask(Task selectedTask) {
        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // prepare REST API call
        TaskService taskService = ApiUtils.getTaskService();
        Call<DeleteResponse> call = taskService.deleteTask(user.getToken(), selectedTask.getTask_id());

        // execute the call
        call.enqueue(new Callback<DeleteResponse>() {
            @Override
            public void onResponse(Call<DeleteResponse> call, Response<DeleteResponse> response) {
                if (response.code() == 200) {
                    // 200 means OK
                    displayAlert("Task successfully deleted");
                    // update data in list view
                    updateListView();
                } else {
                    displayAlert("Task failed to delete");
                    Log.e("MyApp:", response.raw().toString());
                }
            }

            @Override
            public void onFailure(Call<DeleteResponse> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                Log.e("MyApp:", t.getMessage());
            }
        });
    }


    /**
     * Displaying an alert dialog with a single button
     * @param message - message to be displayed
     */
    public void displayAlert(String message) {
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