package com.namleesin.smartalert.settingmgr;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.data.PackData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.notimgr.NotificationListener;

import java.util.ArrayList;

public class SetAppListAdapter extends BaseAdapter
{
    private class ViewHolder
    {
        public ImageView imageview;
        public TextView textview;
        public CheckBox checkBox;
    }

    private Context mContext = null;
    private ArrayList<ListViewItem> mListData = new ArrayList<ListViewItem>();

    public SetAppListAdapter(Context mContext)
    {
        super();
        this.mContext = mContext;
    }

    public void setData(ArrayList<ListViewItem> aListData)
    {
        this.mListData = aListData;
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
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.noti_app_listitem, null);

            holder.imageview = (ImageView) convertView.findViewById(R.id.appicon);
            holder.textview = (TextView) convertView.findViewById(R.id.appname);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.checkstate);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbHandler handler = new DbHandler(mContext);

                PackData packdata = new PackData();
                packdata.packagename = mListData.get(position).mPackageName;

                boolean checked = !mListData.get(position).mIsChecked;
                mListData.get(position).mIsChecked = checked;
                if (false == checked)
                {
                    handler.deleteDB(DBValue.TYPE_DELETE_FILTER_APP, packdata);
                }
                else
                {
                    handler.insertDB(DBValue.TYPE_INSERT_PACKAGEFILTER, packdata);
                }

                Intent update = new Intent(NotificationListener.UPDATE_FILTER);
                mContext.sendBroadcast(update);
            }
        });

        ListViewItem data = mListData.get(position);

        if (data.mAppIcon != null)
        {
            holder.imageview.setImageDrawable(data.mAppIcon);
        }
        else
        {
            holder.imageview.setVisibility(View.GONE);
        }

        holder.textview.setText(data.mAppName);
        holder.checkBox.setChecked(data.mIsChecked);

        return convertView;
    }
}


