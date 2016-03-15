package com.namleesin.smartalert.dbmgr;

import java.util.ArrayList;
import java.util.regex.Matcher;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;

public class DbWorker
{
	private String[][] queryTable = null;
	
	private static final String DELIMETER = "%s";
	private static final String STR_SELECT = "SELECT";
	
	public boolean isReady()
	{
		if (DbManagerSingleton.getInstance() == null)
		{
			return false;
		}
		// initiated successfully
		return true;
	}
	
	// initiate Database
	public int initDb(Context aCtx, DbInfo aDbInfo)
	{
		if (0 == DbManagerSingleton.createInstance(aCtx, aDbInfo))
		{
			return 0;
		}
		
		return -1;
	}

	// execute SQL
	public int executeSQL(String aSQL, String[] aParameter)
	{
		String sql;
		int ret = -1;
		int[] retQuery = new int[1];
		
		DbManagerSingleton dbm = DbManagerSingleton.getInstance();
		if (null == dbm)
		{
			return ret; // not ready. must executy initDB
		}
		
		sql = makeSQL(aSQL, aParameter);
		
		// Log.d("DB", "SQL : " + sql);
		
		if (sql == null)
		{
			return ret; // invalid SQL
		}
		
		// see if sql is SELECT or not
		if (sql.substring(0, 6).equalsIgnoreCase(STR_SELECT))
		{
			// SELECT
			queryTable = dbm.executeQuerySQL(sql, retQuery);
			if (retQuery[0] == 0)
			{
				ret = 0;
			}
			else
			{
				ret = -1;
			}
		}
		else
		{
			// not SELECT
			ret = dbm.executeSQL(sql);
		}
		return ret;
	}
	
	public String[][] getResultTable()
	{
		return queryTable;
	}
	
	public String makeSQL(String aSQL, String[] aParameter)
	{
		String sql;
		String stmt;
		String buf;
		
		int i;
		int argc;
		
		if (aSQL == null || 0 == aSQL.length())
		{
			return null;
		}
		
		sql = aSQL;
		stmt = sql;
		if (aParameter != null)
		{
			argc = aParameter.length;
		}
		else
		{
			argc = 0;
		}
		
		for (i = 0, stmt = sql; i < argc; i++)
		{
			buf = escapedString(aParameter[i]);
			// SKMOON, 2011-03-08, replaceFirst ??reg exp ???�기 ?�문???��? 문자??�??��? escape 처리 ?�요??
			if (null == buf)
			{
				break;
			}
			stmt = sql.replaceFirst(DELIMETER, Matcher.quoteReplacement(buf));
			sql = stmt;
		}
		
		if (i != argc)
		{
			// Log.d("DB", "illegal SQL format");
			return null;
		}
		return stmt;
	}
	
	/**
	 * escaping single quote character "'" with sqlEscapeString method
	 * and remove first and last "'" chars from the processed string.
	 */
	private String escapedString(String Parameter)
	{
		int escape_ch = '\'';
		String buf;
		
		if (Parameter != null && Parameter.indexOf(escape_ch) >= 0)
		{
			buf = DatabaseUtils.sqlEscapeString(Parameter);
			return buf.substring(1, buf.length() - 1);
		}
		return Parameter;
	}
	
	DbManagerSingleton mDb;
	
	public int execute(String sql)
	{
		return execute(sql, null);
	}
	
	public int execute(String sql, Object[] bindArgs)
	{
		if (null == mDb)
		{
			mDb = DbManagerSingleton.getInstance();
			if (null == mDb)
			{
				return -1;
			}
		}
		
		return mDb.execute(sql, bindArgs);
	}
	
	public int excute(ArrayList<String> aSql)
	{
		if (null == mDb)
		{
			mDb = DbManagerSingleton.getInstance();
			if (null == mDb)
			{
				return -1;
			}
		}
		
		return mDb.execute(aSql);
	}
	
	public Cursor query(String sql)
	{
		return query(sql, null);
	}
	
	public Cursor query(String sql, String[] selectionArgs)
	{
		if (null == mDb)
		{
			mDb = DbManagerSingleton.getInstance();
			if (null == mDb)
			{
				return null;
			}
		}
		
		return mDb.query(sql, selectionArgs);
	}
	
	public void beginTransaction()
	{
		if (null == mDb)
		{
			mDb = DbManagerSingleton.getInstance();
			if (null == mDb)
			{
				return;
			}
		}
		
		mDb.beginTransaction();
	}
	
	public void setTransactionSuccessful()
	{
		if (null == mDb)
		{
			mDb = DbManagerSingleton.getInstance();
			if (null == mDb)
			{
				return;
			}
		}
		
		mDb.setTransactionSuccessful();
	}
	
	public void endTransaction()
	{
		if (null == mDb)
		{
			mDb = DbManagerSingleton.getInstance();
			if (null == mDb)
			{
				return;
			}
		}
		
		mDb.endTransaction();
	}
	
	public void close()
	{
		if (null != mDb)
		{
			mDb.closeDB();
			mDb = null;
		}
	}
}
