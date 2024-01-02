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

public class UpdateTaskActivity extends AppCompatActivity {

    private TaskService taskService;
    private Task task;      // store task info

    // form fields
    private EditText txtTitle;
    private EditText txtDesc;
    //private EditText txtShift;
    //private EditText txtStatus;
    private static TextView tvDueDate;

    private static Date dueDate;

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

    /**
     * Called when pick date button is clicked. Display a date picker dialog
     * @param v
     */
    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);

        // retrieve task id from intent
        // get task id sent by BTaskListActivity, -1 if not found
        Intent intent = getIntent();
        int id = intent.getIntExtra("task_id", -1);

        // initialize createdAt to today's date
        //createdAt = new Date();

        // get references to the form fields in layout
        txtTitle = findViewById(R.id.txtTitle);
        txtDesc = findViewById(R.id.txtDesc);
        //txtShift = findViewById(R.id.txtDuedate);
        //txtStatus = findViewById(R.id.txtStatus);
        //txtYear = findViewById(R.id.txtYear);
        tvDueDate = findViewById(R.id.tvDueDate);

        // retrieve task info from database using the task id
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

                // get task object from response
                task = response.body();

                // set values into forms
                txtTitle.setText(task.getTitle());
                txtDesc.setText(task.getDescription());
                //txtShift.setText(task.getShift());
                //txtISBN.setText(book.getIsbn());
                //txtYear.setText(book.getYear());
                tvDueDate.setText(task.getDuedate());

                // parse created_at date to date object
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    // parse created date string to date object
                    dueDate = sdf.parse(task.getDuedate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<Task> call, Throwable t) {
                Toast.makeText(null, "Error connecting", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Update book info in database when the user click Update Task button
     * @param view
     */
    public void updateTask(View view) {
        // get values in form
        String title = txtTitle.getText().toString();
        String description = txtDesc.getText().toString();
        //String shift = txtShift.getText().toString();
        //String isbn = txtISBN.getText().toString();
        //String year = txtYear.getText().toString();

        // convert createdAt date to format in DB
        // reference: https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String duedate = sdf.format(dueDate);

        // set updated_at to current date and time
        //String updated_at = sdf.format(new Date());

        // update the task object retrieved in onCreate with the new data. the task object
        // already contains the id
        task.setTitle(title);
        task.setDescription(description);
        //task.setShift(shift);
        //book.setAuthor(author);
        task.setDuedate(duedate);
        //book.setUpdatedAt(updated_at);

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
                            updatedTask.getTitle() + " updated successfully.",
                            Toast.LENGTH_LONG).show();

                    // end this activity and forward user to BookListActivity
                    Intent intent = new Intent(context, TaskListActivity.class);
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