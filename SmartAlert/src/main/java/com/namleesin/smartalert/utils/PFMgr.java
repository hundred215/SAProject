package com.namleesin.smartalert.utils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.FileObserver;

public class PFMgr
{
	static private FileObserver prefsFO = null;
	static private String lockfile_for_prefs = "/.prefs_lock";
	static private boolean isModified = true;
	static private String PREFERENCE_NAME = null;
	
	private Context m_Ctx;
	private SharedPreferences m_Preferences;
	private SharedPreferences.Editor m_PreferencesEditor;
	private FileChannel mFileChannel = null;
	private String m_pkgName = null;
	
	public PFMgr(Context ctx)
	{
		m_Ctx = ctx;
		
		if (prefsFO == null)
		{
			PFMgr.isModified = true;
			PFMgr.PREFERENCE_NAME = getDefaultSharedPreferenceName() + ".xml";
			startObserving();
		}
	}
	
	/**
	 * 패키지명이 붙은 preference 항목명을 사용하고자 쓰이는 생성자.
	 */
	public PFMgr(Context ctx, String pkgName)
	{
		m_Ctx = ctx;
		m_pkgName = pkgName;
		
		if (prefsFO == null)
		{
			PFMgr.isModified = true;
			PFMgr.PREFERENCE_NAME = getDefaultSharedPreferenceName() + ".xml";
			startObserving();
		}
	}

	private FileLock lock()
	{
		File file = new File(getSharedPrefsDirPath() + lockfile_for_prefs);
		FileLock fl = null;
		try
		{
			mFileChannel = new RandomAccessFile(file, "rw").getChannel();
			fl = mFileChannel.lock();
		}
		catch (Exception e)
		{
			;
		}
		return fl;
	}
	
	private void unlock(FileLock fl)
	{
		if(fl != null)
		{
			try
			{
				fl.release();
			} 
			catch (Exception e)
			{
				;
			}
		}
		
		if(mFileChannel != null)
		{
			try
			{
				mFileChannel.close();
			}
			catch (IOException e)
			{
				;
			}
		}
		mFileChannel = null;
	}
	
	private void startObserving()
	{
		prefsFO = new FileObserver(getSharedPrefsDirPath(), FileObserver.CLOSE_WRITE)
		{
			@Override
			public void onEvent(int event, String path)
			{
				if (PFMgr.PREFERENCE_NAME.equals(path) == false)
				{
					return;
				}

				if ((event & FileObserver.CLOSE_WRITE) != 0)
				{
					PFMgr.isModified = true;
				}
				else
				{
					; // do nothing.
				}
			}
		};
		
		prefsFO.startWatching();
	}
	
	/**
	 * Preference 를 초기화한다.
	 */
	public void clearAllValue()
	{
		FileLock fl = lock();
		m_PreferencesEditor.clear();
		m_PreferencesEditor.commit();
		unlock(fl);
	}
	
	/**
	 * 
	 * String 으로 설정 된 값을 가져온다.
	 * 
	 */
	public String getStringValue(String aKey, String aValue)
	{
		String result;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		result = m_Preferences.getString(aKey, aValue);
		unlock(fl);
		return result;
	}
	
	/**
	 * 
	 * Int 으로 설정 된 값을 가져온다.
	 * 
	 */	
	public int getIntValue(String aKey, int aValue)
	{
		int result;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		result = m_Preferences.getInt(aKey, aValue);
		unlock(fl);
		return  result;
	}	

	/**
	 * 
	 * boolean 으로 설정 된 값을 가져온다.
	 * 
	 */	
	public boolean getBooleanValue(String aKey, boolean aValue)
	{
		boolean result;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		result = m_Preferences.getBoolean(aKey, aValue);
		unlock(fl);
		return  result;
	}	

	/**
	 * 
	 * float 으로 설정 된 값을 가져온다.
	 * 
	 */	
	public float getFloatValue(String aKey, float aValue)
	{
		float result;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		result = m_Preferences.getFloat(aKey, aValue);
		unlock(fl);
		return  result;
	}
	
	/**
	 * 
	 * long 으로 설정 된 값을 가져온다.
	 * 
	 */	
	public long getLongValue(String aKey, long aValue)
	{
		long result;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		result = m_Preferences.getLong(aKey, aValue);
		unlock(fl);
		return  result;
	}
	
	
	/**
	 * 
	 * String 으로 설정한다.
	 * 
	 */	
	public boolean setStringValue(String aKey, String aValue)
	{
		boolean result = true;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		m_PreferencesEditor.putString(aKey, aValue);
		result = m_PreferencesEditor.commit();
		unlock(fl);
		
		return result;
	}
	
	/**
	 * 
	 * Int 으로 설정한다.
	 * 
	 */		
	public boolean setIntValue(String aKey, int aValue)
	{
		boolean result = true;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		m_PreferencesEditor.putInt(aKey, aValue);
		result = m_PreferencesEditor.commit();
		unlock(fl);
		
		return result;
	}
	
	/**
	 * 
	 * boolean 으로 설정한다.
	 * 
	 */		
	public boolean setBooleanValue(String aKey, boolean aValue)
	{
		boolean result = true;

		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		m_PreferencesEditor.putBoolean(aKey, aValue);
		result = m_PreferencesEditor.commit();
		unlock(fl);
		
		return result;
	}
	
	/**
	 * 
	 * float 으로 설정한다.
	 * 
	 */		
	public boolean setFloatValue(String aKey, float aValue)
	{
		boolean result = true;

		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		m_PreferencesEditor.putFloat(aKey, aValue);
		result = m_PreferencesEditor.commit();
		unlock(fl);
		
		return result;
	}
	
	/**
	 * 
	 * Long 으로 설정한다.
	 * 
	 */		
	public boolean setLongValue(String aKey, long aValue)
	{
		boolean result = true;
		
		FileLock fl = lock();
		startReloadIfChangedUnexpectedly();
		m_PreferencesEditor.putLong(aKey, aValue);
		result = m_PreferencesEditor.commit();
		unlock(fl);
		
		return result;
	}
	
	/**
	 * 해당 설정값이 이미 있는지 확인한다.
	 */
	public boolean contains(String key)
	{
		startReloadIfChangedUnexpectedly();
		return m_Preferences.contains(key);
	}
	
	/**
	 * SharedPreferencesListener를 등록한다
	 * 
	 * @param aPreferencesListener
	 * @return boolean
	 */
	public boolean registerPreferencesListener(SharedPreferences.
	                                           OnSharedPreferenceChangeListener
	                                           aPreferencesListener)
	{
		if (null == m_Preferences)
		{
			return false;
		}
		
		m_Preferences.registerOnSharedPreferenceChangeListener(aPreferencesListener);

		return true;
	}
	
	/**
	 * 등록된 SharedPreferencesListener를 해제한다.
	 * 
	 * @param aPreferencesListener
	 * @return boolean
	 */
	public boolean unregisterPreferencesListener(SharedPreferences.
	                                             OnSharedPreferenceChangeListener
	                                             aPreferencesListener)
	{
		if ((null != m_Preferences) && (null != aPreferencesListener))
		{
			m_Preferences.unregisterOnSharedPreferenceChangeListener(aPreferencesListener);

			return true;
		}
		
		return false;
	}
	
	private void startReloadIfChangedUnexpectedly()
	{
		synchronized (PFMgr.class)
		{
			if(m_Preferences == null)
			{
				m_Preferences = m_Ctx.getSharedPreferences(getDefaultSharedPreferenceName(), 0x0004);
				m_PreferencesEditor = m_Preferences.edit();
			}

			if (PFMgr.isModified)
			{
				forceReloadSharedPreferences();
			}
		}
	}
	
	private void forceReloadSharedPreferences()
	{
		File f = new File(getDefaultSharedPreferencePath());
		f.setLastModified(0);
		m_Preferences = m_Ctx.getSharedPreferences(getDefaultSharedPreferenceName(), 0x0004);
		f.setLastModified(System.currentTimeMillis());
		m_Preferences = m_Ctx.getSharedPreferences(getDefaultSharedPreferenceName(), 0x0004);
		m_PreferencesEditor = m_Preferences.edit();
		
		PFMgr.isModified = false;
	}
	
	private String getSharedPrefsDirPath()
	{
		return getAppDataDir(m_Ctx) + "/shared_prefs/";
	}
	
	public String getAppDataDir(Context context)
	{
		if (context == null)
		{
			return null;
		}

		String dataDir = null;
		PackageManager packMgr = context.getPackageManager();
		PackageInfo info;
		try 
		{
			info = packMgr.getPackageInfo(context.getPackageName(), 
			                              PackageManager.GET_CONFIGURATIONS);
			dataDir = info.applicationInfo.dataDir;
		} 
		catch (Exception e) 
		{
			return null;
		}
		return dataDir;
	}
	
	private String getDefaultSharedPreferencePath()
	{
		String name = getDefaultSharedPreferenceName() + ".xml";
		String defaultPrefsPath = getSharedPrefsDirPath() + name;

		return defaultPrefsPath;
	}
	
	private String getDefaultSharedPreferenceName()
	{
		if( m_pkgName != null ){
			return m_pkgName + "_preferences";
		}
		return m_Ctx.getPackageName() + "_preferences";
	}
}