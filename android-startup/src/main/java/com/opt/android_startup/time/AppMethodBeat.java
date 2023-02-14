package com.opt.android_startup.time;

import android.app.Activity;
import android.util.Log;


import java.util.HashSet;
import java.util.Set;

public class AppMethodBeat {

    private static Set<String> sFocusActivitySet = new HashSet<>();

    private static AppMethodBeat sInstance = new AppMethodBeat();

    private static final HashSet<IAppMethodBeatListener> listeners = new HashSet<>();

    public static Long attachBaseContextTime = 0L;


    public static AppMethodBeat getInstance() {
        return sInstance;
    }

    public static void at(Activity activity, boolean isFocus) {
        String activityName = activity.getClass().getName();
        Log.d("数据", "activityName = " + activityName + ", 通过ASM生成代码");
        if (isFocus) {
            if (sFocusActivitySet.add(activityName)) {
                synchronized (listeners) {
                    for (IAppMethodBeatListener listener : listeners) {
                        listener.onActivityFocused(activity);
                    }
                }
            }
        } else {
            sFocusActivitySet.remove(activityName);
        }
    }

    public static void attachBaseContext() {
        attachBaseContextTime = System.currentTimeMillis();
//        synchronized (listeners) {
//            for (IAppMethodBeatListener listener : listeners) {
//                listener.attachBaseContext();
//            }
//        }
    }

    public void addListener(IAppMethodBeatListener listener) {
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeListener(IAppMethodBeatListener listener) {
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }


}
