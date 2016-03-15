package com.namleesin.smartalert.settingmgr;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.namleesin.smartalert.R;
import com.namleesin.smartalert.main.OpenActivity;


public class AlertSettingActivity extends Activity implements OnClickListener
{
	private boolean mFinishState = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alertsetting);
		
		Button btn = (Button)this.findViewById(R.id.alertbutton);
		btn.setOnClickListener(this);
	}

	@Override
	protected void onStart()
	{
		super.onStart();

		if(true == mFinishState)
		{
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onClick(View v) 
	{
		switch(v.getId())
		{
			case R.id.alertbutton:
				mFinishState = true;
				OpenActivity.startAlertAccessSettingActivity(this);
				break;
			default:
				break;
		}
	}
}
