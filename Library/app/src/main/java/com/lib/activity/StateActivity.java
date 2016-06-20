package com.lib.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.lib.utils.*;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class StateActivity extends Activity implements OnClickListener {

	public static final String url = "http://192.168.191.1:8080/LibrarySeatServer/usrInfo";

	private TextView mTitleTv;
	private ImageView mBackImg;
	private RelativeLayout mState_study;
	private RelativeLayout mState_break;
	private TextView Leave;

	private Map<String, String> usrInfo = new HashMap<String, String>();

	private AlarmReceiver alarm_receiver;
	public class AlarmReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Log.d("StateActivity", "Time is over!");
			Map<String, String> req_para = new HashMap<String, String>();
			req_para.put("req_type", "OUT");
			req_para.put("usr", MainActivity.UsrInfo.get("usrName"));
			HttpUtil.sendHttpRequestPost(url, req_para, my_listener);
			unregisterReceiver(alarm_receiver);
			new AlertDialog.Builder(StateActivity.this)
					.setTitle("提醒")
					.setMessage("您已经离开30分钟，座位以自动注销！")
					.setPositiveButton("确定", null)
					.show();
		}

	}

	public MyListener my_listener = new MyListener();
	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {

			//解析json数据
			try {
				usrInfo.clear();
				if(HandleResponseData.getUsrInfo(response) != null)
					usrInfo.putAll(HandleResponseData.getUsrInfo(response));

			} catch (JSONException e) {
				// 输出错误信息
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("StateActivity", elem.toString());
				}
			}

			//回到主线程刷新显示
			runOnUiThread(new Runnable(){

				@Override
				public void run(){
					if(!usrInfo.isEmpty()){
						Intent intent = new Intent();
						intent.putExtra("loginState", usrInfo.get("loginState"));
						setResult(RESULT_OK, intent);
						if(usrInfo.get("loginState").equals("1")){
							Toast.makeText(StateActivity.this, "欢迎学霸归来", Toast.LENGTH_SHORT).show();
							Leave.setText("离开一会儿");
						}else if(usrInfo.get("loginState").equals("2")){
							Toast.makeText(StateActivity.this, "只能离开30分钟哦", Toast.LENGTH_SHORT).show();
							Leave.setText("继续做学霸");
						}else if(usrInfo.get("loginState").equals("0"))
							Toast.makeText(StateActivity.this, "30分钟已到，请重新占座", Toast.LENGTH_SHORT).show();
					}else
						Toast.makeText(StateActivity.this, "请求失败", Toast.LENGTH_SHORT).show();
				}

			});

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("StateActivity", elem.toString());
					}

					Toast.makeText(StateActivity.this, "登陆失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_state);

		init();
		initEvents();
		IntentFilter filter = new IntentFilter();
		filter.addAction("ACTION_ALARM");
		alarm_receiver = new AlarmReceiver();
		registerReceiver(alarm_receiver, filter);
	}

	private void init() {
		mTitleTv = (TextView) findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.state);
		mBackImg = (ImageView) findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);

		mState_study = (RelativeLayout) findViewById(R.id.state_study);
		mState_break =(RelativeLayout) findViewById(R.id.state_break);
		Leave = (TextView)findViewById(R.id.leave);
		if(MainActivity.UsrInfo.get("loginState").equals("1"))
			Leave.setText("离开一会儿");
		else if(MainActivity.UsrInfo.get("loginState").equals("2"))
			Leave.setText("继续做学霸");
	}

	private void initEvents() {
		mBackImg.setOnClickListener(this);
		mState_study.setOnClickListener(this);
		mState_break.setOnClickListener(this);

	}



	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.back_img:
				// 返回按钮
				this.finish();
				break;
			case R.id.state_break:
				if(MainActivity.UsrInfo.get("seatId").equals("0"))
					Toast.makeText(StateActivity.this, "您还未占座", Toast.LENGTH_SHORT).show();
				else{
					String text;
					if(MainActivity.UsrInfo.get("loginState").equals("1"))
						text = "真的要离开吗？";
					else
						text = "继续做学霸？";
					WarningUtils.showDialog(text, new DialogInterface.OnClickListener(){

						@SuppressLint("NewApi")
						@Override
						public void onClick(DialogInterface dialog,
											int which) {
							// TODO Auto-generated method stub
							switch(which){
								case WarningUtils.CONFIRM:
									if(MainActivity.UsrInfo.get("loginState").equals("1")){
										AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
										int time = 10 * 1000; //ms
										long triggerAtTime = SystemClock.elapsedRealtime() + time;
										Intent i = new Intent("ACTION_ALARM");
										PendingIntent pi = PendingIntent.getBroadcast(StateActivity.this, 0, i, 0);
										manager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
										Log.d("StateActivity", "Alarmer start!");
										Map<String, String> req_para = new HashMap<String, String>();
										req_para.put("req_type", "LEAVE");
										req_para.put("usr", MainActivity.UsrInfo.get("usrName"));
										HttpUtil.sendHttpRequestPost(url, req_para, my_listener);


									}else if(MainActivity.UsrInfo.get("loginState").equals("2")){
										//取消闹钟
										Intent i = new Intent("ACTION_ALARM");
										PendingIntent pi = PendingIntent.getBroadcast(StateActivity.this, 0, i, 0);
										AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
										manager.cancel(pi);
										Map<String, String> req_para = new HashMap<String, String>();
										req_para.put("req_type", "BACK");
										req_para.put("usr", MainActivity.UsrInfo.get("usrName"));
										HttpUtil.sendHttpRequestPost(url, req_para, my_listener);
									}
									break;
								case WarningUtils.CANCEL:
									break;
								default: break;
							}
						}

					}, this);
				}
				break;

			case R.id.state_study:
				if(MainActivity.UsrInfo.get("seatId").equals("0"))
					Toast.makeText(StateActivity.this, "您还未占座", Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(StateActivity.this, "您的座位号是" + MainActivity.UsrInfo.get("seatId"), Toast.LENGTH_LONG).show();
				break;
			default:
				break;
		}
	}

}