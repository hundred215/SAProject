package com.namleesin.smartalert.dbmgr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DbManager implements DbInfo.DbHelper
{
	public static final String DB_NAME = "smartalert.db";
	public static final int DB_VERSION = 2;
	
	private Context mContext;
	private DbWorker mDb;
	private DbInfo mDbInfo;
	private List<String> mSqlList;
	
	/**
	 * DB 관리자 생성자
	 * 
	 * @param context
	 */
	public DbManager(Context context)
	{
		mContext = context.getApplicationContext();
		initDatabase();
	}
	
	/**
	 * DB 초기화
	 * 
	 * @return 실패 if 0 >, 성공 otherwise
	 */
	public int initDatabase()
	{
		initTableSqlList();
		
		mDb = new DbWorker();
		mDbInfo = new DbInfo();
		mDbInfo.setDbVersion(DbManager.DB_VERSION);
		mDbInfo.setDbName(DbManager.DB_NAME);
		mDbInfo.setSQLTable(mSqlList);
		mDbInfo.setDbHelperListener(this);
		
		return mDb.initDb(mContext, mDbInfo);
	}
	
	/**
	 * DB 종료
	 */
	public void closeDatabase()
	{
		mDb.close();
		mDb = null;
		mDbInfo = null;
	}
	
	private void initTableSqlList()
	{
		mSqlList = new ArrayList<String>();
				
		addNotiInfoTable();
		addKeywordFilterTable();
		addPackageFilterTable();
		
		addSqlList();
	}
	
	private void addNotiInfoTable()
	{
		final String[] query =
			{
				"CREATE TABLE noti_info_table (" + 
						"noti_package varchar(255) not null, " +
						"noti_titletxt varchar(255) not null, " + 
						"noti_subtxt text not null, " +
						"noti_id varchar(255) not null, " +
						"noti_key varchar(255) not null, " +
						"noti_time varchar(255) not null, " +
						"noti_status integer not null, " +
						"noti_filter varchar(255) not null, " +
						"noti_url_status integer not null);"
			};
		
		Collections.addAll(mSqlList, query);
	}
	
	private void addKeywordFilterTable()
	{
		final String[] query = 
			{
				"CREATE TABLE keyword_filter_table (" +
						"keyword varchar(255) not null primary key, " + 
						"keyword_status integer not null);"
			};
		
		Collections.addAll(mSqlList, query);
	}
	
	private void addPackageFilterTable()
	{
		final String[] query = 
			{
				"CREATE TABLE package_filter_table (" +
						"package varchar(255) not null primary key, " + 
						"pakcage_status integer not null);"
			};
		
		Collections.addAll(mSqlList, query);
	}
	
	private void addSqlList()
	{
		final String[] q = {
				// create table				
		};
		
		Collections.addAll(mSqlList, q);
	}
	
	/**
	 * 사용하는 쪽에서 Cursor 사용 시 동기화 시켜주고, Cursor 사용 후 반드시 close 한다.
	 * 
	 * @param sql
	 * @return
	 */
	public Cursor query(String sql)
	{
		return mDb.query(sql);
	}
	
	/**
	 * 사용하는 쪽에서 Cursor 사용 시 동기화 시켜주고, Cursor 사용 후 반드시 close 한다.
	 * 
	 * @param sql
	 * @param selectionArgs
	 * @return
	 */
	public Cursor query(String sql, String[] selectionArgs)
	{
		return mDb.query(sql, selectionArgs);
	}
	
	/**
	 * insert, update와 같이 리턴 값이 필요 없는 쿼리 실행
	 * 
	 * @param sql
	 */
	public int execute(String sql)
	{
		return mDb.execute(sql);
	}
	
	/**
	 * insert, update와 같이 리턴 값이 필요 없는 쿼리 실행
	 * 
	 * @param sql
	 * @param bindArgs
	 * @return
	 */
	public int execute(String sql, Object[] bindArgs)
	{
		return mDb.execute(sql, bindArgs);
	}
	
	public int execute(ArrayList<String> aSql)
	{
		return mDb.excute(aSql);
	}
	
	public String makeSQL(String sql, String[] aParam)
	{
		return mDb.makeSQL(sql, aParam);
	}
	
	/**
	 * 트랜잭션 시작. 중첩된 트랙잭션 가능 함
	 */
	public void beginTransaction()
	{
		mDb.beginTransaction();
	}
	
	/**
	 * 트랜잭션 성공을 알림
	 */
	public void setTransactionSuccessful()
	{
		mDb.setTransactionSuccessful();
	}
	
	/**
	 * 트랜잭션 종료
	 */
	public void endTransaction()
	{
		mDb.endTransaction();
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		if(null == db)
		{
			return;
		}

		switch(newVersion)
		{
			case 1:
				break;
			case 2:
				break;
			default:
				break;			
		}
	}
}
