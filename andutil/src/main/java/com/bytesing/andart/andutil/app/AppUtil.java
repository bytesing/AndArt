package com.bytesing.andart.andutil.app;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;

import com.bytesing.andart.andutil.security.MDUtil;

import java.util.List;

/**
 * Created by <a href="mailto:lichuangxin@kugou.net">chuangxinli(Icebug)</a> on 2020/4/6.
 */
public class AppUtil {

    public static int getMyPid(){
        return Process.myPid();
    }

    public static PackageInfo getPackageInfo(Context context,String packageName,int flag){
        if (context == null) {
            return null;
        }
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(packageName,flag);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return info;
    }
    public static ApplicationInfo getApplicationInfo(Context context,String packageName,int flag){
        if (context == null) {
            return null;
        }
        ApplicationInfo info = null;
        try {
            info = context.getPackageManager().
                    getApplicationInfo(packageName,flag);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return info;
    }

    private static RunningAppProcessInfo getProcessInfo(Context context,int pid){

        if (context == null) {
            return null;
        }
        RunningAppProcessInfo curInfo = null;
        try{
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<RunningAppProcessInfo> appPro = am.getRunningAppProcesses();
            if (appPro != null && !appPro.isEmpty()) {
                for (RunningAppProcessInfo info :
                        appPro) {
                    if (info != null && info.pid == pid) {
                        curInfo =  info;
                        break;
                    }
                }
            }
        }catch(Throwable e){
            e.printStackTrace();
        }
        return curInfo;
    }

    public static String getProcessName(Context context,int pid){
        if(context == null){
            return "";
        }
        String processNameStr = ActivityThreadImpl.currentProcessName(context);
        if (TextUtils.isEmpty(processNameStr)) {
            RunningAppProcessInfo info = getProcessInfo(context,pid);
            processNameStr = info == null ? processNameStr : info.processName;
        }
        return processNameStr;
    }

    public static String getAppVersionName(Context context,String packageName){
        String versionName = "1.0";
        if(context == null){
            return versionName;
        }
        PackageInfo info = getPackageInfo(context,packageName,0);
        if (info != null) {
            versionName =  info.versionName;
        }
        return versionName;
    }

    public static long getAppVersion(Context context,String packageName){
        long version = 1;
        if (context == null) {
            return version;
        }
        PackageInfo info = getPackageInfo(context,packageName,0);
        if (info != null) {
            if (VERSION.SDK_INT >= VERSION_CODES.P) {
                version = info.getLongVersionCode();
            }else{
                version = info.versionCode;
            }
        }
        return version;
    }

    public static CharSequence getAppName(Context context,String packageName){
        CharSequence appNameStr = "";
        try {
            PackageManager pm = context.getPackageManager();
            ApplicationInfo ai = getApplicationInfo(context,packageName,PackageManager.GET_META_DATA);
            if(ai.labelRes > 0) {
                appNameStr = context.getString(ai.labelRes);
            }else{
                appNameStr = pm.getApplicationLabel(ai);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return appNameStr;
    }

    public static String getMetaData(Context context,String packageName,String key){
        String metaData = "";
        try {
            ApplicationInfo ai = getApplicationInfo(context,packageName,PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            metaData = bundle.getString(key);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return metaData;
    }
    public static String getAppSignature(Context context, String packageName) {
        String md5Sign = "";
        try {
            PackageInfo info = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                info = getPackageInfo(context, packageName, PackageManager.GET_SIGNING_CERTIFICATES);
            } else {
                info = getPackageInfo(context, packageName, PackageManager.GET_SIGNATURES);
            }
            Signature[] asignature = info.signatures;
            int j = asignature.length;
            for (int i = 0; i < j; i++) {
                Signature signature = asignature[i];
                md5Sign  =  MDUtil.md5(signature.toByteArray());
                if (!TextUtils.isEmpty(md5Sign)) {
                    return md5Sign;
                }
            }

        }catch(Throwable e){
            e.printStackTrace();
        }
        return md5Sign;
    }
}
