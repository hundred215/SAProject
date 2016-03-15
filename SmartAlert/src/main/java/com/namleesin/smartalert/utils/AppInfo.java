package com.namleesin.smartalert.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.namleesin.smartalert.graph.SAGraphActivity;
import com.namleesin.smartalert.settingmgr.ListViewItem;
import com.namleesin.smartalert.settingmgr.NotiSettingActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by namjinha on 2016-02-23.
 */
public class AppInfo
{
    public static final int MENU_DOWNLOAD = 0;
    public static final int MENU_ALL = 1;
    public static int MENU_MODE = MENU_DOWNLOAD;

    public static String getVersionName(Context context)
    {
        try
        {
            PackageInfo pi= context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pi.versionName;
        }
        catch (PackageManager.NameNotFoundException e)
        {
            return "N/A";
        }
    }

    public static interface AppFilter
    {
        public void init();
        public boolean filterApp(ApplicationInfo info);
    }

    public static final AppFilter THIRD_PARTY_FILTER = new AppFilter()
    {
        public void init() {
        }
        @Override
        public boolean filterApp(ApplicationInfo info) {
            if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                return true;
            } else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                return true;
            }
            return false;
        }
    };

    public static ArrayList<ListViewItem> getApplicatonInfoList(Context aContext)
    {
        ArrayList<ListViewItem> listAppInfoData = new ArrayList<ListViewItem>();
        PackageManager pm = aContext.getPackageManager();
        List<ApplicationInfo> appList = pm
                .getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                        | PackageManager.GET_DISABLED_COMPONENTS);

        AppFilter filter;
        switch (MENU_MODE)
        {
            case MENU_DOWNLOAD:
                filter = THIRD_PARTY_FILTER;
                break;
            default:
                filter = null;
                break;
        }

        if (filter != null)
        {
            filter.init();
        }

        ListViewItem addInfo = null;
        ApplicationInfo info = null;
        for (ApplicationInfo app : appList)
        {
            info = app;
            if (filter == null || filter.filterApp(info))
            {
                addInfo = new ListViewItem();
                // App Icon
                addInfo.mAppIcon = app.loadIcon(pm);
                // App Name
                addInfo.mAppName = app.loadLabel(pm).toString();

                listAppInfoData.add(addInfo);
            }
        }

        return listAppInfoData;
    }
}
