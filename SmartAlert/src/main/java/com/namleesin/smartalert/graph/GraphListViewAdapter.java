package com.namleesin.smartalert.graph;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.data.PackData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.settingmgr.ListViewItem;

import java.util.ArrayList;

/**
 * Created by namjinha on 2016-02-23.
 */
public class GraphListViewAdapter extends BaseAdapter
{
    private int mMaxCount = 0;
    private class ViewHolder
    {
        public ImageView imageview;
        public TextView textview;
        public ProgressBar progressBar;
        public TextView conttextview;
        public ToggleButton blockbtn;
    }

    private Context mContext = null;
    private ArrayList<ListViewItem> mListData = new ArrayList<ListViewItem>();
    private ArrayList<PackData> mAppBlockList = new ArrayList<PackData>();

    public GraphListViewAdapter(Context mContext)
    {
        super();
        this.mContext = mContext;
    }

    public void setData(ArrayList<ListViewItem> aListData)
    {
        this.mListData = aListData;
    }

    public void setMacCount(int aMaxCount)
    {
        mMaxCount = aMaxCount;
    }

    @Override
    public int getCount()
    {
        return mListData.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        if (convertView == null)
        {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.graph_listitem, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.appicon);
            holder.textview = (TextView) convertView.findViewById(R.id.appname);
            holder.conttextview = (TextView) convertView.findViewById(R.id.appcounttxt);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressbar);
            holder.blockbtn = (ToggleButton) convertView.findViewById(R.id.blockbtn);

            holder.progressBar.setMax(mMaxCount);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.blockbtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DbHandler handler = new DbHandler(mContext);

                PackData packdata = new PackData();
                packdata.packagename = mListData.get(position).mPackageName;

                ToggleButton checkstate = (ToggleButton)v.findViewById(R.id.blockbtn);

                if (true == checkstate.isChecked())
                {
                    handler.insertDB(DBValue.TYPE_INSERT_PACKAGEFILTER, packdata);
                    checkstate.setChecked(true);
                }
                else
                {
                    handler.deleteDB(DBValue.TYPE_DELETE_FILTER_APP, packdata);
                    checkstate.setChecked(false);
                }
            }
        });

        ListViewItem data = mListData.get(position);

        if (data.mAppIcon != null)
        {
            holder.imageview.setImageDrawable(data.mAppIcon);
        }
        else
        {
            holder.imageview.setImageResource(R.drawable.ic_launcher);
        }

        holder.textview.setText(data.mAppName);
        holder.conttextview.setText("" + data.mNotiCount);

        holder.progressBar.setProgress(data.mNotiCount);

        DbHandler handler = new DbHandler(this.mContext);
        Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTER_PACKAGE, data.mPackageName);

        if(null != cursor && cursor.getCount() > 0)
        {
            holder.blockbtn.setChecked(true);
        }
        else
        {
            holder.blockbtn.setChecked(false);
        }

        return convertView;
    }
}


