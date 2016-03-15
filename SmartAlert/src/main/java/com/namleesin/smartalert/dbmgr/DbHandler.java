package com.namleesin.smartalert.dbmgr;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.data.NotiData;
import com.namleesin.smartalert.data.PackData;


public class DbHandler
{	
	private Context mContext								= null;
	private DbManager mDbManager							= null;
	
	public DbHandler(Context context)
	{
		mContext = context.getApplicationContext();
		mDbManager = new DbManager(mContext);
	}
	
	public int selectCountDB(int aSelectType, String aParam)
	{
		int count = 0;
		Cursor cursor	= null;
		String[] selectionArgs = {aParam+""};

		switch(aSelectType)
		{
			case DBValue.TYPE_SELECT_DAY_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_DAY_COUNT, null);
				break;
			case DBValue.TYPE_SELECT_PRE_DAY_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_PRE_DAY_COUNT, selectionArgs);
				break;
			case DBValue.TYPE_SELECT_TOTAL_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_TOTAL_COUNT, null);
				break;
			case DBValue.TYPE_SELECT_URL_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_URL_COUNT, null);
				break;
			case DBValue.TYPE_SELECT_DISLIKE_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_DISLIKE_COUNT, null);
				break;
			case DBValue.TYPE_SELECT_LIKE_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_LIKE_COUNT, null);
				break;
			case DBValue.TYPE_SELECT_DISLIKE_PACKAGE_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_DISLIKE_PACKAGE_COUNT, null);
				break;
			case DBValue.TYPE_SELECT_PACKAGE_INFO_COUNT:
				cursor = mDbManager.query(DBValue.SQL_SELECT_PACKAGE_INFO_COUNT, selectionArgs);
				break;
			default:
				break;
		}
		
		if(null == cursor)
		{
			return count;
		}
		
		cursor.moveToFirst();
		count = cursor.getInt(0);

		cursor.close();
		return count;
	}

	public Cursor selectDBData(int aSelectType, String aParam)
	{
		String[] selectionArgs = {aParam+""};
		switch (aSelectType) {
			case DBValue.TYPE_SELECT_PACKAGE_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_PACKAGE_INFO, selectionArgs);
			case DBValue.TYPE_SELECT_FILTER_PACKAGE:
				return mDbManager.query(DBValue.SQL_SELECT_FILTERPKG_PACKAGENAME, selectionArgs);
			case DBValue.TYPE_SELECT_FILTERWORD_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_FILTERWORD_INFO, selectionArgs);
			case DBValue.TYPE_SELECT_FILTERPKG_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_FILTERPKG_INFO, null);
			case DBValue.TYPE_SELECT_DAILY_NOTI_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_DAILY_NOTI_INFO, null);
			case DBValue.TYPE_SELECT_PACKAGE_NOTI_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_PACKAGE_NOTI_COUNT, null);
			case DBValue.TYPE_SELECT_DISLIKE_PKG_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_DISLIKE_PKG_INFO, null);
			case DBValue.TYPE_SELECT_LIKE_PKG_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_LIKE_PKG_INFO, null);
			case DBValue.TYPE_SELECT_NOTI_INFO:
				return mDbManager.query(DBValue.SQL_SELECT_NOTI_INFO, null);
			default:
				break;
		}
		return null;
	}
	
	public int insertDB(int aInsertType, Object aObject)
	{
		int result = DBValue.SUCCESS;
		
		switch(aInsertType)
		{
			case DBValue.TYPE_INSERT_NOTIINFO:
				result = insertNotiInfoTable((NotiData)aObject);
				break;
			case DBValue.TYPE_INSERT_KEYWORDFILTER:
				result = insertKeywordFilterTable((KeywordData)aObject);
				break;
			case DBValue.TYPE_INSERT_PACKAGEFILTER:
				result= insertPackageFilterTable((PackData)aObject);
				break;
			default:
				break;
		}
		
		return result;
	}

	public int deleteDB(int aDeleteType, Object aObject)
	{
		int result = DBValue.SUCCESS;

		switch(aDeleteType)
		{
			case DBValue.TYPE_DELETE_FILTER_APP:
				result = deleteFilterAppTalble((PackData) aObject);
				break;
			case DBValue.TYPE_DELETE_FILTER_KEYWORD:
				result = deleteFilterKeywordTable((KeywordData) aObject);
				break;
			default:
				break;
		}

		return result;
	}
	private int deleteFilterAppTalble(PackData aPackData)
	{
		String[] params = getFilterPackData(aPackData);
		return handleDBData(DBValue.SQL_DELETE_FILTER_APP, params);
	}

	private int deleteFilterKeywordTable(KeywordData aKeywordData)
	{
		String[] params = getFilterKeywordData(aKeywordData);
		return handleDBData(DBValue.SQL_DELETE_FILTER_KEYWORD, params);
	}

	private int insertNotiInfoTable(NotiData aNotiData)
	{
		String[] params = getNotiData(aNotiData);
		return handleDBData(DBValue.SQL_INSRT_NOTIDATA, params);
	}

	private String[] getFilterKeywordData(KeywordData aKeywordData)
	{
		if(null == aKeywordData)
		{
			return null;
		}

		String[] params =
				{
						aKeywordData.keywordata + "",
						String.valueOf(aKeywordData.keyword_status)
				};

		return params;
	}

	private String[] getFilterPackData(PackData aPackData)
	{
		if(null == aPackData)
		{
			return null;
		}

		String[] params =
				{
						aPackData.packagename + "",
						String.valueOf(aPackData.package_status)
				};

		return params;
	}

	private String[] getNotiData(NotiData aNotiData)
	{
		if(null == aNotiData)
		{
			return null;
		}

		String[] params =
		{
			aNotiData.packagename + "",
			aNotiData.titletxt + "",
			aNotiData.subtxt + "",
			aNotiData.notiid + "",
			aNotiData.notikey + "",
			aNotiData.notitime + "",
			String.valueOf(aNotiData.status) + "",
			aNotiData.filter_word + "",
			String.valueOf(aNotiData.urlstatus)
		};
		
		return params;
	}
	
	private int insertKeywordFilterTable(KeywordData aKeywordData)
	{
		String[] params = getNotiData(aKeywordData);
		return handleDBData(DBValue.SQL_INSERT_KEYWORDDATA, params);
	}
	
	private String[] getNotiData(KeywordData aKeywordData)
	{
		if(null == aKeywordData)
		{
			return null;
		}

		String[] params =
		{
				aKeywordData.keywordata + "",
			String.valueOf(aKeywordData.keyword_status)
		};
		
		return params;
	}
	
	private int insertPackageFilterTable(PackData aPackData)
	{		
		String[] params = getPackData(aPackData);
		return handleDBData(DBValue.SQL_INSERT_PACKDATA, params);
	}
	
	private String[] getPackData(PackData aPackData)
	{
		if(null == aPackData)
		{
			return null;
		}

		String[] params =
		{
			aPackData.packagename + "",
			String.valueOf(aPackData.package_status)
		};
		
		return params;
	}
	
	private synchronized int handleDBData(String aSql, String[] aParams)
	{
		int result = DBValue.SUCCESS;
		
		if(null == mDbManager || null == aSql)
		{
			return DBValue.FAILURE;
		}
		
		mDbManager.beginTransaction();
		
		if (mDbManager.execute(aSql, aParams) < 0)
		{
			result = DBValue.FAILURE;
		}
		
		if (DBValue.SUCCESS == result)
		{
			mDbManager.setTransactionSuccessful();
		}
		
		mDbManager.endTransaction();
		return result;
	}
}
