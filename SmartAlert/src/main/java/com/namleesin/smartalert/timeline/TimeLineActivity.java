package com.namleesin.smartalert.timeline;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.ActionBarView;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;

import java.util.ArrayList;

/**
 * Created by comus1200 on 2015. 12. 24..
 */
public class TimeLineActivity extends FragmentActivity implements LoaderManager.LoaderCallbacks<ArrayList<TimelineData>>, View.OnClickListener {
    public static String TIMELINE_TYPE = "type";
    public static String TIMELINE_PKG = "pkg";

    private final int LOADER_ID = 1001;
    public static final int TYPE_PACKAGE = 0;
    public static final int TYPE_FAVORITE = 1;
    public static final int TYPE_HATE = 2;
    public static final int TYPE_TIME = 3;

    private int type;
    private String param;
    private int queryType;
    private TimelineListAdapter mAdapter;
    private ListView mTimelineListView;
    private ProgressBar mProgressbar;
    private AdView mBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timeline);
        ActionBarView actionbar = (ActionBarView) findViewById(R.id.actionbar);
        actionbar.setOnFinishButtonListener(this);
        actionbar.setOnDelButtonListener(this);

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
                actionbar.setTitleText(getString(R.string.STR_ACTION_TITLE_TXT05));
                actionbar.findViewById(R.id.title_del_btn).setVisibility(View.GONE);
                break;
        }

        mProgressbar = (ProgressBar) findViewById(R.id.progressbar);
        mTimelineListView = (ListView) findViewById(R.id.timeline_list);

        mAdapter = new TimelineListAdapter(this);
        mAdapter.setDelBtnClickListener(this);
        mTimelineListView.setAdapter(mAdapter);

        LinearLayout lview = (LinearLayout)findViewById(R.id.emptytimelinelist);
        mTimelineListView.setEmptyView(lview);

        mBanner = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mBanner.loadAd(adRequest);

        getSupportLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBanner.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBanner.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBanner.destroy();
    }

    @Override
    public Loader<ArrayList<TimelineData>> onCreateLoader(int id, Bundle args) {
        TimelineDataLoader loader = new TimelineDataLoader(this, queryType, param);
        loader.forceLoad();
        mProgressbar.setVisibility(View.VISIBLE);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<TimelineData>> loader, ArrayList<TimelineData> data) {
        mProgressbar.setVisibility(View.GONE);
        if(data==null || data.size() == 0){
            finish();
        }
        mAdapter.setData(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<TimelineData>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_timeline, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.delete:
                DbHandler db = new DbHandler(this);
                db.deleteDB(DBValue.TYPE_DELETE_TIMELINE_EACH, null);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v)
    {
        DbHandler db = new DbHandler(this);
        switch (v.getId())
        {
            case R.id.back_arrow:
                break;
            case R.id.btn_delete:
                String date = (String) v.getTag();
                Log.d("NJ LEE", "delete : "+date);
                db.deleteDB(DBValue.TYPE_DELETE_TIMELINE_EACH, date);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
                break;
            case R.id.title_del_btn:
                mProgressbar.setVisibility(View.VISIBLE);
                String param="";
                switch (type) {
                    case TYPE_PACKAGE:
                        param = "noti_package=\""+this.param+"\"";
                        break;
                    case TYPE_FAVORITE:
                        param = "noti_status = "+DBValue.STATUS_LIKE;
                        break;
                    case TYPE_HATE:
                        param = "noti_status = "+DBValue.STATUS_DISLIKE;
                        break;
                    default:
                        param = null;
                        break;
                }
                db.deleteDB(DBValue.TYPE_DELETE_TIMELINE_WITH, param);
                getSupportLoaderManager().restartLoader(LOADER_ID, null, this);
                break;
            default:
                break;
        }
    }
}
