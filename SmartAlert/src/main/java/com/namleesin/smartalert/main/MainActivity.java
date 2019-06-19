package com.namleesin.smartalert.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;

import androidx.loader.app.LoaderManager.LoaderCallbacks;
import androidx.loader.content.Loader;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.namleesin.smartalert.R;
import com.namleesin.smartalert.commonView.ActionBarView;
import com.namleesin.smartalert.dbmgr.DBValue;
import com.namleesin.smartalert.dbmgr.DbHandler;
import com.namleesin.smartalert.shortcut.PrivacyMode;
import com.namleesin.smartalert.timeline.TimeLineActivity;
import com.namleesin.smartalert.utils.AppInfo;
import com.namleesin.smartalert.utils.NotiAlertState;
import com.namleesin.smartalert.utils.PFMgr;
import com.namleesin.smartalert.utils.PFValue;

import java.util.ArrayList;
import java.util.Locale;



public class MainActivity extends AppCompatActivity implements LoaderCallbacks<ArrayList<NotiInfoData>>, NavigationView.OnNavigationItemSelectedListener {
	//private final String AD_UNIT_ID = "ca-app-pub-1698382082790983/2052368153";
	private final String AD_UNIT_ID = "ca-app-pub-1698382082790983~3217070150";

	private DbHandler mDBHandler;
	private NotiDataListAdapter mAdapter;
	private View mMainDashboardView;
	private LinearLayout mOverlay;
	private int mOverlayHeight = 0;
	private View mRemainLayout;
	private boolean mIsListExpanded = false;
	private ActionBarView mActionbar;
	private AdView mAdView;
	private boolean isRequestEndingAd = false;

	private ViewTreeObserver.OnGlobalLayoutListener mLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
		@Override
		public void onGlobalLayout() {
			mOverlayHeight = mRemainLayout.getHeight();
			FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, mOverlayHeight);
			params.gravity = Gravity.BOTTOM;
			mOverlay.setLayoutParams(params);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		MobileAds.initialize(this, AD_UNIT_ID);

		mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		mDBHandler = new DbHandler(this);
		initView();

		OpenActivity.startSplashScreenActivity(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mAdView.pause();
	}

	private void updateCounts()
	{
		int total = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_TOTAL_COUNT, null);
		int spam_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_DISLIKE_COUNT, null);
		int like_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_LIKE_COUNT, null);

		TextView total_view = (TextView) findViewById(R.id.total_noti_txt);
		total_view.setText(total + "");


		TextView spam_view = (TextView) findViewById(R.id.spam_cnt_txt);
		spam_view.setText(spam_cnt + "");

		TextView like_view = (TextView) findViewById(R.id.fav_cnt_txt);
		like_view.setText(like_cnt + "");

	}

	@Override
	protected void onResume()
	{
		super.onResume();

		mAdView.resume();

		updatePrivacyModeIcon();

		getSupportLoaderManager().initLoader(0, null, this).forceLoad();
		updateCounts();
	}

	@Override
	public void onBackPressed()
	{
		if (mIsListExpanded == true)
		{
			handleMore();
			return;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.menu_drawer);
		if (drawer.isDrawerOpen(GravityCompat.START))
		{
			drawer.closeDrawer(GravityCompat.START);
			return;
		}

		if(isRequestEndingAd){
			return;
		}

		final InterstitialAd interstitialAd = new InterstitialAd(this);
		AdRequest adRequest = new AdRequest.Builder().build();
		interstitialAd.setAdUnitId(AD_UNIT_ID);
		interstitialAd.loadAd(adRequest);
		interstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				if (interstitialAd.isLoaded()) {
					interstitialAd.show();
				}
			}

			@Override
			public void onAdOpened() {
			}

			@Override
			public void onAdFailedToLoad(int errorCode) {
				finish();
			}
			@Override
			public void onAdClosed() {
				finish();
			}
		});
		isRequestEndingAd = true;
	}

	private void initView()
	{
		NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
		navigationView.setNavigationItemSelectedListener(this);

		TextView provertxt = (TextView)navigationView.getHeaderView(0).findViewById(R.id.prodver);
		provertxt.setText(AppInfo.getVersionName(MainActivity.this));

		mActionbar = (ActionBarView) findViewById(R.id.actionbar);
		mActionbar.setOnMenuButtonListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DrawerLayout drawer = (DrawerLayout) findViewById(R.id.menu_drawer);
				drawer.openDrawer(GravityCompat.START);
			}
		});

		mActionbar.setOnGraphButtonListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				int total_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_TOTAL_COUNT, null);
				if(total_cnt == 0)
				{
					Toast.makeText(MainActivity.this, getString(R.string.STR_TOAST_TXT01), Toast.LENGTH_SHORT).show();
					return;
				}

				OpenActivity.startGraphActivity(MainActivity.this);
			}
		});

		mActionbar.setOnFinishButtonListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				handleMore();
			}
		});

		mAdapter = new NotiDataListAdapter(this);
		ListView list = (ListView)findViewById(R.id.noti_list);
		LinearLayout lview = (LinearLayout)findViewById(R.id.emptytimelinelist);
		list.setEmptyView(lview);

		Button openspambtn = (Button)findViewById(R.id.openspambtn);
		openspambtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				OpenActivity.openSpamSettingActivity(MainActivity.this, 0);
			}
		});

		list.setAdapter(mAdapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				NotiInfoData data = (NotiInfoData) view.getTag();

				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_PACKAGE);
				i.putExtra(TimeLineActivity.TIMELINE_PKG, data.getPkgName());

				startActivity(i);
			}
		});

		initDashboard();
	}

	private void initDashboard()
	{
		int total_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_TOTAL_COUNT, null);
		int spam_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_DISLIKE_COUNT, null);
		int like_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_LIKE_COUNT, null);

		TextView total_view = (TextView)findViewById(R.id.total_noti_txt);
		total_view.setText(total_cnt+"");
		total_view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int total_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_TOTAL_COUNT, null);
				if (total_cnt == 0) {
					Toast.makeText(MainActivity.this, getString(R.string.STR_TOAST_TXT01), Toast.LENGTH_SHORT).show();
					return;
				}

				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_TIME);

				startActivity(i);
			}
		});

		TextView spam_view = (TextView) findViewById(R.id.spam_cnt_txt);
		spam_view.setText(spam_cnt + "");

		TextView like_view = (TextView) findViewById(R.id.fav_cnt_txt);
		like_view.setText(like_cnt + "");

		mMainDashboardView = findViewById(R.id.main_dashboard);
		mOverlay = (LinearLayout) findViewById(R.id.overlay);
		View more_btn = findViewById(R.id.more_btn);
		more_btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				handleMore();
			}
		});

		View btn_spam = mMainDashboardView.findViewById(R.id.btn_spam);
		btn_spam.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View view)
			{
				int spam_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_DISLIKE_COUNT, null);
				if(spam_cnt == 0)
				{
					Toast.makeText(MainActivity.this, getString(R.string.STR_TOAST_TXT02), Toast.LENGTH_SHORT).show();
					return;
				}

				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_HATE);

				startActivity(i);
			}
		});

		View btn_like = mMainDashboardView.findViewById(R.id.btn_like);
		btn_like.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				int like_cnt = mDBHandler.selectCountDB(DBValue.TYPE_SELECT_LIKE_COUNT, null);
				if (like_cnt == 0) {
					Toast.makeText(MainActivity.this, getString(R.string.STR_TOAST_TXT03), Toast.LENGTH_SHORT).show();
					return;
				}

				Intent i = new Intent(MainActivity.this, TimeLineActivity.class);
				i.putExtra(TimeLineActivity.TIMELINE_TYPE, TimeLineActivity.TYPE_FAVORITE);

				startActivity(i);
			}
		});

		mRemainLayout = findViewById(R.id.remain_area);
		mRemainLayout.getViewTreeObserver().addOnGlobalLayoutListener(mLayoutListener);
	}

	private void handleMore()
	{
		CheckBox checkBox = (CheckBox)findViewById(R.id.chkmore);
		TextView textView = (TextView)findViewById(R.id.more_btn_txt);
		mRemainLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mLayoutListener);
		View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
		float pixel = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48, getResources().getDisplayMetrics());
		if (mIsListExpanded == false)
		{
			float dpHeight = content.getHeight() - pixel;
			Animation ani = new GrowupAnimation(mOverlay, GrowupAnimation.MODE_GROW, mOverlayHeight, dpHeight);
			mOverlay.startAnimation(ani);
			mActionbar.setTitleType(ActionBarView.ACTIONBAR_TYPE_VIEW, getString(R.string.STR_ACTION_TITLE_TXT01));
			checkBox.setChecked(true);
			textView.setText(getString(R.string.STR_MAIN_TXT03));
		}
		else
		{
			float dpHeight = content.getHeight() - pixel;
			Animation ani = new GrowupAnimation(mOverlay, GrowupAnimation.MODE_SHRINK, dpHeight, mOverlayHeight);
			mOverlay.startAnimation(ani);
			mActionbar.setTitleType(ActionBarView.ACTIONBAR_TYPE_MAIN, null);
			checkBox.setChecked(false);
			textView.setText(getString(R.string.STR_MAIN_TXT02));
		}
		mIsListExpanded = !mIsListExpanded;
	}

	private void initPrivacyMode()
	{
		PFMgr pfMgr = new PFMgr(this);
		boolean initstate =pfMgr.getBooleanValue(PFValue.PRE_SHORTCUT_INIT, false);
		if(false == initstate)
		{
			Intent i = new Intent(this, PrivacyMode.class);
			startActivity(i);
		}

		updatePrivacyModeIcon();

		findViewById(R.id.privacy_mode_btn).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v)
			{
				CheckBox privacy = (CheckBox) findViewById(R.id.privacy_mode_img);
				privacy.setChecked(!privacy.isChecked());

				Intent i = new Intent(MainActivity.this, PrivacyMode.class);
				startActivity(i);
			}
		});
	}

	private void updatePrivacyModeIcon()
	{
		PFMgr pfMgr = new PFMgr(this);
		int mode = pfMgr.getIntValue(PrivacyMode.PREF_PRIVACY_MODE, PrivacyMode.PRIVACY_MODE_OFF);
		CheckBox privacy = (CheckBox) findViewById(R.id.privacy_mode_img);
		if(mode == PrivacyMode.PRIVACY_MODE_ON)
		{
			privacy.setChecked(true);
			//privacy.setImageResource(R.drawable.privacy_on);
		}
		else //mode == PrivacyMode.PRIVACY_MODE_OFF
		{
			privacy.setChecked(false);
			//privacy.setImageResource(R.drawable.privacy_off);
		}
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		mAdView.destroy();
	}

	@Override
	public Loader<ArrayList<NotiInfoData>> onCreateLoader(int id, Bundle bundle)
	{
		return new NotiDataLoader(this);
	}

	@Override
	public void onLoadFinished(Loader<ArrayList<NotiInfoData>> loader,
							   ArrayList<NotiInfoData> data)
	{
		if(data != null) {
			mAdapter.setData(data);
			mAdapter.notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<ArrayList<NotiInfoData>> loader) {
		mAdapter.setData(null);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if(resultCode == Activity.RESULT_CANCELED)
		{
			finish();
			return;
		}

		PFMgr pmgr = new PFMgr(this);
		switch(requestCode)
		{
			case MainValue.RES_SPLASH_SCREEN:
				checkStartState();
				break;
			case MainValue.RES_GUIDE_WIZARD:
				pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_GUIDE_OK);
				if(true == NotiAlertState.isNLServiceRunning(this))
				{
					pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_ALERT_OK);
					OpenActivity.startSpamSettingActivity(this);
				}
				else
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				break;
			case MainValue.RES_ALERT_SETTING:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				else
				{
					pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_ALERT_OK);
					OpenActivity.startSpamSettingActivity(this);
				}
				break;
			case MainValue.RES_SL_SETTING:
				pmgr.setIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_SETTING_OK);

				openGuideMainActivity();

				initPrivacyMode();
				break;
			default:
				break;
		}
	}

	private void checkStartState()
	{
		int initstate = new PFMgr(this).getIntValue(PFValue.PRE_INIT_STATE, PFValue.PRE_INIT_DEFAULT);
		switch(initstate)
		{
			case PFValue.PRE_INIT_DEFAULT:
				OpenActivity.startGuideMgrActivity(this);
				break;
			case PFValue.PRE_INIT_GUIDE_OK:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				else
				{
					OpenActivity.startSpamSettingActivity(this);
				}
				break;
			case PFValue.PRE_INIT_ALERT_OK:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				else
				{
					OpenActivity.startSpamSettingActivity(this);
				}
				break;
			case PFValue.PRE_INIT_SETTING_OK:
				if(false == NotiAlertState.isNLServiceRunning(this))
				{
					OpenActivity.startAlertSettingActivity(this);
				}
				openGuideMainActivity();

				initPrivacyMode();
				break;
			default:
				break;
		}
	}

	private void openGuideMainActivity()
	{
		Locale systemLocale = getResources().getConfiguration().locale;
		String strLanguage = systemLocale.getLanguage();
		if(false == strLanguage.equals("ko"))
		{
			return;
		}

		boolean showguidestate = new PFMgr(this).getBooleanValue(PFValue.PRE_CHECK_STATE, false);
		if(false == showguidestate)
		{
			OpenActivity.openMainGuideActivity(this);
		}
	}

	@Override
	public boolean onNavigationItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu1:
				OpenActivity.openSpamSettingActivity(this, 0);
				break;
			case R.id.menu2:
				OpenActivity.openSpamSettingActivity(this, 2);
				break;
			case R.id.menu3:
				OpenActivity.openGuideMgrActivity(this);
				break;
			default:
				break;
		}

		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.menu_drawer);
		drawer.closeDrawer(GravityCompat.START);

		return false;
	}
}
