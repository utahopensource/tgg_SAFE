package org.utos.android.safe.app;

/**
 * Created by zachariah.davis on 2/3/17.
 */

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    private static MyApplication mInstance;
    private static Context mAppContext;

    @Override public void onCreate() {
        super.onCreate();
        mInstance = this;
        this.setAppContext(getApplicationContext());

    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    private void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

}
