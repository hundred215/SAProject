package com.namleesin.smartalert.dbmgr;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

// for initializing DB
public class DbInfo
{
	// SQL for Table
	List<String> mSQLCreateTable;
	
	// DB Name
	String mDbName;
	
	// DB Version
	int mDbVersion;
	
	// DB Helper Event
	DbHelper mDbHelper;
	
	public void setSQLTable(List<String> aSQLs)
	{
		mSQLCreateTable = aSQLs;
	}
	
	public void setDbName(String aName)
	{
		mDbName = aName;
	}
	
	public void setDbVersion(int aDbVersion)
	{
		mDbVersion = aDbVersion;
	}
	
	public void setDbHelperListener(DbHelper listener)
	{
		mDbHelper = listener;
	}
	
	public interface DbHelper
	{
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
}
