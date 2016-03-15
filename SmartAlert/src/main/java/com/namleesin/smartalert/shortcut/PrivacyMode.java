package com.namleesin.smartalert.shortcut;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.namleesin.smartalert.R;

/**
 * Created by nanjui on 2016. 1. 23..
 */
public class PrivacyMode extends Activity{
    public static final String PRIVACY_MODE = "private_mode";
    public static final String PREF_PRIVACY_MODE = "pref_private_mode";
    public static final int PRIVACY_MODE_ON = 1;
    public static final int PRIVACY_MODE_OFF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changePrivacyMode(getApplication());
    }

    public void changePrivacyMode(Context context)
    {
        SharedPreferences pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        int mode = pref.getInt(PrivacyMode.PREF_PRIVACY_MODE, -1);
        SharedPreferences.Editor editor = pref.edit();
        if(mode == PrivacyMode.PRIVACY_MODE_ON)
        {
            delPrivacyModeShorcut(context);
            editor.putInt(PrivacyMode.PREF_PRIVACY_MODE, PrivacyMode.PRIVACY_MODE_OFF);
            addPrivacyModeShortcut(context, R.drawable.privacy_off_xhdpi);
        }
        else if(mode == PrivacyMode.PRIVACY_MODE_OFF)
        {
            delPrivacyModeShorcut(context);
            editor.putInt(PrivacyMode.PREF_PRIVACY_MODE, PrivacyMode.PRIVACY_MODE_ON);
            addPrivacyModeShortcut(context, R.drawable.privacy_on_xhdpi);
        }
        else
        {
            editor.putInt(PrivacyMode.PREF_PRIVACY_MODE, PrivacyMode.PRIVACY_MODE_OFF);
            addPrivacyModeShortcut(context, R.drawable.privacy_off_xhdpi);
        }
        editor.commit();
        finish();
    }

    public void addPrivacyModeShortcut(Context context, int resId)
    {
        Intent shortcutIntent = new Intent(context, PrivacyMode.class);
        shortcutIntent.setClassName(context, "com.namleesin.smartalert.shortcut.PrivacyMode");
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "privacy");
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, resId));
        // Inform launcher to create shortcut
        context.sendBroadcast(addIntent);
    }

    public void delPrivacyModeShorcut(Context context)
    {
        Intent shortcutIntent = new Intent(context, PrivacyMode.class);
        shortcutIntent.setClassName(context, "com.namleesin.smartalert.shortcut.PrivacyMode");

        // Decorate the shortcut
        Intent delIntent = new Intent();
        delIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        delIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "privacy");

        // Inform launcher to remove shortcut
        delIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        context.sendBroadcast(delIntent);
    }
}
