package com.morristaedt.mirror;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.morristaedt.mirror.configuration.ConfigurationSettings;

import io.fabric.sdk.android.Fabric;

/**
 * Created by HannahMitt on 8/22/15.
 */
public class MirrorApplication extends Application {
    private static Application sApplication;

    public static Application getApplication() {
        return sApplication;
    }

    public static Context getContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (!ConfigurationSettings.isDebugBuild()) {
            Fabric.with(this, new Crashlytics());
        }
        this.sApplication = this;
    }
}
