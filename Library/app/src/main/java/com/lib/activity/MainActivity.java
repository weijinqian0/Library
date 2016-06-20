package com.lib.activity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.lib.fragment.CategoryFragment;
import com.lib.fragment.HomeFragment;
import com.lib.fragment.SettingFragment;
import com.lib.utils.ConstantValues;
import com.lib.view.MyTabWidget;
import com.lib.view.MyTabWidget.OnTabSelectedListener;
/**
 * APP的第一个页面，座位预览
 * @author Administrator
 *
 */

public class MainActivity extends FragmentActivity implements OnTabSelectedListener {

	//全局用户信息，是否需要用application来保存？
	public static Map<String, String> UsrInfo = new HashMap<String, String>();
	private static final int TIME_UP = 1;

	//管理fragment
	private static final String TAG = "MainActivity";
	private MyTabWidget mTabWidget;
	private HomeFragment mHomeFragment;
	private CategoryFragment mCategoryFragment;
	private SettingFragment mSettingFragment;
	private int mIndex = ConstantValues.HOME_FRAGMENT_INDEX;
	private FragmentManager mFragmentManager;
	//时钟
	private long waitTime = 2000;
	private long touchTime = 0;

	private NfcAdapter NFCAdapter;
	private PendingIntent mPendingIntent;
	private Intent NewIntent;
	private String ReadResult;

	public Boolean isFirstIn = false;

	private int flag = 0;
	private Handler handler = new Handler(){

		public void handleMessage(Message msg){
			switch(msg.what){
				case TIME_UP:
					if(flag == 0)
						Toast.makeText(MainActivity.this, "没有检测到标签", Toast.LENGTH_SHORT).show();
					else
						flag = 0;
					break;
				default:
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//百度地图需要用
		init();
		initEvents();

		NFCAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
	}

	private void init() {
		mFragmentManager = getSupportFragmentManager();
		mTabWidget = (MyTabWidget)findViewById(R.id.tab_widget);
		UsrInfo.put("usrName", "");
		UsrInfo.put("studentNum", "");
		UsrInfo.put("loginState", "0");
		UsrInfo.put("seatId", "");
		UsrInfo.put("usrId", "");
	}

	private void initEvents() {
		mTabWidget.setOnTabSelectedListener(this);
		onTabSelected(0);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

		//设置底部导航被选中时的颜色变化
		mTabWidget.setTabsDisplay(this, mIndex);
		//设置版本更新小红点显示（模拟有版本更新时）
		mTabWidget.setIndicateDisplay(this, 3, true);
	}

	//根据导航栏不同的按键，跳转到不同的主页面
	@Override
	public void onTabSelected(int index) {
		// TODO Auto-generated method stub
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		/*
		hideFragments(transaction);
		switch (index) {
			case ConstantValues.HOME_FRAGMENT_INDEX:
				if (null == mHomeFragment) {
					mHomeFragment = new HomeFragment();
					transaction.add(R.id.center_layout, mHomeFragment);
				} else {
					transaction.show(mHomeFragment);
				}
				break;
			case ConstantValues.CATEGORY_FRAGMENT_INDEX:
				if (null == mCategoryFragment) {
					mCategoryFragment = new CategoryFragment();
					transaction.add(R.id.center_layout, mCategoryFragment);

				} else {
					transaction.show(mCategoryFragment);
				}
				break;
			case ConstantValues.SETTING_FRAGMENT_INDEX:
				if (null == mSettingFragment) {
					mSettingFragment = new SettingFragment();
					transaction.add(R.id.center_layout, mSettingFragment);
				} else {
					transaction.show(mSettingFragment);
				}
				break;
			default:
				break;
		}
		mIndex = index;
		transaction.commitAllowingStateLoss();
		*/
		switch (index) {
			case ConstantValues.HOME_FRAGMENT_INDEX:
				mHomeFragment = new HomeFragment();
				transaction.replace(R.id.center_layout, mHomeFragment);
				break;
			case ConstantValues.CATEGORY_FRAGMENT_INDEX:
				if (NFCAdapter == null) {
					Toast.makeText(this, "设备不支持NFC！", Toast.LENGTH_SHORT).show();
				}
				else if (!NFCAdapter.isEnabled()) {
					Toast.makeText(this, "在系统设置中先启请用NFC功能！", Toast.LENGTH_SHORT).show();
				}else{
					new Thread(new Runnable(){

						@Override
						public void run() {
							// TODO Auto-generated method stub
							long initial_time = System.currentTimeMillis();
							while(true){
								long current_time = System.currentTimeMillis();
								if(current_time - initial_time > 6000){
									Message message = new Message();
									message.what = TIME_UP;
									handler.sendMessage(message);
									break;
								}
							}
						}

					}).start();
				}

				mCategoryFragment = new CategoryFragment();
				transaction.replace(R.id.center_layout, mCategoryFragment);
				break;
			case ConstantValues.SETTING_FRAGMENT_INDEX:
				mSettingFragment = new SettingFragment();
				transaction.replace(R.id.center_layout, mSettingFragment);
				break;
			default:
				break;
		}
		mIndex = index;
		transaction.commitAllowingStateLoss();
	}

	private void hideFragments(FragmentTransaction transaction) {
		if (null != mHomeFragment) { transaction.hide(mHomeFragment); }
		if (null != mCategoryFragment) { transaction.hide(mCategoryFragment); }
		if(null != mSettingFragment) { transaction.hide(mSettingFragment);}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		// 自己记录fragment的位置,防止activity被系统回收时，fragment错乱的问题
		outState.putInt("index", mIndex);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState,
									   PersistableBundle persistentState) {
		// TODO Auto-generated method stub
		//super.onRestoreInstanceState(savedInstanceState, persistentState);
		mIndex = savedInstanceState.getInt("index");
	}

	//退出程序按钮
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(event.getAction() == KeyEvent.ACTION_DOWN
				&&KeyEvent.KEYCODE_BACK == keyCode ) {
			long currentTime = System.currentTimeMillis();
			if ((currentTime - touchTime) >= waitTime) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				touchTime = currentTime;
			}else {
				finish();
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onNewIntent(Intent intent) {
		NewIntent = intent;
		flag = 1;
		Log.d("NFC", "onNewIntent called");
		if( readFromTag( intent ) ){
			Log.d("NFC", ReadResult);
			if(mCategoryFragment != null)
				mCategoryFragment.scanNFC(ReadResult);
		}
	}

	private boolean readFromTag(Intent intent){
		Parcelable[] rawArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
		NdefMessage mNdefMsg = (NdefMessage)rawArray[0];
		NdefRecord mNdefRecord = mNdefMsg.getRecords()[0];
		try {
			if(mNdefRecord != null){
				ReadResult = new String(mNdefRecord.getPayload(),"UTF-8");
				return true;
			}
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		};
		return false;
	}

	public void openNFC(){
		Log.d("NFC", "NFC open");
		if(NFCAdapter!=null&&NFCAdapter.isEnabled())
			NFCAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);
	}

	public void closeNFC(){
		Log.d("NFC", "NFC closed");
		if(NFCAdapter!=null&&NFCAdapter.isEnabled())
			NFCAdapter.disableForegroundDispatch(this);
	}

}
