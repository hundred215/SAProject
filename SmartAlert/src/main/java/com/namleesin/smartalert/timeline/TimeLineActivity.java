package com.namleesin.smartalert.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.ActionBarView;
import com.namleesin.smartalert.dbmgr.DBValue;

import java.util.ArrayList;

/**
 * Created by comus1200 on 2015. 12. 24..
 */
public class TimeLineActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<TimelineData>>, View.OnClickListener {
    public static String TIMELINE_TYPE = "type";
    public static String TIMELINE_PKG = "pkg";

    public static final int TYPE_PACKAGE = 0;
    public static final int TYPE_FAVORITE = 1;
    public static final int TYPE_HATE = 2;
    public static final int TYPE_TIME = 3;

    private int type;
    private String param;
    private int queryType;
    private TimelineListAdapter mAdapter;
    private ListView mTimelineListView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ActionBarView actionbar = (ActionBarView) findViewById(R.id.actionbar);
        actionbar.setOnFinishButtonListener(this);

        Intent intent = getIntent();
        type = intent.getIntExtra(TIMELINE_TYPE, 0);

        switch (type)
        {
            case TYPE_PACKAGE:
                param = intent.getStringExtra(TIMELINE_PKG);
                queryType = DBValue.TYPE_SELECT_PACKAGE_INFO;
                break;
            case TYPE_FAVORITE:
                queryType = DBValue.TYPE_SELECT_LIKE_PKG_INFO;
                actionbar.setTitleText(getString(R.string.STR_ACTION_TITLE_TXT03));
                break;
            case TYPE_HATE:
                queryType = DBValue.TYPE_SELECT_DISLIKE_PKG_INFO;
                actionbar.setTitleText(getString(R.string.STR_ACTION_TITLE_TXT02));
                break;
            default:
                queryType = DBValue.TYPE_SELECT_NOTI_INFO;
                actionbar.setTitleText(getString(R.string.STR_ACTION_TITLE_TXT01));
                break;
        }

        mTimelineListView = (ListView) findViewById(R.id.timeline_list);

        mAdapter = new TimelineListAdapter(this);
        mTimelineListView.setAdapter(mAdapter);

        LinearLayout lview = (LinearLayout)findViewById(R.id.emptytimelinelist);
        mTimelineListView.setEmptyView(lview);

        getSupportLoaderManager().initLoader(1001, null, this).forceLoad();
    }

    @Override
    public Loader<ArrayList<TimelineData>> onCreateLoader(int id, Bundle args) {

        return new TimelineDataLoader(this, queryType, param);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TimelineData>> loader, ArrayList<TimelineData> data) {
        if(data != null && data.size() > 0)
        {
            mAdapter.setData(data);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TimelineData>> loader) {

    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.back_arrow:
                finish();
                break;
            default:
                break;
        }
    }
}
