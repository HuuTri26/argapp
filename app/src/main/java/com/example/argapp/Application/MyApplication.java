package com.example.argapp.Application;

import android.app.Application;

import com.example.argapp.AdminPermissionChecker;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Setup default admin data on app startup
        AdminPermissionChecker.setupAdminData();
    }
}