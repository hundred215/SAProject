package com.namleesin.smartalert.main;

import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

/**
 * Created by chitacan on 2016. 1. 12..
 */
public class GrowupAnimation extends Animation {

    public static final int MODE_GROW = 0;
    public static final int MODE_SHRINK = 1;

    private View mView;
    private float mToHeight;
    private float mFromHeight;
    private int mMode;

    public GrowupAnimation(View v, int mode,  float fromHeight, float toHeight) {
        mMode = mode;
        mToHeight = toHeight;
        mFromHeight = fromHeight;
        mView = v;
        setDuration(500);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float height = mFromHeight;
        switch (mMode)
        {
            case MODE_GROW:
                height = (mToHeight - mFromHeight) * interpolatedTime + mFromHeight;
                break;
            case MODE_SHRINK:
                height = mFromHeight - (mFromHeight - mToHeight) * interpolatedTime;
                break;
        }

        FrameLayout.LayoutParams p = (FrameLayout.LayoutParams) mView.getLayoutParams();
        p.height = (int)height;
        mView.setLayoutParams(p);

        mView.requestLayout();
    }
}
