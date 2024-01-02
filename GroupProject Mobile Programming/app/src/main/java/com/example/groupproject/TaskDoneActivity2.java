package com.example.groupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.Task;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.TaskService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaskDoneActivity2 extends AppCompatActivity {

    TaskService taskService;
    private Task task;      // store task info

    //private EditText txtShift;
    private EditText txtStatus;
    //private static TextView tvDueDate;

    //private static Date dueDate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_done);

        // get book id sent by BookListActivity, -1 if not found
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
                task = response.body();

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

    /**
     * Update book info in database when the user click Task Done button
     * @param view
     */
    public void updateTaskDone(View view) {

        task.setStatus("Finished");

        Log.d("MyApp:", "Task info: " + task.toString());

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to update the book record to the REST API
        TaskService taskService = ApiUtils.getTaskService();
        Call<Task> call = taskService.updateTask(user.getToken(), task);

        Context context = this;
        // execute
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // book updated successfully?
                Task updatedTask = response.body();
                if (updatedTask != null) {
                    // display message
                    Toast.makeText(context,
                            updatedTask.getTitle() + " successfully done",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, TaskListActivity2.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Update Task failed.");
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                displayAlert("Error [" + t.getMessage() + "]");
                // for debug purpose
                Log.d("MyApp:", "Error: " + t.getCause().getMessage());
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