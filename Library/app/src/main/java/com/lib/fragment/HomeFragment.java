package com.lib.fragment;

import java.util.*;

import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.utils.HttpCallbackListener;
import com.lib.utils.HandleResponseData;
import com.lib.utils.HttpUtil;
import com.lib.activity.R;
import com.lib.adapter.LibPagerAdapter;
import com.lib.view.CirclePageIndicator;


public class HomeFragment extends BaseFragment {

	private static final String url = "http://192.168.191.1:8080/LibrarySeatServer/roomInfo";

	private static final String TAG = "HomeFragment";
	private Activity mActivity;
	private TextView mTitleTv;
	private ImageView mUpdateImg;

//	private TextView TotalSeat;
//	private TextView EmptySeat;
//	private TextView EmptyRatio;

	private LibPagerAdapter mLibPagerAdapter;
	//private ImageView libImg;
	private ViewPager mViewPager;
	private List<Fragment> fragments;
	private FirstLibFragment LibFragment = new FirstLibFragment();
	private SecondLibFragment LibFragment_2 = new SecondLibFragment();

	private List<Map<String, String>> seatStatistic = new ArrayList<Map<String, String>>();
	private List<Map<String, String>> seatStatistic_2 = new ArrayList<Map<String, String>>();

	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {
			// TODO Auto-generated method stub
			Log.d("MainActivity", response);
			//解析json数据
			try {
				seatStatistic.clear();
				seatStatistic.addAll(HandleResponseData.getRoomInfo(response).subList(0, 3));
				seatStatistic_2.clear();
				seatStatistic_2.addAll(HandleResponseData.getRoomInfo(response).subList(3, 6));
			} catch (JSONException e) {
				// 输出错误信息
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("HomeFragment", elem.toString());
				}
			}

			//回到主线程刷新显示
			mActivity.runOnUiThread(new Runnable(){

				@Override
				public void run(){
					Toast.makeText(mActivity, "更新成功", Toast.LENGTH_SHORT).show();
//					TotalSeat.setText("总座位数:" + seatStatistic.get(3).get("seatNum"));
//					EmptySeat.setText("剩余座位:" + seatStatistic.get(3).get("emptyNum"));
//					EmptyRatio.setText("空座率:" + seatStatistic.get(3).get("emptyRatio"));
					LibFragment.refreshDisplay(seatStatistic);
					LibFragment_2.refreshDisplay(seatStatistic_2);
					mLibPagerAdapter.notifyDataSetChanged();
				}

			});

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			mActivity.runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("HomeFragment", elem.toString());
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
		View view =  inflater.inflate(R.layout.fragment_home, container, false);
		return view;
	}


	/*fragment第一次绘制它的用户界面的时候, 系统会调用此方法. 为了绘制fragment的UI,此方法必须返回一个View
	 * 这个view是你的fragment布局的根view. 如果fragment不提供UI, 可以返回null.
	 */
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		init(view);
	}

	private void init(View view) {
		mTitleTv = (TextView) view.findViewById(R.id.title_tv);
		mTitleTv.setText(R.string.seat_preview);
		//	View v = view.findViewById(R.id.lib_total);
//		TotalSeat = (TextView) v.findViewById(R.id.home_total_seat);
//		EmptySeat = (TextView) v.findViewById(R.id.home_remain_seat);
//		EmptyRatio = (TextView) v.findViewById(R.id.home_percent);

		//搜索分馆座位分布的按钮
		//刷新座位的按钮
		mUpdateImg = (ImageView)view.findViewById(R.id.update_img);
		mUpdateImg.setVisibility(View.VISIBLE);
		mUpdateImg.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				refreshDisplay();
			}

		});

		//位ViewPager加载布局
		fragments = new ArrayList<Fragment>();
		fragments.add(LibFragment);
		fragments.add(LibFragment_2);
		//fragments.add(new SecondLibFragment());
		mViewPager = (ViewPager)view.findViewById(R.id.viewpager);
		mLibPagerAdapter = new LibPagerAdapter (getChildFragmentManager(), fragments);
		mViewPager.setAdapter(mLibPagerAdapter);
		((CirclePageIndicator)view. findViewById(R.id.indicator))
				.setViewPager(mViewPager);
		refreshDisplay();
	}

	@Override
	public String getFragmentName() {
		// TODO Auto-generated method stub
		return TAG;
	}

	public void refreshDisplay(){
		final MyListener listener = new MyListener();
		HttpUtil.sendHttpRequestGet(url, listener);
	}

}
