package com.namleesin.smartalert.timeline;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.namleesin.smartalert.R;
import com.namleesin.smartalert.dbmgr.DBValue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by comus1200 on 2015. 12. 30..
 */
public class TimelineListAdapter extends BaseAdapter {

    class ViewHolder
    {
        ImageView mStatusIv;
        TextView mDataTv;
        TextView mContentTv;
        TextView mAppnameTv;
        ImageView mIconIv;
    }

    private final int TYPE_ADD  = 1;
    private final int TYPE_ITEM = 2;

    private Context mCtx;
    private LayoutInflater mInflater;
    private ArrayList<TimelineData> mDataArray;
    private PackageManager mPkgMgr;
    private int mAddedCnt = 0;
    private AdView mAdView;
    private SparseArray<Integer> mTypeArray = new SparseArray<>();

    TimelineListAdapter(Context context)
    {
        mCtx = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataArray = new ArrayList<>();
        mPkgMgr = context.getPackageManager();
    }

    public void setData(ArrayList<TimelineData> data)
    {
        mDataArray.clear();
        mDataArray.addAll(data);
    }

    @Override
    public int getCount() {
        if(mDataArray.size() == 0)
            return 0;

        int cnt = mDataArray.size()%5;
        return mDataArray.size() + ((cnt == 0)?(mDataArray.size()/5):(mDataArray.size()/5) + 1);
    }

    @Override
    public Object getItem(int position) {
        return mDataArray.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private int getItemType(int position)
    {
        if(mTypeArray.size() < position + 1 )
        {
            if((position + mAddedCnt) % 6 == 1)
                mTypeArray.put(position, TYPE_ADD);
            else
                mTypeArray.put(position, TYPE_ITEM);
        }

        return mTypeArray.get(position);
    }

    private int getItemIndex(int position)
    {
        int index = position / 7 + 1;
        if(position > 0)
            return position - index;
        return position;
    }

    private Spannable getContentWithColor(int type, String content, String word)
    {
        String contentWitColor = null;
        int index = 0;
        ForegroundColorSpan colorSpan;

        if(type == DBValue.STATUS_LIKE)
        {
            colorSpan = new ForegroundColorSpan(mCtx.getResources().getColor(R.color.like_txt));
        }
        else if(type == DBValue.STATUS_DISLIKE)
        {
            colorSpan = new ForegroundColorSpan(mCtx.getResources().getColor(R.color.spam_txt));
        }
        else    //Normal
        {
            return null;
        }
        Spannable coloredStr = new SpannableString(content);
        Log.d("NJ LEE", "string : " + content + " change : " + word);
        while(true){
            index = content.indexOf(word, index);
            if(index == -1)
                break;

            Log.d("NJ LEE", "index : " + index);
            coloredStr.setSpan(colorSpan, index, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            index += word.length();
        }
        Log.d("NJ LEE", "complete : "+contentWitColor);
        return coloredStr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(getItemType(position) == TYPE_ADD)
        {
            if(mAdView == null) {
                mAdView = new AdView(mCtx);
                mAdView.setAdSize(AdSize.BANNER);
                mAdView.setAdUnitId(mCtx.getString(R.string.banner_ad_unit_id));
                AdRequest adRequest = new AdRequest.Builder()
                        .build();
                mAdView.loadAd(adRequest);
                mAdView.setTag(position);
            }

            return mAdView;
        }

        int index = getItemIndex(position);

        ViewHolder holder;
        if(convertView == null || convertView.getTag() instanceof Integer) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_timeline_list, parent, false);
            holder.mStatusIv = (ImageView) convertView.findViewById(R.id.img_status);
            holder.mDataTv = (TextView) convertView.findViewById(R.id.date);
            holder.mContentTv = (TextView) convertView.findViewById(R.id.content);
            holder.mAppnameTv = (TextView) convertView.findViewById(R.id.app_name);
            holder.mIconIv = (ImageView) convertView.findViewById(R.id.icon);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        TimelineData timeData = mDataArray.get(index);
        if(timeData.getLikeStatus() == DBValue.STATUS_LIKE)
        {
            holder.mStatusIv.setBackgroundResource(R.drawable.timeline_like);
        }
        else if(timeData.getLikeStatus() == DBValue.STATUS_DISLIKE)
        {
            holder.mStatusIv.setBackgroundResource(R.drawable.timeline_spam);
        }
        else
        {
            holder.mStatusIv.setBackgroundResource(R.drawable.timeline_noti);
        }

        String date = timeData.getDate();
        if(date != null) {
            long dateLong = Long.valueOf(date);
            String dateStr = new SimpleDateFormat("yyyy. MM. dd hh:mm a").format(new Date(dateLong));
            holder.mDataTv.setText(dateStr);
        }

        Spannable content = getContentWithColor(mDataArray.get(index).getLikeStatus(),
                                             mDataArray.get(index).getContent(),
                                             mDataArray.get(index).getFilter());
        if(content == null)
        {
            holder.mContentTv.setText(mDataArray.get(index).getContent());
        }
        else
        {
            holder.mContentTv.setText(content);
        }
        holder.mAppnameTv.setText(mDataArray.get(index).getAppName());
        try {
            Drawable icon = mPkgMgr.getApplicationInfo(mDataArray.get(index).getPkgName(), PackageManager.GET_UNINSTALLED_PACKAGES).loadIcon(mPkgMgr);
            holder.mIconIv.setImageDrawable(icon);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return convertView;
    }
}
