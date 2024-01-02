package com.example.groupproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.model.SharedPrefManager;
import com.example.groupproject.model.User;

public class MainActivity extends AppCompatActivity {

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        // get reference to the textview
        TextView txtHello = findViewById(R.id.txtHello);

        // get user info from SharedPreferences
        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        // set the textview to display username
        txtHello.setText("Hello Team Leader !");

        // assign action to logout button
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // clear the shared preferences
                SharedPrefManager.getInstance(getApplicationContext()).logout();

                // display message
                Toast.makeText(getApplicationContext(),
                        "You have successfully logged out.",
                        Toast.LENGTH_LONG).show();

                // forward to LoginActivity
                finish();
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));

            }
        });

        // assign action to Task List button
        Button btnTaskList = findViewById(R.id.btnTaskList);
        btnTaskList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to TaskListActivity
                Intent intent = new Intent(context, TaskListActivity.class);
                startActivity(intent);
            }
        });

        // assign action to Task List button
        Button btnReport = findViewById(R.id.btnReport);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // forward user to TaskListActivity
                Intent intent = new Intent(context, ReportActivity.class);
                startActivity(intent);
            }
        });


    }
}