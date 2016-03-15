package com.namleesin.smartalert.settingmgr;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;

import java.security.Key;
import java.util.ArrayList;

public class SetKeywordListAdapter extends BaseAdapter
{
    private class ViewHolder
    {
        public TextView textview;
        public Button deletebtn;
    }

    private Context mContext = null;
    private ArrayList<ListViewItem> mListData = new ArrayList<ListViewItem>();

    public SetKeywordListAdapter(Context mContext)
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
            convertView = inflater.inflate(R.layout.noti_keyword_listitem, null);

            holder.textview = (TextView) convertView.findViewById(R.id.appname);
            holder.deletebtn = (Button) convertView.findViewById(R.id.deleteBtn);

            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.deletebtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                DbHandler handler = new DbHandler(mContext);
                KeywordData keywordData = new KeywordData();
                keywordData.setKeywordata(mListData.get(position).mAppName);
                keywordData.setKeywordstatus(mListData.get(position).mFilterState);
                handler.deleteDB(DBValue.TYPE_DELETE_FILTER_KEYWORD, keywordData);

                mListData.remove(position);
                notifyDataSetChanged();
            }
        });

        ListViewItem data = mListData.get(position);

        holder.textview.setText(data.mAppName);

        return convertView;
    }
}


