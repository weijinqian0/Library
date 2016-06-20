package com.lib.activity;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import com.lib.utils.*;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity implements OnClickListener{

	public static final String url = "http://192.168.191.1:8080/LibrarySeatServer/usrInfo";

	private TextView mTitleTv;
	private ImageView mBackImg;
	private Button mRegister_btn, mLogin_btn;
	private EditText user, passWord;
	private SharedPreferences pref;
	private SharedPreferences.Editor editor;
	private CheckBox rememberPass;

	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {
			// TODO Auto-generated method stub
			//����json���
			try {
				Map<String, String> usr_info = HandleResponseData.getUsrInfo(response);
				if(!usr_info.isEmpty()){
					Intent intent = new Intent();
					intent.putExtra("usrName", usr_info.get("usrName"));
					intent.putExtra("studentNum", usr_info.get("studentNum"));
					intent.putExtra("loginState", usr_info.get("loginState"));
					intent.putExtra("seatId", usr_info.get("seatId"));
					intent.putExtra("usrId", usr_info.get("usrId"));
					setResult(RESULT_OK, intent);
					editor = pref.edit();
					if(rememberPass.isChecked()){
						editor.putBoolean("remember_password", true);
						editor.putString("account", usr_info.get("usrName"));
						editor.putString("password", usr_info.get("passWord"));
					}else
						editor.clear();
					editor.commit();
					finish();
				}else
					Toast.makeText(LoginActivity.this, "��½ʧ��", Toast.LENGTH_SHORT).show();

			} catch (JSONException e) {
				// ���������Ϣ
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("LoginActivity", elem.toString());
				}
			}

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("LoginActivity", elem.toString());
					}

					Toast.makeText(LoginActivity.this, "��½ʧ��", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_login);
		pref = PreferenceManager.getDefaultSharedPreferences(this);
		init();
		initViews();
		//Intent intent = new Intent();
		//intent.putExtra("data", "Hello");
		//setResult(RESULT_OK, intent);
	}

	private void init() {
		mTitleTv = (TextView)findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.login);
		mBackImg = (ImageView)findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);
		mRegister_btn = (Button)findViewById(R.id.register_btn);
		mLogin_btn = (Button)findViewById(R.id.login_btn);
		user = (EditText)findViewById(R.id.username);
		passWord = (EditText)findViewById(R.id.password);
		rememberPass = (CheckBox) findViewById(R.id.savePassword);
	}

	private void initViews() {
		mBackImg.setOnClickListener(this);
		mRegister_btn.setOnClickListener(this);
		mLogin_btn.setOnClickListener(this);
		boolean isRemember = pref.getBoolean("remember_password", false);
		if(isRemember){
			String account = pref.getString("account", "");
			String password = pref.getString("password", "");
			user.setText(account);
			passWord.setText(password);
			rememberPass.setChecked(true);
		}
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.back_img:
				this.finish();
				break;
			case R.id.register_btn:
				CommonUtils.launchActivity(this, RegisterActivity.class);
				break;
			case R.id.login_btn:
				Map<String, String> req_para = new HashMap<String, String>();
				String usr_name = user.getText().toString();
				String password = passWord.getText().toString();
				if( usr_name.equals("") || password.equals("") )
					Toast.makeText(LoginActivity.this, "�û�������벻��Ϊ��", Toast.LENGTH_SHORT).show();
				else{
					req_para.put("req_type", "IN");
					req_para.put("usr", usr_name);
					req_para.put("password", password);
					HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
				}
				break;
			default:
				break;
		}
	}

}
