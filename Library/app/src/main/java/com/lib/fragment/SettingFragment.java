package com.lib.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.activity.FeedbackActivity;
import com.lib.activity.LoginActivity;
import com.lib.activity.MainActivity;
import com.lib.activity.PersonalInfoActivity;
import com.lib.activity.R;
import com.lib.activity.StateActivity;
import com.lib.utils.CommonUtils;
import com.lib.utils.ConstantValues;
import com.lib.utils.LogUtils;

/**
 * 个人信息，设置fragment
 * @author Administrator
 *
 */
public class SettingFragment extends BaseFragment implements OnClickListener{

	private static final String TAG = "SettingFragment";
	private Activity mActivity;
	private ImageView user_img;
	private TextView user_info;
	//我的状态
	private RelativeLayout mState;
	// 反馈意见
	private RelativeLayout mFeedbackLayout;
	// 关于我们
	private RelativeLayout mAboutUsLayout;
	public static SettingFragment newInstance() {
		SettingFragment settingFragment = new SettingFragment();
		return settingFragment;
	}


	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}


	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	//加载fragment_settings的布局
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.fragment_setting, container, false);
		return view;
	}


	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
		initEvents();
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}

	public void initViews(View view) {
		user_img = (ImageView)view.findViewById(R.id.user_img);
		user_info = (TextView)view.findViewById(R.id.user_info);
		mState = (RelativeLayout) view.findViewById(R.id.state_btn);
		mFeedbackLayout = (RelativeLayout) view.findViewById(R.id.feedback_btn);
		mAboutUsLayout = (RelativeLayout) view.findViewById(R.id.about_btn);
		//更合理的做法应该是，发送请求查询服务端用户的登陆情况，因为可能会出现登陆后，app在后台运行时被系统收回
		if(MainActivity.UsrInfo.get("loginState").equals("1")){
			user_info.setText(MainActivity.UsrInfo.get("usrName") + "\n" + "在线");
		}
		if(MainActivity.UsrInfo.get("loginState").equals("0")){
			user_info.setText("请登录");
		}
		if(MainActivity.UsrInfo.get("loginState").equals("2")){
			user_info.setText("离开");
		}
	}

	private void initEvents() {
		user_info.setOnClickListener(this);
		user_img.setOnClickListener(this);
		mState.setOnClickListener(this);
		mFeedbackLayout.setOnClickListener(this);
		mAboutUsLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
			case R.id.user_img:
				if(!MainActivity.UsrInfo.get("loginState").equals("0")){
					//CommonUtils.launchActivity(mActivity, PersonalInfoActivity.class);
					Intent intent = new Intent(mActivity, PersonalInfoActivity.class);
					startActivityForResult(intent, ConstantValues.LOGIN_OUT);
				}
				else
					Toast.makeText(mActivity, "未登录", Toast.LENGTH_SHORT).show();
				break;
			case R.id.user_info:
				if(MainActivity.UsrInfo.get("loginState").equals("0")){
					Intent intent = new Intent(mActivity, LoginActivity.class);
					startActivityForResult(intent, ConstantValues.LOGIN_IN);
				}
				break;
			case R.id.state_btn:
				if(!MainActivity.UsrInfo.get("loginState").equals("0")){
					Intent intent = new Intent(mActivity, StateActivity.class);
					startActivityForResult(intent, ConstantValues.STATE);
				}
				else
					Toast.makeText(mActivity, "未登录", Toast.LENGTH_SHORT).show();
				break;
			case R.id.feedback_btn:
				// 反馈意见
				CommonUtils.launchActivity(mActivity, FeedbackActivity.class);
				break;
			case R.id.about_btn:
				// 关于我们
				aboutus();
				break;

			default:
				break;
		}
	}


	@Override
	public String getFragmentName() {
		// TODO Auto-generated method stub
		return TAG;
	}

	public void aboutus(){
		AlertDialog.Builder alert = new AlertDialog.Builder(mActivity);
		alert.setTitle("关于我们");
		alert.setIcon(android.R.drawable.btn_star);
		alert.setMessage("   \n"+"    图书馆占座系统1.0\n"+"        ");
		alert.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data){
		LogUtils.d("SettingFragment", "onActivityResult");
		switch(requestCode){
			case ConstantValues.LOGIN_IN:
				if(resultCode == MainActivity.RESULT_OK){
					MainActivity.UsrInfo.clear();
					MainActivity.UsrInfo.put("usrName", data.getStringExtra("usrName"));
					MainActivity.UsrInfo.put("studentNum", data.getStringExtra("studentNum"));
					MainActivity.UsrInfo.put("loginState", data.getStringExtra("loginState"));
					MainActivity.UsrInfo.put("seatId", data.getStringExtra("seatId"));
					MainActivity.UsrInfo.put("usrId", data.getStringExtra("usrId"));
					if(MainActivity.UsrInfo.get("loginState").equals("1")){
						Toast.makeText(mActivity, "登录成功", Toast.LENGTH_SHORT).show();
						user_info.setText(MainActivity.UsrInfo.get("usrName") + "\n" + "在线");
					}else{
						Toast.makeText(mActivity, "登录失败，可能已在其他设备上登陆", Toast.LENGTH_SHORT).show();
					}
				}
				break;
			case ConstantValues.LOGIN_OUT:
				if(resultCode == MainActivity.RESULT_OK){
					MainActivity.UsrInfo.put("loginState", data.getStringExtra("loginState"));
					if(MainActivity.UsrInfo.get("loginState").equals("0")){
						Toast.makeText(mActivity, "注销成功", Toast.LENGTH_SHORT).show();
						user_info.setText("请登录");

					}
				}
				break;

			case ConstantValues.STATE:
				if(resultCode == MainActivity.RESULT_OK){
					MainActivity.UsrInfo.put("loginState", data.getStringExtra("loginState"));
					switch(MainActivity.UsrInfo.get("loginState")){
						case "0": user_info.setText("请登录"); break;
						case "1": user_info.setText(MainActivity.UsrInfo.get("usrName") + "\n在线"); break;
						case "2": user_info.setText(MainActivity.UsrInfo.get("usrName") +"\n离线"); break;
						default: break;
					}
				}
				break;
			default: break;
		}
	}
}