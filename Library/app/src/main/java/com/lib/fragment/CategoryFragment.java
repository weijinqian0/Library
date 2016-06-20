package com.lib.fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.utils.*;
import com.lib.activity.MainActivity;
import com.lib.activity.R;
import com.lib.view.Search;
import com.lib.view.SeatSlider;
import com.lib.view.SeatSlider.onStatusListener;

public class CategoryFragment extends BaseFragment {

	private static final String url = "http://192.168.191.1:8080/LibrarySeatServer/deskInfo";

	private static final String TAG = "CatogoryFragment";
	private Activity mActivity;
	private TextView mTitleTv;
	private Search search;
	private SeatSlider mSeatSlider;
	private View view_mask;

	private int RoomId, DeskId;

	private MyListener mListener = new MyListener();

	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {
			// TODO Auto-generated method stub
			Log.d("CategoryFragment", response);
			//解析json数据
			try {

				List<Map<String, Object>> temp = HandleResponseData.getDeskInfo(response);
				if(temp != null){
					mSeatSlider.setGridViewData(temp);
				}

			} catch (JSONException e) {
				// 输出错误信息
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("CategoryFragment", elem.toString());
				}
			}

			mActivity.runOnUiThread(new Runnable(){

				@Override
				public void run(){
					mSeatSlider.show();
				}
			});

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			mActivity.runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("CategoryFragment", elem.toString());
					}

					Toast.makeText(mActivity, "更新失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	//重写onAttach,保证和Activity通信
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}


	//调用该方法创建fragment,inflater产生一个对象，inflate加载fragment的布局
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view =  inflater.inflate(R.layout.fragment_category, container, false);
		return view;
	}


	/*fragment第一次绘制它的用户界面的时候, 系统会调用此方法. 为了绘制fragment的UI,此方法必须返回一个View
	 * 这个view是你的fragment布局的根view. 如果fragment不提供UI, 可以返回null.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		initViews(view);
//		new Thread(new Runnable(){
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				long initial_time = System.currentTimeMillis();
//				while(true){
//					long current_time = System.currentTimeMillis();
//					if(current_time - initial_time > 6000){
//						Message message = new Message();
//						message.what = TIME_UP;
//						handler.sendMessage(message);
//						break;
//					}
//				}
//			}
//
//		}).run();
	}

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		((MainActivity) mActivity).openNFC();
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		((MainActivity) mActivity).closeNFC();
	}

	private void initViews(View view) {
		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.choose_seat);
		search = (Search)view.findViewById(R.id.search_view);
		search.setWillNotDraw(false);
		mSeatSlider = (SeatSlider)view.findViewById(R.id.mSeatSlider);
		view_mask = (View)view.findViewById(R.id.view_mask);
		mSeatSlider.setEnabled(true);
		//模拟扫描nfc
		//scanNFC();
		//设置遮罩阴影层点击消失该界面
		view_mask.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mSeatSlider.isShow()) {
					mSeatSlider.dismiss();
				}
			}

		});

		//设置座位显示状态监听
		mSeatSlider.setOnStatusListener(new onStatusListener() {

			@Override
			public void onShow() {
				//显示
				view_mask.setVisibility(View.VISIBLE);
			}

			@Override
			public void onDismiss() {
				//隐藏
				view_mask.setVisibility(View.GONE);
			}
		});

    	/*
    	//暂时将seatslider出现的监听器设置在title上，实际应设置在扫描到NFC之后
    	mTitleTv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mSeatSlider.isShow()) {
					mSeatSlider.dismiss();
				} else {
					mSeatSlider.show();
				}
			}
		});
    	*/

	}


	@Override
	public String getFragmentName() {
		// TODO Auto-generated method stub
		return TAG;
	}

	public void scanNFC(String data){

		//NFC扫描到的数据是utf-8编码的
		String[] desk_info = data.split(":");
		RoomId = Integer.valueOf(desk_info[0]);
		DeskId = Integer.valueOf(desk_info[1]);
		updateDeskData();

	}

	private void updateDeskData(){
		if(MainActivity.UsrInfo.get("loginState").equals("1")){
			//发送请求，获取桌子信息
			final Map<String, String> req_para = new HashMap<String, String>();
			//请求类型1表示请求桌子数据
			req_para.put("reqType", "1");
			req_para.put("desk", String.valueOf(DeskId));
			req_para.put("room", String.valueOf(RoomId));
			//final MyListener listener = new MyListener();
			HttpUtil.sendHttpRequestPost(url, req_para, mListener);
		}else if(MainActivity.UsrInfo.get("loginState").equals("0"))
			//回到主线程刷新显示
			mActivity.runOnUiThread(new Runnable(){
				@Override
				public void run(){
					Toast.makeText(mActivity, "请先登录，才能占座", Toast.LENGTH_SHORT).show();
				}

			});

	}

}