package com.example.mybasevideoview.utils;

import android.app.Activity;
import android.view.WindowManager;

import com.example.mybasevideoview.MainPlayerActivity;

import java.lang.ref.WeakReference;

public class XslUtils {
    //true隐藏，false显示
    public static void hideStausbar(WeakReference<Activity> activityWeakReference, boolean enable) {
        Activity activity = activityWeakReference.get();
        if (enable) {

            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        }
    }

    public static String convertSecToTimeString(long lSeconds) {
        long nHour = lSeconds / 3600;
        long nMin = lSeconds % 3600;
        long nSec = nMin % 60;
        nMin = nMin / 60;

        return String.format("%02d:%02d:%02d", nHour, nMin, nSec);
    }
}
