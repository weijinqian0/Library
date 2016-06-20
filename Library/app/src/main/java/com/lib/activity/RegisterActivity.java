package com.lib.activity;

import java.util.HashMap;
import java.util.Map;

import com.lib.utils.HttpCallbackListener;
import com.lib.utils.HttpUtil;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends Activity implements OnClickListener{

	String url = "http://192.168.191.1:8080/LibrarySeatServer/register";

	private TextView mTitleTv;
	private ImageView mBackImg;
	private EditText UserName, StudentNum, CardNum, Password, PasswordAgain;
	private Button Reg;

	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {
			// TODO Auto-generated method stub
			final String result;
			switch(response){
				case "Fail":
					result = "注册失败";
					break;
				case "Repeat":
					result = "用户名重复";
					break;
				case "Success":
					result = "注册成功";
					break;
				default: result = "注册失败"; break;
			}

			runOnUiThread(new Runnable(){

				@Override
				public void run(){
					Toast.makeText(RegisterActivity.this, result, Toast.LENGTH_SHORT).show();
					if(result.equals("Success"))
						RegisterActivity.this.finish();
					else{
						UserName.setText("");
						StudentNum.setText("");
						CardNum.setText("");
						Password.setText("");
						PasswordAgain.setText("");
						UserName.requestFocus();
					}
				}
			});
		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("RegisterActivity", elem.toString());
					}

					Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_register);
		init();
		initViews();
	}

	private void init() {
		mTitleTv = (TextView)findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.register);
		mBackImg = (ImageView)findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);
		UserName = (EditText)findViewById(R.id.username);
		StudentNum = (EditText)findViewById(R.id.xuehao);
		CardNum = (EditText)findViewById(R.id.card_num);
		Password = (EditText)findViewById(R.id.register_pw);
		PasswordAgain = (EditText)findViewById(R.id.re_pw);
		Reg = (Button)findViewById(R.id.register_btn);

	}

	private void initViews() {
		mBackImg.setOnClickListener(this);
		Reg.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
			case R.id.back_img:
				this.finish();
				break;
			case R.id.register_btn:
				switch(checkInput()){
					case 0: Toast.makeText(RegisterActivity.this, "信息没有填完整", Toast.LENGTH_SHORT).show(); break;
					case 1:
						Map<String, String> req_para = new HashMap<String, String>();
						req_para.put("name", UserName.getText().toString());
						req_para.put("studentNum", StudentNum.getText().toString());
						req_para.put("cardNum",  CardNum.getText().toString());
						req_para.put("password", Password.getText().toString());
						HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
						break;
					case 2: Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show(); break;
				}

			default:
				break;
		}
	}

	private int checkInput(){
		String name = UserName.getText().toString();
		String studentNum = StudentNum.getText().toString();
		String cardNum = CardNum.getText().toString();
		String password = Password.getText().toString();
		String password_again = PasswordAgain.getText().toString();
		if( name.equals("") || studentNum.equals("") || cardNum.equals("") || password.equals("") || password_again.equals("") ){
			return 0; //信息没有填完整
		}else if(password.equals(password_again)){
			return 1; //信息正确
		}else
			return 2; //密码不一致
	}

}
