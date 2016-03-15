package com.namleesin.smartalert.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;

public class NotiDataLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<NotiInfoData>>
{
	private PackageManager mPkgMgr;
	private DbHandler mDbHandler;

	private ArrayList<NotiInfoData> noti_data_list = null;
	private Comparator<NotiInfoData> compator = new Comparator<NotiInfoData>() {

		@Override
		public int compare(NotiInfoData lhs, NotiInfoData rhs) {
			// TODO Auto-generated method stub
			return Integer.compare(lhs.totalCnt, rhs.totalCnt);
		}
	};
	
	
	public NotiDataLoader(Context context) 
	{
		super(context);
		mPkgMgr = context.getPackageManager();
		mDbHandler = new DbHandler(context);
	}

	@Override
	public ArrayList<NotiInfoData> loadInBackground() 
	{
		noti_data_list = new ArrayList<NotiInfoData>();
		List<ApplicationInfo> pkg_list_info = mPkgMgr.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
		for(ApplicationInfo info : pkg_list_info)
		{
			int total = mDbHandler.selectCountDB(DBValue.TYPE_SELECT_PACKAGE_INFO_COUNT, info.packageName);
			int like = mDbHandler.selectCountDB(DBValue.TYPE_SELECT_LIKE_PACKAGE_COUNT, info.packageName);

			if(total>0) {
				NotiInfoData data = new NotiInfoData()
						.setPkgName(info.packageName)
						.setAppName((String) info.loadLabel(mPkgMgr))
						.setLikeCnt(like)
						.setTotalCnt(total);
				noti_data_list.add(data);
			}
		}
		
		Collections.sort(noti_data_list, Collections.reverseOrder(compator));

		return noti_data_list;
	}

	@Override protected void onStartLoading() {
		if (noti_data_list != null) {
			// If we currently have a result available, deliver it
			// immediately.
			deliverResult(noti_data_list);
		}
	}

	@Override public void deliverResult(ArrayList<NotiInfoData> datas) {

		super.deliverResult(datas);
	}
}
