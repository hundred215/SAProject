package com.namleesin.smartalert.graph;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.namleesin.smartalert.R;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.settingmgr.ListViewItem;

import java.util.ArrayList;
import java.util.List;

public class SAGraphActivity extends Activity
{
    private int mYMaxCount = 1000;
    private ListView mListView = null;
    private GraphListViewAdapter mAdapter = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_graph);

        mListView = (ListView) findViewById(R.id.graphlistview);

        ProgressBar progressBarempty = (ProgressBar)this.findViewById(R.id.emptyprogress);
        mListView.setEmptyView(progressBarempty);

        AppListAsyncTask appListAsyncTask = new AppListAsyncTask();
        appListAsyncTask.execute();

        drawGraph();
	}

    private void drawGraph()
    {
        GraphView graph = (GraphView) findViewById(R.id.graph);

        ArrayList<String> daylist = new ArrayList<String>();
        DbHandler handler = new DbHandler(getApplication());
        Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_DAILY_NOTI_INFO, null);
        if(cursor == null)
            return;
        cursor.moveToFirst();

        do
        {
            String day = cursor.getString(0);
            String count = cursor.getString(1);
            daylist.add(count);

            if(mYMaxCount < Integer.valueOf(count).intValue())
            {
                mYMaxCount = Integer.valueOf(count).intValue();
            }
        }
        while(cursor.moveToNext());

        cursor.close();

        while(daylist.size() < 31)
        {
            daylist.add("0");
        }

        DataPoint[] points = new DataPoint[31];
        for (int i = 0; i < points.length; i++)
        {
            points[i] = new DataPoint(i, Integer.valueOf(daylist.get(30-i)));
        }

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<DataPoint>(points);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<DataPoint>(points);
        PointsGraphSeries<DataPoint> series3 = new PointsGraphSeries<DataPoint>(points);

        series1.setDrawBackground(true);
        series1.setBackgroundColor(Color.rgb(3, 40, 55));
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        paint.setLinearText(true);
        paint.setColor(Color.rgb(245, 181, 55));
        paint.setPathEffect(new DashPathEffect(new float[]{8, 5}, 0));
        series1.setCustomPaint(paint);

        series2.setSpacing(100);
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.rgb(245, 181, 55));
        series2.setColor(Color.rgb(237, 238, 239));

        series3.setSize(10);
        series3.setColor(Color.rgb(245, 181, 55));

        // styling viewport
        graph.getViewport().setBackgroundColor(Color.rgb(3, 70, 98));
        graph.getGridLabelRenderer().setHorizontalLabelsColor(Color.WHITE);

        // hide label
        graph.getGridLabelRenderer().setVerticalLabelsVisible(false);

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(1);
        graph.getViewport().setMaxY(mYMaxCount + 100);

        graph.getViewport().setXAxisBoundsManual(false);
        graph.getViewport().setMinX(26);
        graph.getViewport().setMaxX(30);

        // enable scaling
        graph.getViewport().setScalable(true);

        // custom label formatter to show currency "EUR"
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter()
        {
            @Override
            public String formatLabel(double value, boolean isValueX)
            {
                if (isValueX)
                {
                    double ret = 30 - value;
                    if(ret >= 0 && ret < 1)
                    {
                        return "오늘";
                    }
                    else
                    {
                        // show normal x values
                        return super.formatLabel(30-value, isValueX) + " 일전";
                    }
                }
                else
                {
                    // show currency for y values
                    return super.formatLabel(value, isValueX);
                }
            }
        });

        graph.addSeries(series1);
        graph.addSeries(series2);
        graph.addSeries(series3);
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
            mProgressBar = (ProgressBar)findViewById(R.id.progressbar);

            mPm = SAGraphActivity.this.getPackageManager();
            mAppList = mPm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES
                    | PackageManager.GET_DISABLED_COMPONENTS);

            mProgressBar.setMax(mAppList.size());
        }

        @Override
        protected ArrayList<ListViewItem> doInBackground(Void... params)
        {
            int i = 0;
            ListViewItem addInfo = null;
            ApplicationInfo info = null;
            ArrayList<ListViewItem> listAppInfoData = new ArrayList<ListViewItem>();

            DbHandler handler = new DbHandler(SAGraphActivity.this);
            Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_PACKAGE_NOTI_INFO, null);
            cursor.moveToFirst();
            String packagename = null;
            String noticount = null;

            do
            {
                addInfo = new ListViewItem();
                packagename = cursor.getString(0);
                noticount = cursor.getString(1);

                addInfo.mPackageName = packagename;
                addInfo.mNotiCount = Integer.valueOf(noticount).intValue();

                if(mMaxCount < Integer.valueOf(noticount).intValue())
                {
                    mMaxCount = Integer.valueOf(noticount).intValue();
                }
                listAppInfoData.add(addInfo);
            }
            while(cursor.moveToNext());

            for (ApplicationInfo app : mAppList)
            {
                    for(ListViewItem item : listAppInfoData)
                    {
                        if(item.mPackageName.equals(app.packageName))
                        {
                            if(app.loadLabel(mPm).toString().length() == 0)
                            {
                                item.mAppName = app.packageName;
                            }
                            else
                            {
                                item.mAppName = app.loadLabel(mPm).toString();
                            }

                            item.mAppIcon =  app.loadIcon(mPm);
                            break;
                        }
                    }

                i++;
                this.publishProgress(i);
            }

            cursor.close();

            return listAppInfoData;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mProgressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(ArrayList<ListViewItem> aListArray)
        {
            mAdapter = new GraphListViewAdapter(SAGraphActivity.this);
            mAdapter.setData(aListArray);
            mAdapter.setMacCount(mMaxCount);
            mListView.setAdapter(mAdapter);

            mProgressBar.setVisibility(View.GONE);
        }
    }
}
