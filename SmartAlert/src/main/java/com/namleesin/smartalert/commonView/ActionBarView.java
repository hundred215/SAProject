package com.namleesin.smartalert.commonView;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.namleesin.smartalert.R;

/**
 * Created by comus1200 on 2016. 1. 14..
 */
public class ActionBarView extends LinearLayout implements View.OnClickListener{
    public static final int ACTIONBAR_TYPE_MAIN             = 0;
    public static final int ACTIONBAR_TYPE_ACTIVITY         = 1;
    public static final int ACTIONBAR_TYPE_VIEW             = 2;
    public static final int ACTIONBAR_TYPE_WIZARD           = 3;


    private LayoutInflater mInflater;
    private OnClickListener mGraghBtnListener;
    private OnClickListener mFinishBtnListener;
    private OnClickListener mMenuBtnListener;

    public ActionBarView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);
    }

    public ActionBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        TypedArray a = context.obtainStyledAttributes(attrs,  R.styleable.ActionBarView);
        int type = a.getInteger(R.styleable.ActionBarView_actionbarType, -1);
        String title = a.getString(R.styleable.ActionBarView_actionbarTitle);
        initView(type, title);
        Log.d("NJ LEE", "type : "+type+" title : "+title);
    }

    public int getLayoutResource(int type)
    {
        int resource = R.layout.layout_actionbar_main;
        switch (type) {
            case ACTIONBAR_TYPE_MAIN:
                resource = R.layout.layout_actionbar_main;
                break;
            case ACTIONBAR_TYPE_ACTIVITY:
                resource = R.layout.layout_actionbar_activity;
                break;
            case ACTIONBAR_TYPE_VIEW:
            case ACTIONBAR_TYPE_WIZARD:
                resource = R.layout.layout_actionbar_view;
                break;
        }
        return resource;
    }

    public void initView(int type, String title)
    {
        View main = mInflater.inflate(getLayoutResource(type), this, false);
        if(main != null) {
            addView(main);
        }

        switch (type)
        {
            case ACTIONBAR_TYPE_MAIN:
                main.findViewById(R.id.menu_drawer_btn).setOnClickListener(this);
                main.findViewById(R.id.graph_btn).setOnClickListener(this);
                break;
            case ACTIONBAR_TYPE_ACTIVITY:
                main.findViewById(R.id.back_arrow).setOnClickListener(this);
                break;
            case ACTIONBAR_TYPE_VIEW:
            case ACTIONBAR_TYPE_WIZARD:
                break;
        }

        TextView title_tv = (TextView) findViewById(R.id.title_txt);
        if(title_tv == null)
            return;

        title_tv.setText(title);
    }

    public void setTitleText(String text)
    {
        TextView view = (TextView) findViewById(R.id.title_txt);
        if(view != null)
            view.setText(text);
    }

    public void setTitleType(int type, String titleStr)
    {
        initView(type, titleStr);
        invalidate();
    }

    public void setOnGraphButtonListener(OnClickListener l)
    {
        mGraghBtnListener = l;
    }

    public void setOnFinishButtonListener(OnClickListener l)
    {
        mFinishBtnListener = l;
    }

    public void setOnMenuButtonListener(OnClickListener l)
    {
        mMenuBtnListener = l;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.back_arrow:
                if(mFinishBtnListener != null)
                    mFinishBtnListener.onClick(v);
                break;
            case R.id.graph_btn:
                if(mGraghBtnListener != null)
                    mGraghBtnListener.onClick(v);
                break;
            case R.id.menu_drawer_btn:
                if(mMenuBtnListener != null)
                    mMenuBtnListener.onClick(v);
                break;
        }
    }
}
