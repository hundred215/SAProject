package com.namleesin.smartalert.settingmgr;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.PullDownInputView;
import com.namleesin.smartalert.data.KeywordData;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.notimgr.NotificationListener;

import java.util.ArrayList;

/**
 * Created by namjinha on 2016-03-07.
 */
public class NotiSpamSetKeywordFragment extends Fragment
{
    private ListView mListView = null;
    private SetKeywordListAdapter mAdapter = null;
    private View mRootView = null;
    private static ViewPager mViewPager = null;
    ArrayList<ListViewItem> mListKeywordData = null;

    public NotiSpamSetKeywordFragment()
    {
        mListKeywordData = new ArrayList<ListViewItem>();
    }

    public static NotiSpamSetKeywordFragment newInstance(ViewPager aViewPager)
    {
        mViewPager = aViewPager;
        NotiSpamSetKeywordFragment fragment = new NotiSpamSetKeywordFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_spamnotiset_keyword, container, false);

        getKeywordData();

        mListView = (ListView) mRootView.findViewById(R.id.listview);
        mAdapter = new SetKeywordListAdapter(getActivity().getApplicationContext());
        mAdapter.setData(mListKeywordData);
        mListView.setAdapter(mAdapter);

        PullDownInputView pullDownInputView = (PullDownInputView) mRootView.findViewById(R.id.pulldownView);
        pullDownInputView.setOnPullDownInputViewCallback(new PullDownInputView.OnPullDownInputViewCallback()
        {
            @Override
            public void onPUlldownInpuViewCallback(String input)
            {
                Log.d("NJ Lee", "input : " + input);
                if(input.trim().length() == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DbHandler handler = new DbHandler(getActivity().getApplicationContext());
                KeywordData keywordData = new KeywordData().setKeywordata(input)
                        .setKeywordstatus(DBValue.STATUS_DISLIKE);
                handler.insertDB(DBValue.TYPE_INSERT_KEYWORDFILTER, keywordData);
                Toast.makeText(getActivity().getApplicationContext(), input + " 추가 되었습니다.", Toast.LENGTH_SHORT).show();

                ListViewItem item = new ListViewItem();
                item.mAppName = input;
                item.mFilterState = DBValue.STATUS_DISLIKE;

                mListKeywordData.add(item);
                mAdapter.notifyDataSetChanged();

                Intent update = new Intent(NotificationListener.UPDATE_FILTER);
                getActivity().sendBroadcast(update);
            }
        });

        Button apptabBtn = (Button) mRootView.findViewById(R.id.tab01);
        apptabBtn.setPressed(false);
        apptabBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                mViewPager.setCurrentItem(0);
            }
        });

        Button keywordtabBtn = (Button) mRootView.findViewById(R.id.tab02);
        keywordtabBtn.setPressed(true);

        return mRootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        PullDownInputView pullDownInputView = (PullDownInputView) mRootView.findViewById(R.id.pulldownView);
        Rect delegateArea = new Rect();
        View container = pullDownInputView.findViewById(R.id.handle);
        container.getHitRect(delegateArea);

        TouchDelegate delegate = new TouchDelegate(delegateArea, pullDownInputView);
        pullDownInputView.setTouchDelegate(delegate);
    }

    private void getKeywordData()
    {
        DbHandler handler = new DbHandler(getActivity().getApplicationContext());
        Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERWORD_INFO, String.valueOf(DBValue.STATUS_DISLIKE));

        if(null != cursor && cursor.getCount() > 0)
        {
            cursor.moveToFirst();
            do
            {
                ListViewItem item = new ListViewItem();
                item.mAppName = cursor.getString(0);
                item.mFilterState = cursor.getInt(1);
                mListKeywordData.add(item);
            }
            while (cursor.moveToNext());
        }
    }
}
