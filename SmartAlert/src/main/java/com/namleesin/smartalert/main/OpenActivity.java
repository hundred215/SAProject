package com.namleesin.smartalert.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.namleesin.smartalert.graph.SAGraphActivity;
import com.namleesin.smartalert.guidemgr.GuideMgrActivity;
import com.namleesin.smartalert.settingmgr.AlertSettingActivity;
import com.namleesin.smartalert.settingmgr.NotiSettingActivity;

public class OpenActivity
{
	public static void startSplashScreenActivity(Context aContext)
	{
		Intent i = new Intent(aContext, SplashScreenActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_SPLASH_SCREEN);
	}
	
	public static void startGuideMgrActivity(Context aContext)
	{
		Intent i = new Intent(aContext, GuideMgrActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_GUIDE_WIZARD);
	}
	
	public static void startAlertSettingActivity(Context aContext)
	{
		Intent i = new Intent(aContext, AlertSettingActivity.class);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_ALERT_SETTING);
	}

	public static void startAlertAccessSettingActivity(Context aContext)
	{
		Intent i = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
		((Activity)aContext).startActivityForResult(i, MainValue.RES_ALERT_ACCESS);
	}

	public static void startSpamSettingActivity(Context aContext)
	{
		Intent i = new Intent(aContext, NotiSettingActivity.class);
		i.putExtra(MainValue.ACTIVITY_TYPE, MainValue.TYPE_INIT_NOTI_SETTING);
		((Activity)aContext).startActivityForResult(i, MainValue.RES_SL_SETTING);
	}

	public static void startGraphActivity(Context aContext)
	{
		Intent i = new Intent(aContext, SAGraphActivity.class);
		((Activity)aContext).startActivity(i);
	}

	public static void openSpamSettingActivity(Context aContext, int aIndex)
	{
		Intent i = new Intent(aContext, NotiSettingActivity.class);
		i.putExtra(MainValue.ACTIVITY_TYPE, MainValue.TYPE_MENU_NOTI_SETTING);
		i.putExtra(MainValue.SET_INDEX_NUMBER, aIndex);
		((Activity) aContext).startActivity(i);
	}

	public static void openGuideMgrActivity(Context aContext)
	{
		Intent i = new Intent(aContext, GuideMgrActivity.class);
		((Activity)aContext).startActivity(i);
	}

	public static void openMainGuideActivity(Context aContext)
	{
		Intent i = new Intent(aContext, MainGuideActivity.class);
		((Activity)aContext).startActivity(i);
	}
}
