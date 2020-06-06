package com.bbf.cruise;

import android.app.Application;
import android.content.Context;

public class CruiseApplication extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        CruiseApplication.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return CruiseApplication.context;
    }
}
