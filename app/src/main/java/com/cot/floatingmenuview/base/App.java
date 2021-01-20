package com.cot.floatingmenuview.base;

import android.app.Application;
import android.content.Context;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;
import com.squareup.leakcanary.BuildConfig;
import com.squareup.leakcanary.LeakCanary;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Utils.init(this);

        LogUtils.getConfig().setLogSwitch(BuildConfig.DEBUG);

        LeakCanary.install(this);

    }
}