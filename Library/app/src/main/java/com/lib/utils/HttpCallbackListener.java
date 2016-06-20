package com.lib.utils;

public interface HttpCallbackListener {
	
	public void onFinish(String response);
	public void onError(Exception e);
	
}
