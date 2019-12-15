package com.example.betreuer.helper;

import android.app.Application;
import android.content.Context;

/**
 * Hacky class to get a context from anywhere in the program
 * Used exclusively to access resources outside of activities
 * Reference to this thread on StackOverflow:
 * https://stackoverflow.com/questions/2002288/static-way-to-get-context-in-android
 */
public class GlobalContext extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        GlobalContext.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return GlobalContext.context;
    }
}
