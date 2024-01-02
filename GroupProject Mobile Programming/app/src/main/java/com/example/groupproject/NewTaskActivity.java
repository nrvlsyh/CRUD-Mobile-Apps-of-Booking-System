package com.example.groupproject;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.Task;
import com.example.groupproject.model.User;
import com.example.groupproject.remote.ApiUtils;
import com.example.groupproject.remote.TaskService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewTaskActivity extends AppCompatActivity {

    private EditText txtTitle;
    private EditText txtDesc;
    //private EditText txtShift;
    private TextView tvStatus;

    private static TextView tvDueDate; // static because need to be accessed by DatePickerFragment

    private static Date dueDate; // static because need to be accessed by DatePickerFragment

    private Context context;

    /**
     * Date picker fragment class
     * Reference: https://developer.android.com/guide/topics/ui/controls/pickers
     */
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user

            // create a date object from selected year, month and day
            dueDate = new GregorianCalendar(year, month, day).getTime();

            // display in the label beside the button with specific date format
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            tvDueDate.setText( sdf.format(dueDate) );
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);

        // store context
        context = this;

        // get view objects references
        txtTitle = findViewById(R.id.txtTitle);
        txtDesc = findViewById(R.id.txtDesc);
        tvStatus = findViewById(R.id.tvStatus);
        tvDueDate = findViewById(R.id.tvDueDate);

        // set default createdAt value, get current date
        dueDate = new Date();
        // display in the label beside the button with specific date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        tvDueDate.setText( sdf.format(dueDate) );
    }

    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void addNewTask(View v) {
        // get values in form
        String title = txtTitle.getText().toString();
        String description = txtDesc.getText().toString();
        //String shift = txtShift.getText().toString();
        String status = tvStatus.getText().toString();
        //String year = txtYear.getText().toString();

        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String duedate = sdf.format(dueDate);



        // set updated_at with the same value as created_at
        //String updated_at = created_at;

        // create a Task object
        // set id to 0, it will be automatically generated by the db later
        // set image to dummy value
        Task b = new Task(0, title, description, duedate, status);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // send request to add new book to the REST API
        TaskService taskService = ApiUtils.getTaskService();
        Call<Task> call = taskService.addTask(user.getToken(), b);

        // execute
        call.enqueue(new Callback<Task>() {
            @Override
            public void onResponse(Call<Task> call, Response<Task> response) {

                // for debug purpose
                Log.d("MyApp:", "Response: " + response.raw().toString());

                // invalid session?
                if (response.code() == 401)
                    displayAlert("Invalid session. Please re-login");

                // task added successfully?
                Task addedTask = response.body();
                if (addedTask != null) {
                    // display message

                    addedTask.setStatus("In Progress");
                    Toast.makeText(context,
                            addedTask.getTitle() + " added successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, TaskListActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    displayAlert("Add New Task failed.");
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