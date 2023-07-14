package com.example.test.fragments;

import android.app.Activity;

public class Runner {
    public static void runTask(Runnable mainFunction, Runnable uiFunction, Activity activity) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                mainFunction.run();
                activity.runOnUiThread(uiFunction);
            }
        }).start();
    }
}
