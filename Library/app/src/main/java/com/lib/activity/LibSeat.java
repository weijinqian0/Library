package com.lib.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lib.seatmodel.Seat;
import com.lib.seatmodel.SeatInfo;
import com.lib.utils.HandleResponseData;
import com.lib.utils.HttpCallbackListener;
import com.lib.utils.HttpUtil;
import com.lib.utils.OnSeatClickListener;
import com.lib.view.SSThumView;
import com.lib.view.SSView;

public class LibSeat extends Activity{

	private static final String url = "http://192.168.191.1:8080/LibrarySeatServer/seatInfo";
	private TextView mTitleTv;
	private ImageView mBackImg;
	private int row = 2;
	private int rowCount = 20;
	private SSView mSSView;
	private SSThumView mSSThumView;
	private ArrayList<SeatInfo> list_seatInfos = new ArrayList<SeatInfo>();
	private ArrayList<ArrayList<Integer>> list_seat_conditions = new ArrayList<ArrayList<Integer>>();

	private class MyListener implements HttpCallbackListener{

		public void onFinish(String response) {
			// TODO Auto-generated method stub
			Log.d("LibSeat", response);
			//解析json数据
			try {

				List<Map<String, Integer>> temp = HandleResponseData.getSeatInfo(response);
				if(temp != null){
					setSeatInfo(temp);//初始化座位状态信息，形成list_seatInfos, list_seat_conditions
				}

			} catch (JSONException e) {
				// 输出错误信息
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("LibSeat", elem.toString());
				}
			}

			runOnUiThread(new Runnable(){

				@Override
				public void run(){
					Toast.makeText(LibSeat.this, "更新成功", Toast.LENGTH_SHORT).show();
					mSSView.init(rowCount, row, list_seatInfos, list_seat_conditions, mSSThumView);
				}
			});

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("LibSeat", elem.toString());
					}

					Toast.makeText(LibSeat.this, "更新失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lib_seat);
		init();
		Intent intent = getIntent();
		mTitleTv.setText(intent.getStringExtra("name"));
		final Map<String, String> req_para = new HashMap<String, String>();
		req_para.put("room", intent.getStringExtra("id"));
		HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
	}

	private void init() {

		mSSView = (SSView)this.findViewById(R.id.mSSView);
		mSSThumView = (SSThumView)this.findViewById(R.id.ss_ssthumview);
		//setSeatInfo();//初始化座位状态信息，形成list_seatInfos, list_seat_conditions
		//mSSView.init(rowCount, row, list_seatInfos, list_seat_conditions, mSSThumView);
		//设置监听器
		mSSView.setOnSeatClickListener(new OnSeatClickListener() {

			@Override
			public boolean selectSeat(int column_num, int row_num, String string) {
				// TODO Auto-generated method stub
				int desk_num = 5 * ( row_num / 2 ) + column_num / 3 + 1;
				int seat_num = 3 * ( row_num % 2 ) + column_num % 3 + 1;
				String desc =  String.valueOf(desk_num) + "桌" + "" + String.valueOf(seat_num) +"座" + string;
				Toast.makeText(LibSeat.this,desc.toString(), Toast.LENGTH_SHORT).show();
				return false;
			}

		});
		mTitleTv = (TextView)findViewById(R.id.title_tv);
		//mTitleTv.setText("科技馆");
		mBackImg = (ImageView) findViewById(R.id.back_img);
		mBackImg.setVisibility(View.VISIBLE);
		mBackImg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
	}


	//具体设置座位的初始状态,0为走道，1为可选，2为离开，3为已被使用。具体的座位信息需要从服务器获得
	private void setSeatInfo(List<Map<String, Integer>> seat_info) {
		row = seat_info.size() / 15;
		row += row / 2;
		/*将座位信息转换为按照六个一组的显示格式。该程序在画图时是按照一行一行的画，所以在座位时需要找到其对应在seat_info中的数据。
		 * 012 6, 7, 8 12,13,14 18,19,20 24,25,26
		 * 345 9,10,11 15,16,17 19,20,21 27,28,29
		 * ...
		 */
		for(int i=0; i<row; i++) {
			int start = 0; //每一行开始座位对应的座位号
			SeatInfo mSeatInfo = new SeatInfo();
			ArrayList<Seat> mSeatList = new ArrayList<Seat>();
			ArrayList<Integer> mConditionList = new ArrayList<Integer>();
			for(int j=0; j<rowCount; j++) {
				Seat mSeat = new Seat();
				//指示过道
				int row_flag = (i + 1) % 3;
				int column_flag = j % 4;
				if(j == 0){
					mSeat.setIndex("Z");//Z为走道
					mConditionList.add(0);//0为走道
					if(row_flag == 1)
						start = ( (i + 1) / 3 ) * 30; //30表示一排桌子30个座位，每桌6个桌子时，共5张桌子。
					if(row_flag == 2)
						start = ( (i + 1) / 3 ) * 30 + 3;
				}
				else{
					//判断是否为走道
					if(row_flag == 0 || column_flag == 0){
						mSeat.setIndex("Z");//Z为走道
						mConditionList.add(0);//0为走道
						if(column_flag == 0)
							start = start + 3;
					}
					else{
						if(start >= seat_info.size()){
							break;
						}
						else{
							mSeat.setIndex(String.valueOf(j));
							int state;
							if(seat_info.get(start).get("seatState") == 0)
								state = 1;
							else
								state = 3;
							mConditionList.add( state );
							start++;
						}
					}
				}
				mSeatList.add(mSeat);
			}

			if ((i + 1) % 3 == 0) {//将走道的desc设为"*"
				mSeatInfo.setDesc("*");
				mSeatInfo.setRow("*");
			} else {
				int k = (i + 1) -(i + 1) / 3;
				mSeatInfo.setDesc(String.valueOf(k));
				mSeatInfo.setRow(String.valueOf(k));
			}
			mSeatInfo.setSeatList(mSeatList);
			list_seatInfos.add(mSeatInfo);
			list_seat_conditions.add(mConditionList);
		}
	}

}
