package com.bytesing.andart.app;

import android.app.Application;
import android.os.Process;
import android.util.Log;

import com.bytesing.andart.andutil.app.AppUtil;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/5.
 */
public class MyApplication extends Application {

    private static final String TAG = "MyApplication";
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate: process name is "+ AppUtil.getProcessName(this, Process.myPid()));

    }
}
