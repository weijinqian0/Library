package com.lib.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;


import com.lib.utils.*;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PersonalInfoActivity extends Activity implements OnClickListener{

	public static final String url = "http://192.168.191.1:8080/LibrarySeatServer/usrInfo";

	private TextView mTitleTv, TextName, TextStudentNum;
	private ImageView mBackImg;
	private Button exit_btn;

	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {
			// TODO Auto-generated method stub
			//解析json数据
			try {
				Map<String, String> usr_info = HandleResponseData.getUsrInfo(response);
				if(usr_info != null){
					Intent intent = new Intent();
					intent.putExtra("loginState", usr_info.get("loginState"));
					setResult(RESULT_OK, intent);
					finish();
				}

			} catch (JSONException e) {
				// 输出错误信息
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("PersonalInfoActivity", elem.toString());
				}
			}

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("PersonalInfoActivity", elem.toString());
					}

					Toast.makeText(PersonalInfoActivity.this, "注销失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_info);
		init();
		initViews();

	}

	private void init() {
		mTitleTv = (TextView)findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.info);
		exit_btn = (Button)findViewById(R.id.exit_btn);
		mBackImg = (ImageView)findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);
		TextName = (TextView)findViewById(R.id.name);
		TextStudentNum = (TextView)findViewById(R.id.student_num);
	}

	private void initViews() {
		mBackImg.setOnClickListener(this);
		exit_btn.setOnClickListener(this);
		TextName.setText(MainActivity.UsrInfo.get("usrName"));
		TextStudentNum.setText(MainActivity.UsrInfo.get("studentNum"));
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.exit_btn:
				WarningUtils.showDialog("真的要注销吗", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog,
										int which) {
						// TODO Auto-generated method stub
						switch(which){
							case WarningUtils.CONFIRM:
								Map<String, String> req_para = new HashMap<String, String>();
								req_para.put("req_type", "OUT");
								req_para.put("usr", MainActivity.UsrInfo.get("usrName"));
								HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
								break;
							case WarningUtils.CANCEL:
								break;
							default: break;
						}
					}

				}, this);
				break;
			case R.id.back_img:
				this.finish();
				break;
			default:
				break;
		}
	}

}
