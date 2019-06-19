package com.namleesin.smartalert.shortcut;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.utils.PFMgr;
import com.namleesin.smartalert.utils.PFValue;

/**
 * Created by nanjui on 2016. 1. 23..
 */
public class PrivacyMode extends Activity
{
    public static final String PREF_PRIVACY_MODE = "pref_private_mode";
    public static final int PRIVACY_MODE_ON = 1;
    public static final int PRIVACY_MODE_OFF = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        changePrivacyMode(getApplication());
    }

    public void changePrivacyMode(Context context)
    {
        int resmegid = 0;
        PFMgr pfMgr = new PFMgr(context);
        int mode = pfMgr.getIntValue(PrivacyMode.PREF_PRIVACY_MODE, PRIVACY_MODE_ON);

        if(mode == PrivacyMode.PRIVACY_MODE_ON)
        {
            //delPrivacyModeShorcut(context);
            pfMgr.setIntValue(PrivacyMode.PREF_PRIVACY_MODE, PRIVACY_MODE_OFF);
            //addPrivacyModeShortcut(context, R.drawable.privacy_off);
            resmegid = R.string.STR_PRIVACY_MODE_OFF;
        }
        else //mode == PrivacyMode.PRIVACY_MODE_OFF
        {
            //delPrivacyModeShorcut(context);
            pfMgr.setIntValue(PrivacyMode.PREF_PRIVACY_MODE, PRIVACY_MODE_ON);
            //addPrivacyModeShortcut(context, R.drawable.privacy_on);
            resmegid = R.string.STR_PRIVACY_MODE_ON;
        }

        boolean initstate = pfMgr.getBooleanValue(PFValue.PRE_SHORTCUT_INIT, false);
        if(false == initstate)
        {
            pfMgr.setBooleanValue(PFValue.PRE_SHORTCUT_INIT, true);
            finish();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(getString(R.string.STR_PRIVACY_MODE));
        builder.setMessage(resmegid);
        builder.setCancelable(true);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                finish();
            }
        });
        builder.setPositiveButton(getString(R.string.STR_COMM_BTN_CLOSE_TXT), new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int whichButton)
            {
                finish();
            }
        });
        builder.show();
    }

    public void addPrivacyModeShortcut(Context context, int resId)
    {
        Intent shortcutIntent = new Intent(context, PrivacyMode.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.setClassName(context, "com.namleesin.smartalert.shortcut.PrivacyMode");
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent addIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.STR_PRIVACY_MODE));
        addIntent.putExtra("duplicate", false);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, resId));

        context.sendBroadcast(addIntent);
    }

    public void delPrivacyModeShorcut(Context context)
    {
        Intent shortcutIntent = new Intent(context, PrivacyMode.class);
        shortcutIntent.setAction(Intent.ACTION_MAIN);
        shortcutIntent.setClassName(context, "com.namleesin.smartalert.shortcut.PrivacyMode");
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        Intent addIntent = new Intent("com.android.launcher.action.UNINSTALL_SHORTCUT");
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.STR_PRIVACY_MODE));

        context.sendBroadcast(addIntent);
    }
}
