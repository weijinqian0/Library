package com.lib.utils;

import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.*;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lib.activity.R;


public class HandleResponseData {//解析服务器返回的json数据，转换成用于显示的数据。显示数据采用什么格式？采用java集合类还是自定义一个数据显示类？

	public static List<Map<String, String>> getRoomInfo(String response) throws JSONException{
		JSONArray json_array = new JSONArray(response);
		List<Map<String, String>> seatStatistic = new ArrayList<Map<String, String>>();
		int seat_sum = 0;
		int empty_sum = 0;
		double ratio_sum;
		for(int i = 0; i < json_array.length(); i ++){
			JSONObject json_object = json_array.getJSONObject(i);
			String name = json_object.getString("name");
			int seat_num = json_object.getInt("seatNum");
			seat_sum += seat_num;
			int empty_num = json_object.getInt("emptyNum");
			empty_sum += empty_num;
			double ratio = (double)empty_num / seat_num;
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMinimumFractionDigits(0);//设置保留小数位
			nf.setRoundingMode(RoundingMode.HALF_UP); //设置舍入模式
			String percent = nf.format(ratio);
			//String statistic = name + " 座位个数:" + seat_num + " 空座:" + empty_num + " 空座率:" + percent;
			Map<String, String> statistic = new HashMap<String, String>();
			statistic.put("name", name);
			statistic.put("seatNum", String.valueOf(seat_num));
			statistic.put("emptyNum", String.valueOf(empty_num));
			statistic.put("emptyRatio", String.valueOf(percent));
			seatStatistic.add(statistic);
		}
		ratio_sum = (double)empty_sum / seat_sum;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(1);//设置保留小数位
		nf.setRoundingMode(RoundingMode.HALF_UP); //设置舍入模式
		String percent = nf.format(ratio_sum);
		//String statistic = "总馆" + " 座位个数:" + seat_sum + " 空座:" + empty_sum + " 空座率:" + percent;
		Map<String, String> statistic = new HashMap<String, String>();
		statistic.put("name", "总馆");
		statistic.put("seatNum", String.valueOf(seat_sum));
		statistic.put("emptyNum", String.valueOf(empty_sum));
		statistic.put("emptyRatio", String.valueOf(percent));
		seatStatistic.add(statistic);
		return seatStatistic;
	}

	public static ArrayList<Map<String, Object>> getDeskInfo(String response) throws JSONException{
		if("".equals(response))
			return null;
		else{
			JSONArray json_array = new JSONArray(response);
			ArrayList<Map<String, Object>> deskData = new ArrayList<Map<String, Object>>();
			for(int i = 0; i < json_array.length(); i ++){
				JSONObject json_object = json_array.getJSONObject(i);
				HashMap<String, Object> map = new HashMap<String, Object>();
				//int seat_id = Integer.valueOf(json_object.getString("seatId"));
				//map.put("seatId", String.valueOf(1 + (seat_id-1) % 6));
				map.put("seatId", String.valueOf(json_object.getString("seatId")));
				map.put("seatState", json_object.getString("seatState"));
				map.put("roomId", json_object.getString("roomId"));
				map.put("deskId", json_object.getString("deskId"));
				//设置座位的图标
				if( "1".equals( map.get("seatState").toString() ) ){
					map.put("seatIcon", R.drawable.book);
				}
				else
					map.put("seatIcon", R.drawable.heart);
				deskData.add(map);
			}
			return deskData;
		}
	}

	public static Map<String, String> getUsrInfo(String response) throws JSONException{
		if(!response.equals("")){
			JSONObject data = new JSONObject(response);
			Map<String, String> map = new HashMap<String, String>();
			map.put("cardNum", data.getString("cardNum"));
			map.put("loginState", data.getString("loginState"));
			map.put("usrName", data.getString("usrName"));
			map.put("passWord", data.getString("passWord"));
			map.put("seatId", data.getString("seatId"));
			map.put("studentNum", data.getString("studentNum"));
			map.put("usrId", data.getString("usrId"));
			return map;
		}else
			return null;
	}

	public static List<Map<String, Integer>> getSeatInfo(String response) throws JSONException{
		if("".equals(response))
			return null;
		else{
			JSONArray json_array = new JSONArray(response);
			ArrayList<Map<String, Integer>> seatData = new ArrayList<Map<String, Integer>>();
			for(int i = 0; i < json_array.length(); i ++){
				JSONObject json_object = json_array.getJSONObject(i);
				HashMap<String, Integer> map = new HashMap<String, Integer>();
				map.put("seatId", Integer.valueOf(json_object.getString("seatId")));
				map.put("seatState", Integer.valueOf(json_object.getString("seatState")));
				seatData.add(map);
			}
			return seatData;
		}
	}

}
