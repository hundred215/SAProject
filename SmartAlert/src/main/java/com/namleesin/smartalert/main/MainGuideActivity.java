package com.namleesin.smartalert.main;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.utils.PFMgr;
import com.namleesin.smartalert.utils.PFValue;

public class MainGuideActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_guide);

        Button finishBtn = (Button) findViewById(R.id.finishbtn);
        finishBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) findViewById(R.id.notretrycbox);
                new PFMgr(MainGuideActivity.this).setBooleanValue(PFValue.PRE_CHECK_STATE, checkBox.isChecked());

                finish();
            }
        });
    }
}
