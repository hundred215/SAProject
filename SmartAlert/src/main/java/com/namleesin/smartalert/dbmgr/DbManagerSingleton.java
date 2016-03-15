package com.namleesin.smartalert.dbmgr;

import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbManagerSingleton
{
	private static DbManagerSingleton instance;
	
	/** 데이터베이스 저장 */
	private DatabaseHelper mDbHelper = null;
	private SQLiteDatabase mDb = null; // 데이터베이스를 저장
	
	/** 컨텍스트를 저장해서 SQL을 로드할 수 있다. */
	private static Context sCtx;
	
	private static DbInfo sDbInfo = null;
	
	// create Instance
	// if success, return 0 otherwise -1
	public static int createInstance(final Context aCtx, DbInfo aDbInfo)
	{
		if (aCtx == null || aDbInfo == null)
		{
			return -1;
		}
		
		// check DB info.
		if (aDbInfo.mSQLCreateTable.size() < 1 ||
				aDbInfo.mDbName.length() == 0 ||
				aDbInfo.mDbVersion <= 0)
		{
			return -1;
		}
		
		if (instance == null)
		{
			synchronized (DbManagerSingleton.class)
			{
				if (instance == null)
				{
					// create instance
					instance = new DbManagerSingleton();
					sCtx = aCtx;
					sDbInfo = aDbInfo;
				}
			}
		}
		
		return 0;
	}
	
	// if null returns, you must call createInstance()
	public static DbManagerSingleton getInstance()
	{
		return instance;
	}
	
	public synchronized int executeSQL(String sql)
	{
		if (openDB() < 0)
		{
			return -1;
		}
		
		try
		{
			mDb.execSQL(sql);
		}
		catch (Exception e)
		{
			return -1;
		}
		finally
		{
			closeDB();
		}
		return 0;
	}
	
	// public synchronized boolean executeSQL(String sql, String[][] result)
	public synchronized String[][] executeQuerySQL(String sql, int retQuery[])
	{
		Cursor cur = null;
		int colCnt;
		int rowCnt;
		int colInx;
		int rowInx;
		String[][] result;
		// Log.d("KW", "SQL :" + sql);
		if (openDB() < 0)
		{
			return null;
		}
		
		retQuery[0] = 0;
		try
		{
			cur = mDb.rawQuery(sql, null);
			colCnt = cur.getColumnCount();
			rowCnt = cur.getCount();
			if (rowCnt == 0 || colCnt == 0)
			{
				return null;
			}
			result = new String[rowCnt][colCnt];
			
			cur.moveToFirst();
			for (rowInx = 0; rowInx < rowCnt; rowInx++)
			{
				for (colInx = 0; colInx < colCnt; colInx++)
				{
					result[rowInx][colInx] = cur.getString(colInx);
				}
				cur.moveToNext();
			}
			cur.close();
		}
		catch (Exception e)
		{
			retQuery[0] = -1;
			return null;
		}
		finally
		{
			// 중간에 null 로 바로 리턴할 때에 커서가 close 되지 않았던 문제 해결
			if (null != cur)
			{
				try
				{
					cur.close();
				}
				catch (Exception e)
				{
					// 아무것도 없다.
				}
			}
			closeDB();
		}
		return result;
	}
	
	public int execute(String sql)
	{
		return execute(sql, null);
	}
	
	public int execute(String sql, Object[] bindArgs)
	{
		if (openDB() < 0)
		{
			return -1;
		}
		
		try
		{
			if(bindArgs != null)
			{
				mDb.execSQL(sql, bindArgs);
			}
			else
			{
				mDb.execSQL(sql);
			}
		}
		catch (Exception e)
		{
			return -2;
		}

		return 0;
	}
	
	public int execute(ArrayList<String> aSql)
	{
		if (openDB() < 0)
		{
			return -1;
		}
		
		try
	    {
		    for (String sql : aSql)
		    {
			    mDb.execSQL(sql);
		    }
	    }
	    catch (Exception e)
	    {
		    return -2;
	    }

		return 0;
	}
	
	public Cursor query(String sql)
	{
		return query(sql, null);
	}
	
	public Cursor query(String sql, String[] selectionArgs)
	{
		if (openDB() < 0)
		{
			return null;
		}
		
		try
		{
			return mDb.rawQuery(sql, selectionArgs);
		}
		catch (Exception e)
		{
			return null;
		}
	}
	
	/**
	 * This class helps open, create, and upgrade the database file.
	 */
	private class DatabaseHelper extends SQLiteOpenHelper
	{
		
		DatabaseHelper(Context aCtx, String aDbname, int aDbVersion)
		{
			super(aCtx, aDbname, null, aDbVersion);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db)
		{
			// create table
			
			if (sDbInfo != null)
			{
				for (String SQLCrtTbl : sDbInfo.mSQLCreateTable)
				{
					try
					{
						db.execSQL(SQLCrtTbl);
					}
					catch (Exception e)
					{
						// Log.e("DB", "failed to create table : " + SQLCrtTbl);
					}
				}
			}
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
		{
			if (null != sDbInfo.mDbHelper)
			{
				sDbInfo.mDbHelper.onUpgrade(db, oldVersion, newVersion);
			}
		}
	}
	
	/** 생성자 (private) */
	private DbManagerSingleton()
	{
	}
	
	private int openDB()
	{
		if (mDbHelper == null)
		{
			mDbHelper = new DatabaseHelper(sCtx, sDbInfo.mDbName, sDbInfo.mDbVersion);
		}
		
		try
		{
			mDb = mDbHelper.getWritableDatabase();
		}
		catch (Exception e)
		{
			return -1;
		}
		
		return 0;
	}
	
	public void closeDB()
	{
		if (mDbHelper != null)
		{
			mDbHelper.close();
		}
	}
	
	public void beginTransaction()
	{
		if(null == mDb)
		{
			if (openDB() < 0)
			{
				return;
			}
		}
		
		mDb.beginTransaction();
	}
	
	public void setTransactionSuccessful()
	{
		if(null == mDb)
		{
			return;
		}
		
		mDb.setTransactionSuccessful();
	}
	
	public void endTransaction()
	{
		if(null == mDb)
		{
			return;
		}
		
		mDb.endTransaction();
	}
}
