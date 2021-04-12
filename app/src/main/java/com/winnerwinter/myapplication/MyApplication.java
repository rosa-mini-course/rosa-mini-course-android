package com.winnerwinter.myapplication;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {
    public static Context context;
    public static boolean shouldLogin = true;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }
}
