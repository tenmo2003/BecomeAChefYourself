package com.example.test.activities;

import android.os.Bundle;
import android.view.MenuItem;

import com.example.test.R;
import com.example.test.components.User;
import com.example.test.database.SQLConnection;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.test.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static SQLConnection sqlConnection;

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

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(navView, navController);

        navView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
//
//                NavDestination currentDestination = navController.getCurrentDestination();
//                if (currentDestination != null && currentDestination.getId() == id) {
//                    // If the user is already on the selected navigation item, do nothing
//                    return true;
//                }

                if (id == R.id.navigation_home) {
                    navController.navigate(R.id.navigation_home);
                } else if (id == R.id.navigation_share) {
                    navController.navigate(R.id.navigation_share);
                } else if (id == R.id.navigation_user) {
                    if (loggedInUser != null) {
                        navController.navigate(R.id.navigation_user);
                    } else {
                        navController.navigate(R.id.navigation_login);
                    }
                }
                item.setChecked(true);
                return false;
            }
        });
    }
}