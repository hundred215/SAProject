package com.namleesin.smartalert.settingmgr;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by namjinha on 2016-01-05.
 */
public class ListViewItem
{
    public String mAppName = null;
    public Drawable mAppIcon  = null;
    public String mPackageName = null;
    public int mNotiCount = 0;
    public int mFilterState = 0;
/*
    public ListViewItem(String aAppName, Drawable aAppIcon)
    {
        this.mAppName = aAppName;
        this.mAppIcon = aAppIcon;
    }

    public String getAppName()
    {
        return this.mAppName;
    }

    public Drawable getAppIcon()
    {
        return this.mAppIcon;
    }
    */
}
