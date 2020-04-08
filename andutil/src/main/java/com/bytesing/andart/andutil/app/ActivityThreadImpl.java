package com.bytesing.andart.andutil.app;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/6.
 */
public class ActivityThreadImpl {
    private static final String TAG = "ActivityThreadImpl";

    @SuppressLint("PrivateApi")
    private static Object currentActivityThread(Context context){
        if(context == null){
            return null;
        }
        Object mActivityThread = null;
        try{
            Class activityThreadClazz = Class.forName("android.app.ActivityThread");
            Method m = activityThreadClazz.getMethod("currentActivityThread");
            m.setAccessible(true);
            mActivityThread = m.invoke(null);
            if (mActivityThread == null) {
                Application app = (Application) context.getApplicationContext();
                Field mLoadedApkField = app.getClass().getField("mLoadedApk");
                mLoadedApkField.setAccessible(true);
                Object mLoadedApk = mLoadedApkField.get(app);
                Field mActivityThreadField = mLoadedApk.getClass().getField("mActivityThread");
                mActivityThreadField.setAccessible(true);
                mActivityThread = mActivityThreadField.get(mLoadedApk);

            }

        }catch(Throwable e){
            e.printStackTrace();

        }
        return mActivityThread;
    }

    public static String currentProcessName(Context context){

        String processNameStr = currentProcessName();
        if(TextUtils.isEmpty(processNameStr)) {
            Object mActivityThread = currentActivityThread(context);
            if (mActivityThread != null) {
                try {
                    Method getProcessName = mActivityThread.getClass().getDeclaredMethod("getProcessName");
                    getProcessName.setAccessible(true);
                    processNameStr = (String) getProcessName.invoke(mActivityThread);

                } catch (Throwable e) {
                    e.printStackTrace();

                }

            }
        }
        Log.i(TAG, "currentProcessName: "+processNameStr);
        return processNameStr;
    }

    @SuppressLint("PrivateApi")
    public static String currentPackageName(){

        String pkn = "";
        try {
            Class activityThreadClazz = Class.forName("android.app.ActivityThread");
            Method method = activityThreadClazz.getMethod("currentPackageName");
            method.setAccessible(true);
            pkn = (String) method.invoke(null);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return pkn;

    }

    @SuppressLint("PrivateApi")
    public static String currentProcessName(){

        String processNameStr = "";
        try {
            Class activityThreadClazz = Class.forName("android.app.ActivityThread");
            Method method = activityThreadClazz.getMethod("currentProcessName");
            method.setAccessible(true);
            processNameStr = (String) method.invoke(null);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return processNameStr;

    }
}
