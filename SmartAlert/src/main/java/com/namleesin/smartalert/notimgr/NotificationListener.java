package com.namleesin.smartalert.notimgr;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.data.NotiData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.shortcut.PrivacyMode;
import com.namleesin.smartalert.utils.PFMgr;

import java.util.ArrayList;
import java.util.HashMap;

public class NotificationListener extends NotificationListenerService
{
	public static String UPDATE_FILTER = "update_filter";
	private String[] checkText =
			{
					Notification.EXTRA_BIG_TEXT,
					Notification.EXTRA_TEXT,
					Notification.EXTRA_INFO_TEXT,
					Notification.EXTRA_SUB_TEXT,
					Notification.EXTRA_SUMMARY_TEXT
			};

	private HashMap<Integer, ArrayList<KeywordData>> mFilterKeyword = new HashMap<>();
	private ArrayList<String> mFilterPkg = new ArrayList<String>();
	DbHandler handler;

	private BroadcastReceiver mFilterUpdateReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
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

		if(mFilterKeyword.get(type) == null)
		{
			mFilterKeyword.put(type, new ArrayList<KeywordData>());
		}
		else
		{
			mFilterKeyword.get(type).clear();
		}

		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERWORD_INFO, type+"");
		if(cursor == null || cursor.getCount() == 0)
		{
			return;
		}

		cursor.moveToFirst();
		do
		{
			KeywordData keyword = new KeywordData();
			keyword.setKeywordata(cursor.getString(0));
			keyword.setKeywordstatus(cursor.getInt(1));
			if(keyword != null)
			{
				added.add(keyword);
			}
		}
		while(cursor.moveToNext());
		mFilterKeyword.put(type, added);
	}

	private void loadFilterPkg()
	{
		DbHandler handler = new DbHandler(getApplication());
		mFilterPkg.clear();
		Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
		if(cursor == null || cursor.getCount() == 0)
		{
			return;
		}

		cursor.moveToFirst();
		do
		{
			String pkg = cursor.getString(0);
			if(pkg != null)
			{
				mFilterPkg.add(pkg);
			}
		}
		while(cursor.moveToNext());
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

	@SuppressLint("NewApi")
	@Override
	public void onNotificationRemoved(StatusBarNotification sbn)
	{
		super.onNotificationRemoved(sbn);
	}

	public String getLikeFilter(String str)
	{
		for(KeywordData word : mFilterKeyword.get(DBValue.STATUS_LIKE))
		{
			if(str.contains(word.getKeywordata()))
				return word.getKeywordata();
		}
		return null;
	}

	public String getDisLikeFilter(String str)
	{
		for(KeywordData word : mFilterKeyword.get(DBValue.STATUS_DISLIKE))
		{
			if(str.contains(word.getKeywordata()))
				return word.getKeywordata();
		}
		return null;
	}

	private boolean isPrivacyMode()
	{
		PFMgr pfMgr = new PFMgr(this.getApplicationContext());
		int mode = pfMgr.getIntValue(PrivacyMode.PREF_PRIVACY_MODE, PrivacyMode.PRIVACY_MODE_OFF);

		if(mode == PrivacyMode.PRIVACY_MODE_ON)
			return true;
		return false;
	}

	@Override
	public void onNotificationPosted(StatusBarNotification sbn)
	{
		Notification noti = sbn.getNotification();

		if(noti.flags == Notification.FLAG_ONGOING_EVENT ||
				noti.flags == Notification.FLAG_FOREGROUND_SERVICE)
		{
			return;
		}

		NotiData notiData = new NotiData();
		notiData.notiid = sbn.getId()+"";
		notiData.packagename = sbn.getPackageName();
		notiData.titletxt = noti.extras.getString(Notification.EXTRA_TITLE);
		//Noti 이미지 가져오는 방법
		//notiData.picturebitmap = (Bitmap) sbn.getNotification().extras.get(Notification.EXTRA_PICTURE);

		String notiText = "";

		for(int i = 0; i< checkText.length; i++)
		{
			CharSequence temp = noti.extras.getCharSequence(checkText[i]);
			if(temp != null)
			{
				if(i == 1 && notiText.contains(temp.toString()))
				{
					continue;
				}
				notiText += temp.toString();
				notiText += "\n";
			}
		}

		notiData.subtxt = notiText;
		notiData.notitime = sbn.getPostTime()+"";
		notiData.urlstatus = 0;

		handler = new DbHandler(getApplicationContext());

		if(isPrivacyMode())
		{
			cancelAllNotifications();
		}

		if(true == mFilterPkg.contains(notiData.packagename))
		{
			notiData.status = DBValue.STATUS_DISLIKE;
			handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

			if(Build.VERSION.SDK_INT < 21)
			{
				cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
			}
			else
			{
				cancelNotification(sbn.getKey());
			}
			return;
		}

		String like = getLikeFilter(notiText);
		String dislike = getDisLikeFilter(notiText);
		if(like!= null)
		{
			notiData.status = DBValue.STATUS_LIKE;
			notiData.filter_word = like;
		}
		else if(dislike != null)
		{
			notiData.status = DBValue.STATUS_DISLIKE;
			notiData.filter_word = dislike;
			handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);

			if(Build.VERSION.SDK_INT < 21)
			{
				cancelNotification(sbn.getPackageName(), sbn.getTag(), sbn.getId());
			}
			else
			{
				cancelNotification(sbn.getKey());
			}
		}
		handler.insertDB(DBValue.TYPE_INSERT_NOTIINFO, notiData);
	}
}
