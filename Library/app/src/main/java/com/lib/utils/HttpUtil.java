package com.lib.utils;

import java.net.*;
import java.io.*;
import java.util.Map;

import android.util.Log;

public class HttpUtil {

	public static void sendHttpRequestPost(final String address, final Map<String, String> params, final HttpCallbackListener listener){
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;

				PrintWriter printWriter = null;
				BufferedReader bufferedReader = null;

				StringBuffer response = new StringBuffer();
				StringBuffer request = new StringBuffer();

				// 组织请求参数
				if (null != params && !params.isEmpty()){
					for (Map.Entry<String, String> entry : params.entrySet()){
						request.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
					}
					//< 删除多余的&
					request.deleteCharAt(request.length() - 1);
				}
				Log.d("HttpUtil", request.toString());

				try{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("POST");
					connection.setRequestProperty("Content-Length", String.valueOf(request.length()));
					connection.setDoInput(true);
					connection.setDoOutput(true);
					printWriter = new PrintWriter(connection.getOutputStream());
					printWriter.write(request.toString());
					printWriter.flush();

					int responseCode = connection.getResponseCode();
					if(responseCode != 200){
						Log.d("HttpUtil", "Post Fail");
					}else{
						Log.d("HttpUtil", "Post Success");
						bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
						String line;
						while ((line = bufferedReader.readLine()) != null) {
							response.append(line);
						}
					}

					if(listener != null){
						listener.onFinish(response.toString());
					}

				}catch(Exception e){
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
					try {
						if (printWriter != null) {
							printWriter.close();
						}
						if (bufferedReader != null) {
							bufferedReader.close();
						}
					} catch (IOException ex) {
						ex.printStackTrace();
					}
				}

			}

		}).start();
	}

	public static void sendHttpRequestGet(final String address, final HttpCallbackListener listener){
		new Thread(new Runnable(){

			public void run() {
				// TODO Auto-generated method stub
				HttpURLConnection connection = null;
				StringBuilder response = new StringBuilder();
				try{
					URL url = new URL(address);
					connection = (HttpURLConnection) url.openConnection();
					connection.setRequestMethod("GET");
					connection.setConnectTimeout(8000);
					connection.setReadTimeout(8000);
					connection.setDoInput(true);
					//connection.setDoOutput(true);
					int responseCode = connection.getResponseCode();
					Log.d("HttpUtil", String.valueOf(responseCode));
					if(responseCode != 200){
						Log.d("HttpUtil", "Get Fail");
					}else{
						InputStream in = connection.getInputStream();
						BufferedReader reader = new BufferedReader(new InputStreamReader(in));
						String line;
						while((line = reader.readLine()) != null){
							response.append(line);
						}
						Log.d("HttpUtil", "Get Success");
					}
					if(listener != null){
						listener.onFinish(response.toString());
					}
				}catch(Exception e){
					if(listener != null){
						listener.onError(e);
					}
				}finally{
					if(connection != null){
						connection.disconnect();
					}
				}
			}

		}).start();

	}
}


