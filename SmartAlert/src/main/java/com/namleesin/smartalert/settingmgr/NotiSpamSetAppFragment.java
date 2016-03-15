package com.namleesin.smartalert.settingmgr;

import android.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.data.PackData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namjinha on 2016-03-07.
 */
public class NotiSpamSetAppFragment extends Fragment implements AdapterView.OnItemClickListener
{
    private ListView mListView = null;
    private SetAppListAdapter mAdapter = null;
    private View mRootView = null;
    private static ViewPager mViewPager = null;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        CheckBox checkstate = (CheckBox) view.findViewById(R.id.checkstate);
        if (true == checkstate.isChecked())
        {
            checkstate.setChecked(false);
        }
        else
        {
            checkstate.setChecked(true);
        }
    }

    private class AppListAsyncTask extends AsyncTask<Void, Integer, ArrayList<ListViewItem>>
    {
        private int mMaxCount = 0;
        private ProgressBar mProgressBar = null;
        private List<ApplicationInfo> mAppList = null;
        private PackageManager mPm = null;

        @Override
        protected void onPreExecute()
        {
            mProgressBar = (ProgressBar)mRootView.findViewById(R.id.progressbar);

            mPm = getActivity().getPackageManager();
            mAppList = mPm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                    | PackageManager.GET_DISABLED_COMPONENTS);

            mProgressBar.setMax(mAppList.size());
        }

        @Override
        protected ArrayList<ListViewItem> doInBackground(Void... params)
        {
            AppInfo.AppFilter filter;
            switch (AppInfo.MENU_MODE)
            {
                case AppInfo.MENU_DOWNLOAD:
                    filter = AppInfo.THIRD_PARTY_FILTER;
                    break;
                default:
                    filter = null;
                    break;
            }

            if (filter != null)
            {
                filter.init();
            }

            int i = 0;
            ApplicationInfo info = null;
            ArrayList<ListViewItem> listAppInfoData = new ArrayList<ListViewItem>();

            ArrayList<String> packagelist = new ArrayList<String>();
            DbHandler handler = new DbHandler(getActivity().getApplicationContext());
            Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERPKG_INFO, null);
            if(null != cursor && cursor.getCount() > 0)
            {
                cursor.moveToFirst();
                do
                {
                    String packagename = cursor.getString(0);
                    packagelist.add(packagename);
                }
                while (cursor.moveToNext());
            }

            for (ApplicationInfo app : mAppList)
            {
                info = app;
                if (filter == null || filter.filterApp(info))
                {
                    ListViewItem addInfo = new ListViewItem();
                    addInfo.mPackageName = app.packageName;
                    addInfo.mAppIcon = app.loadIcon(mPm);
                    addInfo.mAppName = app.loadLabel(mPm).toString();

                    if(true == packagelist.contains(addInfo.mPackageName))
                    {
                        addInfo.mFilterState = 1;
                    }

                    listAppInfoData.add(addInfo);
                }
                i++;
                this.publishProgress(i);
            }

            return listAppInfoData;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<ListViewItem> aListArray)
        {
            mAdapter = new SetAppListAdapter(getActivity().getApplicationContext());
            mAdapter.setData(aListArray);
            mListView.setAdapter(mAdapter);

            mProgressBar.setVisibility(View.GONE);
        }
    }

    public NotiSpamSetAppFragment() {
    }

    public static NotiSpamSetAppFragment newInstance(ViewPager aViewPager)
    {
        mViewPager = aViewPager;
        NotiSpamSetAppFragment fragment = new NotiSpamSetAppFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_spamnotiset_app, container, false);

        mListView = (ListView)mRootView.findViewById(R.id.listview);
        ProgressBar progressBarempty = (ProgressBar)mRootView.findViewById(R.id.emptyprogress);
        mListView.setEmptyView(progressBarempty);
        mListView.setOnItemClickListener(this);

        AppListAsyncTask appListAsyncTask = new AppListAsyncTask();
        appListAsyncTask.execute();

        Button apptabBtn = (Button) mRootView.findViewById(R.id.tab01);
        apptabBtn.setPressed(true);

        Button keywordtabBtn = (Button) mRootView.findViewById(R.id.tab02);
        keywordtabBtn.setPressed(false);
        keywordtabBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mViewPager.setCurrentItem(1);
            }
        });

        return mRootView;
    }
}
