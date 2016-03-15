package com.namleesin.smartalert.settingmgr;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
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
public class NotiLikeSetKeywordFragment extends Fragment
{
    private ListView mListView = null;
    private SetKeywordListAdapter mAdapter = null;
    private View mRootView = null;
    private ArrayList<ListViewItem> mListKeywordData = null;

    public NotiLikeSetKeywordFragment()
    {
        mListKeywordData = new ArrayList<ListViewItem>();
    }

    public static NotiLikeSetKeywordFragment newInstance()
    {
        NotiLikeSetKeywordFragment fragment = new NotiLikeSetKeywordFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        mRootView = inflater.inflate(R.layout.fragment_likenotiset_keyword, container, false);

        getKeywordData();

        mListView = (ListView) mRootView.findViewById(R.id.listview);
        mAdapter = new SetKeywordListAdapter(getActivity().getApplicationContext());
        mAdapter.setData(mListKeywordData);
        mListView.setAdapter(mAdapter);

        PullDownInputView pulldownView = (PullDownInputView) mRootView.findViewById(R.id.pulldownView);
        pulldownView.setOnPullDownInputViewCallback(new PullDownInputView.OnPullDownInputViewCallback() {
            @Override
            public void onPUlldownInpuViewCallback(String input) {
                if(input.trim().length() == 0)
                {
                    Toast.makeText(getActivity().getApplicationContext(), "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                DbHandler handler = new DbHandler(getActivity().getApplicationContext());
                KeywordData keywordData = new KeywordData().setKeywordata(input)
                        .setKeywordstatus(DBValue.STATUS_LIKE);
                handler.insertDB(DBValue.TYPE_INSERT_KEYWORDFILTER, keywordData);
                Toast.makeText(getActivity().getApplicationContext(), input + " 추가 되었습니다.", Toast.LENGTH_SHORT).show();

                ListViewItem item = new ListViewItem();
                item.mAppName = input;
                item.mFilterState = DBValue.STATUS_LIKE;
                mListKeywordData.add(item);

                mAdapter.notifyDataSetChanged();

                Intent update = new Intent(NotificationListener.UPDATE_FILTER);
                getActivity().sendBroadcast(update);

            }
        });

        Rect delegateArea = new Rect();
        mRootView.getHitRect(delegateArea);
        TouchDelegate delegate = new TouchDelegate(delegateArea, pulldownView);
        mRootView.setTouchDelegate(delegate);
        return mRootView;
    }

    private void getKeywordData()
    {
        DbHandler handler = new DbHandler(getActivity().getApplicationContext());
        Cursor cursor = handler.selectDBData(DBValue.TYPE_SELECT_FILTERWORD_INFO, String.valueOf(DBValue.STATUS_LIKE));

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
