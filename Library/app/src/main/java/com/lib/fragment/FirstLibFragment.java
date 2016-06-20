package com.lib.fragment;

import java.util.*;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.lib.activity.LibSeat;
import com.lib.activity.R;

//ViewPager用到的不同图书馆的座位分布情况布局
public class FirstLibFragment extends Fragment{

	private Activity mActivity;
	private TextView libTv1, libTv2, libTv3;
	private ImageView libImg1, libImg2, libImg3;
	private TextView UseableSeat1, UseableSeat2, UseableSeat3;
	private TextView percent1, percent2, percent3;

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		this.mActivity = activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.lib_item, container, false);
		View view1 = view.findViewById(R.id.subitem1);
		View view2 = view.findViewById(R.id.subitem2);
		View view3 = view.findViewById(R.id.subitem3);

		libTv1 = (TextView)view1.findViewById(R.id.lib_name);
		libTv2 = (TextView)view2.findViewById(R.id.lib_name);
		libTv3 = (TextView)view3.findViewById(R.id.lib_name);

		libImg1 = (ImageView)view1.findViewById(R.id.lib_img);
		libImg1.setBackgroundResource(R.drawable.library);
		libImg2 = (ImageView)view2.findViewById(R.id.lib_img);
		libImg2.setBackgroundResource(R.drawable.library);
		libImg3 = (ImageView)view3.findViewById(R.id.lib_img);
		libImg3.setBackgroundResource(R.drawable.library);

		UseableSeat1 = (TextView)view1.findViewById(R.id.seat_usable);
		UseableSeat2 = (TextView)view2.findViewById(R.id.seat_usable);
		UseableSeat3 = (TextView)view3.findViewById(R.id.seat_usable);

		percent1 = (TextView)view1.findViewById(R.id.seat_percent);
		percent2 = (TextView)view2.findViewById(R.id.seat_percent);
		percent3 = (TextView)view3.findViewById(R.id.seat_percent);

		libImg1.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, LibSeat.class);
				intent.putExtra("id", "1");
				intent.putExtra("name", libTv1.getText().toString());
				mActivity.startActivity(intent);

			}

		});

		libImg2.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, LibSeat.class);
				intent.putExtra("id", "2");
				intent.putExtra("name", libTv2.getText().toString());
				mActivity.startActivity(intent);

			}

		});

		libImg3.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(mActivity, LibSeat.class);
				intent.putExtra("id", "3");
				intent.putExtra("name", libTv3.getText().toString());
				mActivity.startActivity(intent);

			}

		});
		return view;
	}

	public void refreshDisplay(List<Map<String, String>> data){
		libTv1.setText( changeToChinese(data.get(0).get("name")) );
		UseableSeat1.setText( data.get(0).get("emptyNum") );
		percent1.setText( data.get(0).get("emptyRatio") );

		libTv2.setText( changeToChinese(data.get(1).get("name")) );
		UseableSeat2.setText( data.get(1).get("emptyNum") );
		percent2.setText( data.get(1).get("emptyRatio") );

		libTv3.setText( changeToChinese(data.get(2).get("name")) );
		UseableSeat3.setText( data.get(2).get("emptyNum") );
		percent3.setText( data.get(2).get("emptyRatio") );
	}

	private String changeToChinese(String lib_name){
		String name;
		switch(lib_name){
			case "Literature":
				name = "人文馆";
				break;
			case "Nature":
				name = "自然馆";
				break;
			case "Society":
				name = "社会馆";
				break;
			case "Periodical":
				name = "期刊馆";
				break;
			case "Study_1":
				name = "自习室1";
				break;
			case "Study_2":
				name = "自习室2";
				break;
			default:
				name = "";
				break;
		}
		return name;
	}

}
