package com.namleesin.smartalert.notimgr;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.data.NotiData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.shortcut.PrivacyMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Observable;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class NotificationListener extends NotificationListenerService
{
	public static String UPDATE_FILTER = "update_filter";
	private String[] checkText = {  Notification.EXTRA_TEXT,
									Notification.EXTRA_INFO_TEXT,
									Notification.EXTRA_BIG_TEXT,
						   			Notification.EXTRA_SUB_TEXT,
									Notification.EXTRA_SUMMARY_TEXT};

	private HashMap<Integer, ArrayList<KeywordData>> mFilterKeyword = new HashMap<>();
	private ArrayList<String> mFilterPkg = new ArrayList<>();
	DbHandler handler;

	private BroadcastReceiver mFilterUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(action.equals(UPDATE_FILTER))
			{
				loadFilterPkg();
				loadFilterKeywords(DBValue.STATUS_LIKE);
				loadFilterKeywords(DBValue.STATUS_DISLIKE);
			}
		}
	};

	private void loadFilterKeywords(int type)
	{
		DbHandler handler = new DbHandler(getApplication());
		ArrayList<KeywordData> added = new ArrayList<>();

		if(mFilterKeyword.get(type) == null) {
			mFilterKeyword.put(type, new ArrayList<KeywordData>());
		}
		else
		{
			mFilterKeyword.get(type).clear();
		}

		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERWORD_INFO, type+"");
		if(cursor == null || cursor.getCount() == 0) {
			return;
		}

		cursor.moveToFirst();
		do {
			KeywordData keyword = new KeywordData();
			keyword.setKeywordata(cursor.getString(0));
			keyword.setKeywordstatus(cursor.getInt(1));
			if(keyword != null)
			{
				added.add(keyword);
			}
		}while(cursor.moveToNext());
		mFilterKeyword.put(type, added);
	}

	private void loadFilterPkg()
	{
		DbHandler handler = new DbHandler(getApplication());
		mFilterPkg.clear();
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
		if(cursor == null || cursor.getCount() == 0) {
			return;
		}

		cursor.moveToFirst();
		do {
			String pkg = cursor.getString(0);
			if(pkg != null)
			{
				mFilterPkg.add(pkg);
			}
		}while(cursor.moveToNext());
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		handler = new DbHandler(getApplicationContext());

		loadFilterPkg();
		loadFilterKeywords(DBValue.STATUS_LIKE);
		loadFilterKeywords(DBValue.STATUS_DISLIKE);

		IntentFilter filter = new IntentFilter();
		filter.addAction(UPDATE_FILTER);
		registerReceiver(mFilterUpdateReceiver, filter);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		unregisterReceiver(mFilterUpdateReceiver);
	}

	@Override
	public void onNotificationRemoved(StatusBarNotification sbn) 
	{
		super.onNotificationRemoved(sbn);
	}
	
	public boolean isDislikePkg(String pkg_name, String noti)
	{
		for(String pkg : mFilterPkg) {
			if (pkg_name.equals(pkg)) {
				return true;
			}
		}
		return false;
	}
	
	public String getLikeFilter(String str)
	{
		for(KeywordData word : mFilterKeyword.get(DBValue.STATUS_LIKE))
		{
			Log.d("NJ LEE", "word : "+word.getKeywordata()+" status : "+word.getKeywordstatus());
			if(str.contains(word.getKeywordata()))
				return word.getKeywordata();
		}
		return null;
	}

	public String getDisLikeFilter(String str)
	{
		for(KeywordData word : mFilterKeyword.get(DBValue.STATUS_DISLIKE))
		{
			Log.d("NJ LEE", "word : "+word.getKeywordata()+" status : "+word.getKeywordstatus());
			if(str.contains(word.getKeywordata()))
				return word.getKeywordata();
		}
		return null;
	}

	private boolean isPrivacyMode()
	{
		SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
		int mode = pref.getInt(PrivacyMode.PREF_PRIVACY_MODE, -1);
		if(mode == PrivacyMode.PRIVACY_MODE_ON)
			return true;
		return false;
	}

	@SuppressLint("NewApi")
	@Override
	public void onNotificationPosted(StatusBarNotification sbn)
	{
		Notification noti = sbn.getNotification();
		NotiData notiData = new NotiData();
		notiData.notiid = sbn.getId()+"";
		notiData.notikey = sbn.getKey();
		notiData.packagename = sbn.getPackageName();

		if(noti.flags == Notification.FLAG_ONGOING_EVENT ||
				noti.flags == Notification.FLAG_FOREGROUND_SERVICE ||
				noti.flags == Notification.FLAG_AUTO_CANCEL)
		{
			return;
		}

		notiData.titletxt = noti.extras.getString(Notification.EXTRA_TITLE);

		String notiText = "";

		for(int i = 0; i< checkText.length; i++) {
			CharSequence temp = noti.extras.getCharSequence(checkText[i]);
			if(temp != null) {
				notiText += temp.toString();
				notiText += "\n";
			}
		}

		notiData.subtxt = notiText;
		notiData.notitime = sbn.getPostTime()+"";
		notiData.urlstatus = 0;

		String like = getLikeFilter(notiText);
		if(like!= null)
		{
			Log.d("NJ LEE", "like : "+like);
			notiData.status = DBValue.STATUS_LIKE;
			notiData.filter_word = like;
		}

		handler = new DbHandler(getApplicationContext());
		handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

		if(isDislikePkg(notiData.packagename, notiText))
		{
			notiData.status = DBValue.STATUS_DISLIKE;
			notiData.filter_word = null;
			handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

			cancelNotification(sbn.getKey());
		}
		else
		{
			String dislike = getDisLikeFilter(notiText);
			Log.d("NJ LEE", "dislike : "+dislike);
			if(dislike != null) {
				notiData.status = DBValue.STATUS_DISLIKE;
				notiData.filter_word = dislike;
				cancelNotification(sbn.getKey());
				handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);
			}
			else if(like == null)
			{
				notiData.status = DBValue.STATUS_NORMAL;
				handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);
			}
		}

		if(isPrivacyMode())
		{
			cancelAllNotifications();
		}
	}
}
