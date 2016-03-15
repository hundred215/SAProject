package com.namleesin.smartalert.settingmgr;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.PullDownInputView;
import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.data.PackData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.main.MainValue;
import com.namleesin.smartalert.notimgr.NotificationListener;
import com.namleesin.smartalert.utils.AppInfo;

import java.util.ArrayList;
import java.util.List;

public class NotiSettingActivity extends Activity
{
    private SectionsPagerAdapter mSectionsPagerAdapter = null;
    private ViewPager mViewPager = null;
    private static Activity mActivity = null;
    private int mNextIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        Intent i = getIntent();
        int type = i.getIntExtra(MainValue.ACTIVITY_TYPE, MainValue.TYPE_INIT_NOTI_SETTING);
        if(type == MainValue.TYPE_MENU_NOTI_SETTING)
        {
            setContentView(R.layout.activity_spamnotisetting);
        }
        else
        {
            setContentView(R.layout.activity_initspamnotisetting);
        }

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mActivity = NotiSettingActivity.this;

        if(type == MainValue.TYPE_MENU_NOTI_SETTING)
        {
            int index = i.getIntExtra(MainValue.SET_INDEX_NUMBER, 0);
            mViewPager.setCurrentItem(index);
        }
        else
        {
            Button gonebtn = (Button)findViewById(R.id.nextbtn);
            gonebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNextIdx = mViewPager.getCurrentItem();

                    mNextIdx++;

                    if (mNextIdx > 2) {
                        setResult(Activity.RESULT_OK);
                        finish();
                        return;
                    }
                    mViewPager.setCurrentItem(mNextIdx);
                }
            });
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position)
            {
                case 0: return NotiSpamSetAppFragment.newInstance(mViewPager);
                case 1: return NotiSpamSetKeywordFragment.newInstance(mViewPager);
                case 2: return NotiLikeSetKeywordFragment.newInstance();
                default: return NotiSpamSetAppFragment.newInstance(mViewPager);
            }
        }

        @Override
        public int getCount()
        {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position)
        {
            switch (position)
            {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }
}
