package com.namleesin.smartalert.commonView;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.namleesin.smartalert.R;

/**
 * Created by nanjui on 2016. 1. 30..
 */
public class PullDownInputView extends FrameLayout implements View.OnTouchListener,
                                                               View.OnClickListener

{
    private final int EXPAND_HEIGHT = 100;

    private float mY;
    private float mDistance;
    private float mMaxHeight = 0;
    private OnPullDownInputViewCallback mPulldownInputCallback = null;
    private boolean isInputboxShown = false;
    private float mOriginalHeight;

    public PullDownInputView(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.layout_pulldown, this);
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mMaxHeight = getHeight() + EXPAND_HEIGHT;
                mOriginalHeight = findViewById(R.id.view_temp).getHeight();

                PullDownInputView.this.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        initView();
    }

    private void initView()
    {
        ImageButton addBtn = (ImageButton) findViewById(R.id.add_btn);
        addBtn.setOnClickListener(this);

        View view = findViewById(R.id.handle);
        view.setOnTouchListener(this);

        LinearLayout close_layout = (LinearLayout) findViewById(R.id.close);
        close_layout.setOnClickListener(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event)
    {
        final int action = MotionEventCompat.getActionMasked(event);
        Log.d("NJ LEE", "action : "+action);
        if(isInputboxShown == true)
            return false;

        View temp = findViewById(R.id.view_temp);
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                mY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 54, getResources().getDisplayMetrics());
                if(mDistance < h)
                {
                    h = 0;
                }
                else
                {
                    isInputboxShown = true;
                    EditText edit = (EditText) findViewById(R.id.keyword);
                    edit.setEnabled(true);
                    View pulldown_layout = findViewById(R.id.pulldown);
                    pulldown_layout.setVisibility(View.GONE);

                    View close_layout = findViewById(R.id.close);
                    close_layout.setVisibility(View.VISIBLE);
                }
                temp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) h));
                break;
            case MotionEvent.ACTION_MOVE:
                mDistance = Math.abs(mY - event.getY());
                if(mDistance > mMaxHeight)
                {
                    mDistance = mMaxHeight;
                }
                mDistance *= 0.9;
                temp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)mDistance));
                break;
        }
        return false;
    }

    public void initialize(Activity activity){
        ImageButton btn = (ImageButton) findViewById(R.id.add_btn);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPulldownInputCallback != null) {
                    EditText input = (EditText) PullDownInputView.this.findViewById(R.id.keyword);
                    mPulldownInputCallback.onPUlldownInpuViewCallback(input.getText().toString());
                }
            }
        });
    }

    public void setOnPullDownInputViewCallback(OnPullDownInputViewCallback l)
    {
        mPulldownInputCallback = l;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id)
        {
            case R.id.close:
                isInputboxShown = false;
                View temp = findViewById(R.id.view_temp);
                temp.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)mOriginalHeight));
                findViewById(R.id.close).setVisibility(View.GONE);
                findViewById(R.id.pulldown).setVisibility(View.VISIBLE);
                break;
            case R.id.add_btn:
                EditText edit = (EditText) findViewById(R.id.keyword);
                mPulldownInputCallback.onPUlldownInpuViewCallback(edit.getText().toString());
                edit.setText("");
                break;
        }
    }

    public interface OnPullDownInputViewCallback
    {
        void onPUlldownInpuViewCallback(String input);
    }
}
