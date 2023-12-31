package com.example.test.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.test.R;
import com.example.test.components.Article;
import com.example.test.components.InAppNotification;
import com.example.test.components.User;
import com.example.test.fragments.AdminFragment;
import com.example.test.fragments.HomeFragment;
import com.example.test.fragments.LoginFragment;
import com.example.test.fragments.NotificationFragment;
import com.example.test.fragments.ShareFragment;
import com.example.test.fragments.ProfileFragment;
import com.example.test.utils.DatabaseHelper;
import com.example.test.utils.NotificationUtils;
import com.example.test.utils.SQLConnection;
import com.example.test.databinding.ActivityMainBinding;
import com.example.test.utils.SaveSharedPreference;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import me.ibrahimsn.lib.OnItemSelectedListener;
import me.ibrahimsn.lib.SmoothBottomBar;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    public static SQLConnection sqlConnection;

    public static ProgressDialog progressDialog;

    public static User loggedInUser = null;
    public static SmoothBottomBar navView;
    NavController navController;
    public static Toast toast;
    public static List<Article> articleList;
    public static List<InAppNotification> notificationList;

    private void setupSmoothBottomMenu() {
        PopupMenu popupMenu = new PopupMenu(this, null);
        popupMenu.inflate(R.menu.bottom_nav_menu);

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);

        navView = findViewById(R.id.nav_view);
        navView.setupWithNavController(popupMenu.getMenu(), navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = Toast.makeText(this, null, Toast.LENGTH_SHORT);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);

        ProgressDialog con = new ProgressDialog(this);
        con.setMessage("Đang kết nối");
        con.setCancelable(false);

        runTask(() -> {
            try {
                //Background work here
//                String url = "jdbc:mysql://byyrm3ewrta8hnhxsoxm-mysql.services.clever-cloud.com:3306/byyrm3ewrta8hnhxsoxm";
//                String url = "jdbc:mysql://46.252.181.32:3306/byyrm3ewrta8hnhxsoxm";
//                String username = "urv00k6u73pbdhlv";
//                String password = "YsXWXThVUsLfNbaKL1aP";

                String url = "jdbc:mysql://sql6.freemysqlhosting.net:3306/sql6636224?useUnicode=yes&characterEncoding=UTF-8";
                String username = "sql6636224";
                String password = "2WlVgwUzRt";

                sqlConnection = new SQLConnection(url, username, password);

                sqlConnection.connectServer();

                Log.i("Database", "Connection established");

            } catch (Exception e) {
                e.printStackTrace();
            }

            // Auto login if user logged before

            if (SaveSharedPreference.getUserName(MainActivity.this).length() != 0) {
                MainActivity.loggedInUser = sqlConnection.getUserWithUsername(SaveSharedPreference.getUserName(MainActivity.this));
            }

            if (MainActivity.loggedInUser != null) {
                notificationList = sqlConnection.getNotificationsForUser(MainActivity.loggedInUser.getUsername());
            }
        }, () -> {
            binding = ActivityMainBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());


            // Set up bottom bar
            setupSmoothBottomMenu();

            //BottomNavigationView navView = findViewById(R.id.nav_view);

            navView.setOnItemSelectedListener(new OnItemSelectedListener() {
                @Override
                public boolean onItemSelect(int id) {
                    NavDestination currentDestination = navController.getCurrentDestination();
                    if (currentDestination != null && currentDestination.getId() == id) {
                        // If the user is already on the selected navigation item, do nothing
                        return true;
                    }

                    int navigation_home = 0;
                    int navigation_share = 1;
                    int navigation_notification = 2;
                    int navigation_user = 3;

                    if (id == navigation_home) {
                        navController.popBackStack(R.id.navigation_home, false);
//                    navController.navigate(R.id.navigation_home);
                    } else if (id == navigation_share) {
                        if (loggedInUser == null) {
                            Toast.makeText(getApplicationContext(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
                            navController.navigate(R.id.navigation_login);
                            navView.setItemActiveIndex(navigation_user);
                            return false;
                        } else {
                            navController.navigate(R.id.navigation_share);
                        }
                    } else if (id == navigation_user) {
                        if (loggedInUser != null) {
                            if (loggedInUser.getUsername().equals("admin")) {
                                navController.navigate(R.id.navigation_admin);
                            } else {
                                navController.navigate(R.id.navigation_profile);
                            }
                        } else {
                            navController.navigate(R.id.navigation_login);
                        }
                    } else if (id == navigation_notification){
                        if (loggedInUser == null) {
                            Toast.makeText(getApplicationContext(), "Vui lòng đăng nhập trước", Toast.LENGTH_SHORT).show();
                            navController.navigate(R.id.navigation_login);
                            navView.setItemActiveIndex(navigation_user);
                            return false;
                        } else {
                            navController.navigate(R.id.navigation_notification);
                        }
                    }

                    return false;
                }
            });

            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            service.scheduleAtFixedRate(() -> {
                if (MainActivity.loggedInUser != null) {
                    System.out.println("Updated");
                    // Fetch notifications in the background
                    List<InAppNotification> notifications = sqlConnection.getNotificationsForUser(loggedInUser.getUsername());
                    if (notificationList == null) {
                        notificationList = new ArrayList<>();
                        notificationList.addAll(notifications);
                    }
                    System.out.println(notifications.size());
                    System.out.println(notificationList.size());

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Do your stuff here, It gets loop every 15 Minutes
                            if (notificationList.size() < notifications.size()) {
                                int difference = notifications.size() - notificationList.size();
                                for (int i = 0; i < difference; i++) {
                                    // Get the first n new notifications and show push notifications
                                    InAppNotification notification = notifications.get(i);

                                    NotificationUtils.showNotification(MainActivity.this, notification);

                                    notificationList.add(0, notification);
                                }
                                NotificationFragment.updated.setValue(true);
                            } else if (notificationList.size() > notifications.size()) {
                                notificationList.clear();
                                notificationList.addAll(notifications);
                            }
                        }
                    });
                }
            }, 0, 3, TimeUnit.SECONDS);
        }, con);
    }

    public static void runTask(Runnable background, Runnable result, ProgressDialog progressDialog) {
        if (progressDialog != null) {
            progressDialog.setMessage("Đang tải");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        final CountDownLatch latch = new CountDownLatch(1);

        executor.execute(new Runnable() {
            @Override
            public void run() {
                background.run();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (result != null)
                            result.run();
                        latch.countDown();

                    }
                });

                try {
                    latch.await();
                    if (progressDialog != null)
                        progressDialog.dismiss();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public static void runTask(Runnable background) {

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                background.run();
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

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Bundle extras = intent.getExtras();

        Bundle args = new Bundle();

        String type = extras.getString("type");
        System.out.println(type);
        if (type.equals("LIKE") || type.equals("FOLLOWING_POST")) {
            args.putInt("articleID", extras.getInt("articleID"));
            navController.navigate(R.id.navigation_article, args);
        } else if (type.equals("COMMENT")) {
            args.putInt("articleID", extras.getInt("articleID"));
            args.putBoolean("toComment", true);
            navController.navigate(R.id.navigation_article, args);
        } else if (type.equals("FOLLOW")) {
            args.putString("username", extras.getString("username"));
            navController.navigate(R.id.navigation_profile, args);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}