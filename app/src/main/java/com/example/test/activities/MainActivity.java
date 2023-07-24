package com.example.test.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.components.User;
import com.example.test.utils.SQLConnection;
import com.example.test.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationBarView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static SQLConnection sqlConnection;

    public static ProgressDialog progressDialog;

    public static User loggedInUser = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Runner.runTask(() -> {
//            try {
//                //Background work here
//                String url = "jdbc:mysql://sql6.freemysqlhosting.net:3306/sql6631936";
//                String username = "sql6631936";
//                String password = "aE8v6qffBv";
//
//
//                sqlConnection = new SQLConnection(url, username, password);
//
//                sqlConnection.connectServer();
//
//                Log.i("Database", "Connection established");
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }, null, this);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                NavDestination currentDestination = navController.getCurrentDestination();
                if (currentDestination != null && currentDestination.getId() == id) {
                    // If the user is already on the selected navigation item, do nothing
                    return true;
                }

                if (id == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home);
                } else if (id == R.id.navigation_share) {
                    if (loggedInUser == null) {
                        navController.navigate(R.id.navigation_login);
                    } else {
                        navController.navigate(R.id.navigation_share);
                    }
                } else if (id == R.id.navigation_profile) {
                    if (loggedInUser != null) {
                        if (loggedInUser.getUsername().equals("admin")) {
                            navController.navigate(R.id.navigation_admin);
                        } else {
                            navController.navigate(R.id.navigation_profile);
                        }
                    } else {
                        navController.navigate(R.id.navigation_login);
                    }
                }
                item.setChecked(true);
                return false;
            }
        });
    }

    public static void runTask(Runnable background, Runnable result, ProgressDialog progressDialog) {
        if (progressDialog != null)
            progressDialog.show();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(new Runnable() {
            @Override
            public void run() {
                background.run();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null)
                            result.run();
                        if (progressDialog != null)
                            progressDialog.dismiss();
                    }
                });
            }
        });
    }


    // Clear focus on touch outside EditText, and hide keyboard on touch outside SearchView
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    v.clearFocus();
                }
            } else if (v instanceof SearchView) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}