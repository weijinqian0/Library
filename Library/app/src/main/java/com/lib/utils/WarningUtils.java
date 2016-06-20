package com.lib.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class WarningUtils {

	public static final int CONFIRM = -1;
	public static final int CANCEL = -2;

	public static void showDialog(String str, DialogInterface.OnClickListener listener, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提醒");
		builder.setMessage(str);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setPositiveButton("确定", listener);
		builder.setNegativeButton("取消", listener);
		AlertDialog dialog = builder.create();
		dialog.show();

	}

	public static void showDialog(String str, Context context) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("提醒");
		builder.setMessage(str);
		builder.setIcon(android.R.drawable.btn_star);
		builder.setPositiveButton("确定", null);
		builder.setNegativeButton("取消", null);
		AlertDialog dialog = builder.create();
		dialog.show();
	}
}
