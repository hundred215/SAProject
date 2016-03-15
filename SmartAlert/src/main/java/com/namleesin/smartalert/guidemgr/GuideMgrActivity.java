package com.namleesin.smartalert.guidemgr;


import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

import com.namleesin.smartalert.R;

public class GuideMgrActivity extends Activity
{
    private SectionsPagerAdapter mSectionsPagerAdapter = null;
    private static ViewPager mViewPager = null;
    private static Activity mActivity = null;
    private int mNextIdx = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guidemgr);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        mActivity = GuideMgrActivity.this;

        Button gonebtn = (Button)findViewById(R.id.nextbtn);
        gonebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mNextIdx = mViewPager.getCurrentItem();

                mNextIdx++;

                if(mNextIdx > 2)
                {
                    setResult(Activity.RESULT_OK);
                    finish();
                    return;
                }
                mViewPager.setCurrentItem(mNextIdx);
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

    public static class Wizard01Fragment extends Fragment
    {
        public Wizard01Fragment()
        {
        }

        public static Wizard01Fragment newInstance()
        {
            Wizard01Fragment fragment = new Wizard01Fragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_guide01, container, false);
            return rootView;
        }
    }

    public static class Wizard02Fragment extends Fragment
    {
        public Wizard02Fragment()
        {
        }

        public static Wizard02Fragment newInstance()
        {
            Wizard02Fragment fragment = new Wizard02Fragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_guide02, container, false);
            return rootView;
        }
    }

    public static class Wizard03Fragment extends Fragment
    {
        public Wizard03Fragment()
        {
        }

        public static Wizard03Fragment newInstance()
        {
            Wizard03Fragment fragment = new Wizard03Fragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState)
        {
            View rootView = inflater.inflate(R.layout.fragment_guide03, container, false);
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            //return PlaceholderFragment.newInstance(position + 1);

            switch(position)
            {
                case 0: return Wizard01Fragment.newInstance();
                case 1: return Wizard02Fragment.newInstance();
                case 2: return Wizard03Fragment.newInstance();
                default : return Wizard01Fragment.newInstance();
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
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
