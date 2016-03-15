package com.namleesin.smartalert.main;

import java.util.ArrayList;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.DataSetObserver;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.namleesin.smartalert.R;

public class NotiDataListAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;
	private PackageManager mPkgMgr;
	private ArrayList<NotiInfoData> mNotiData = new ArrayList<>();
	public NotiDataListAdapter(Context context)
	{
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mPkgMgr = context.getPackageManager();
	}

	@Override
	public int getCount() {
		return mNotiData.size();
	}

	@Override
	public Object getItem(int position) {
		return mNotiData.get(position);


	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public void setData(ArrayList<NotiInfoData> data)
	{
		if(data == null)
			return;

		if(mNotiData != null) {
			mNotiData.clear();
			mNotiData.addAll(data);
		}
	}
	
	private Drawable loadIcon(String pkg_name)
	{
		try {
			return mPkgMgr.getApplicationIcon(pkg_name);
		}
		catch (NameNotFoundException e) {
		}
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(convertView == null)
		{
			convertView = mInflater.inflate(R.layout.layout_mian_list_item, parent, false);
		}

		Log.d("NJ LEE", "mNotiData : " + mNotiData.get(position).pkgName);
		TextView name = (TextView) convertView.findViewById(R.id.app_name);
		name.setText(mNotiData.get(position).appName);
		ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		try {
			icon.setImageDrawable(mPkgMgr.getApplicationIcon(mNotiData.get(position).pkgName));
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		TextView total = (TextView) convertView.findViewById(R.id.cnt);
		String count = String.format("%d/ %d", mNotiData.get(position).getLikeCnt(), mNotiData.get(position).getTotalCnt());
		total.setText(count);

		convertView.setTag(mNotiData.get(position));
		return convertView;
	}
}
