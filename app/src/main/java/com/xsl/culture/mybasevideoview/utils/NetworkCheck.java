package com.xsl.culture.mybasevideoview.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

public class NetworkCheck {
    /**
     * 判断wifi是否链接
     * @param inContext
     * @return
     */
    public static boolean isWiFiActive(Context inContext) {
        WifiManager mWifiManager = (WifiManager) inContext
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        int ipAddress = wifiInfo == null ? 0 : wifiInfo.getIpAddress();
        if (mWifiManager.isWifiEnabled() && ipAddress != 0) {
            System.out.println("**** WIFI is on");
            return true;
        } else {
            System.out.println("**** WIFI is off");
            return false;
        }
    }

    /**
     * 判断是否有流量网络
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable( Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            System.out.println("**** newwork is off");
            return false;
        } else {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if(info == null){
                System.out.println("**** newwork is off");
                return false;
            }else{
                if(info.isAvailable()){
                    System.out.println("**** newwork is on");
                    return true;
                }

            }
        }
        System.out.println("**** newwork is off");
        return false;
    }

    public static boolean checkPermission(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Settings.canDrawOverlays(activity)) {
            Toast.makeText(activity, "当前无权限，请授权", Toast.LENGTH_SHORT).show();
            activity.startActivityForResult(
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + activity.getPackageName())), 0);
            return false;
        }
        return true;
    }

    public static void onActivityResult(Activity activity,
                                        int requestCode,
                                        int resultCode,
                                        Intent data,
                                        OnWindowPermissionListener onWindowPermissionListener) {
        if (requestCode == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !Settings.canDrawOverlays(activity)) {
                Toast.makeText(activity.getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
                if(onWindowPermissionListener!=null)
                    onWindowPermissionListener.onFailure();
            }else {
                Toast.makeText(activity.getApplicationContext(), "授权成功", Toast.LENGTH_SHORT).show();
                if(onWindowPermissionListener!=null)
                    onWindowPermissionListener.onSuccess();
            }
        }
    }

    public interface OnWindowPermissionListener{
        void onSuccess();
        void onFailure();
    }
}
