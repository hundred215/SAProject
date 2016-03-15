package com.namleesin.smartalert.utils;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import android.provider.Settings;

import com.namleesin.smartalert.notimgr.NotificationListener;

/**
 * Created by namjinha on 2016-01-23.
 */
public class NotiAlertState
{
    public static boolean isNLServiceRunning(Context aContext)
    {
        ContentResolver contentResolver = aContext.getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = aContext.getPackageName();

        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName))
        {
            return false;
        }

        return true;
    }
}
