package com.lib.view;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lib.utils.*;
import com.lib.activity.MainActivity;
import com.lib.activity.R;
import com.lib.fragment.CategoryFragment;
import com.lib.utils.BaseTools;

public class SeatSlider extends RelativeLayout {

	private static final String url = "http://192.168.191.1:8080/LibrarySeatServer/deskInfo";

	private GridView seat;
	private List<Map<String,Object>> gridViewData = new ArrayList<Map<String, Object>>();
	private SimpleAdapter adapter = null;
	private String seatId;
	private int ReqType;
	private Context MyContext;
	/** Scroller 拖动类 */
	private Scroller mScroller;
	/** 屏幕高度  */
	private int mScreenHeight = 0;
	/** 屏幕宽度  */
	private int mScreenWidth = 0;
	/** 点击时候Y的坐标*/
	private int downY = 0;
	/** 拖动时候Y的坐标*/
	private int moveY = 0;
	/** 拖动时候Y的方向距离*/
	private int scrollY = 0;
	/** 松开时候Y的坐标*/
	private int upY = 0;
	/** 是否在移动*/
	private Boolean isMoving = false;
	/** 布局的高度*/
	private int viewHeight = 0;
	/** 是否打开*/
	public boolean isShow = false;
	/** 是否可以拖动*/
	public boolean mEnabled = true;
	/** 点击外面是否关闭该界面*/
	public boolean mOutsideTouchable = true;
	/** 上升动画时间 */
	private int mDuration = 800;

	//	private HorizontalScrollView mImageContainer;
//	private GridViewAdapter mGridViewAdapter;
//	private GridView mGridView;
	private final static String TAG = "SeatSlider";

	private class MyListener implements HttpCallbackListener{

		private int flag;
		public void onFinish(String response) {
			// TODO Auto-generated method stub
			//解析json数据
			try {
				gridViewData.clear();
				List<Map<String, Object>> temp = HandleResponseData.getDeskInfo(response);
				if(temp != null){
					gridViewData.addAll(temp);
					flag = 1;
				}
				else
					flag = 0;
			} catch (JSONException e) {
				// 输出错误信息
				for(StackTraceElement elem : e.getStackTrace()) {
					Log.e("SeatSlider", elem.toString());
				}
			}

			//回到主线程刷新显示
			((Activity) MyContext).runOnUiThread(new Runnable(){
				@Override
				public void run(){
					if(flag == 1){
						if(ReqType == 2)
							MainActivity.UsrInfo.put("seatId", seatId);
						Toast.makeText(MyContext, "更新成功", Toast.LENGTH_SHORT).show();
						adapter.notifyDataSetChanged();
					}else
						Toast.makeText(MyContext, "占座失败", Toast.LENGTH_SHORT).show();
				}

			});

		}

		public void onError(final Exception e) {
			// TODO Auto-generated method stub
			((Activity) MyContext).runOnUiThread(new Runnable(){

				@Override
				public void run(){

					for(StackTraceElement elem : e.getStackTrace()) {
						Log.e("SeatSlider", elem.toString());
					}

					Toast.makeText(MyContext, "更新失败", Toast.LENGTH_SHORT).show();
				}
			});
		}
	}

	public SeatSlider(Context context) {
		super(context);
		MyContext = context;
		init(context);
	}

	public SeatSlider(Context context, AttributeSet attrs) {
		super(context, attrs);
		MyContext = context;
		init(context);
	}

	public SeatSlider(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		MyContext = context;
		init(context);
	}

	private void init(Context context) {
		setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
		setFocusable(true);
		mScroller = new Scroller(context);
		mScreenHeight = BaseTools.getWindowHeigh(context);
		mScreenWidth = BaseTools.getWindowWidth(context);
		//背景设成透明
		this.setBackgroundColor(Color.argb(0, 0, 0, 0));
		//加载布局
		final View view = LayoutInflater.from(context).inflate(R.layout.seat_item, null);

		ImageView btn_close = (ImageView)view.findViewById(R.id.btn_close);
		btn_close.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}

		});

		seat = (GridView)view.findViewById(R.id.seat);

		adapter = new SimpleAdapter(context, gridViewData, R.layout.seat_subitem,
				new String[]{"seatId", "seatIcon"}, new int[]{R.id.ItemText, R.id.ItemImage});
		seat.setAdapter(adapter);
		seat.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				String seat_state = (String)gridViewData.get(position).get("seatState");
				final String room_id = (String)gridViewData.get(position).get("roomId");
				final String desk_id = (String)gridViewData.get(position).get("deskId");
				final String seat_id = (String)gridViewData.get(position).get("seatId");
				String temp = MainActivity.UsrInfo.get("seatId");
				if("0".equals(temp) || temp == null){
					if(seat_state.equals("0")){
						String message = "该位置位于 " + room_id + "馆" + desk_id + "桌" + "\n" +
								"没有人使用该座位，你可以选择：";
						new AlertDialog.Builder(MyContext)
								.setTitle("请选择")
								.setMessage(message)
								.setPositiveButton("确认使用",new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										seatId = seat_id;
										final Map<String, String> req_para = new HashMap<String, String>();
										//请求类型1表示请求桌子数据
										ReqType = 2;
										req_para.put("reqType", "2");
										req_para.put("seat", seat_id);
										req_para.put("desk", desk_id);
										req_para.put("room", room_id);
										req_para.put("usr", MainActivity.UsrInfo.get("usrId"));
										req_para.put("actionType", "occupy");
										HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
									}

								})
								.show();
					}else{
						String message = "该位置位于 " + room_id + "馆" + desk_id + "桌" + "\n" +
								"该位置有人，你可以选择：";
						new AlertDialog.Builder(MyContext)
								.setTitle("请选择")
								.setMessage(message)
								.setPositiveButton("抢占", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										seatId = seat_id;
										final Map<String, String> req_para = new HashMap<String, String>();
										//请求类型1表示请求桌子数据
										ReqType = 2;
										req_para.put("reqType", "2");
										req_para.put("seat", seat_id);
										req_para.put("desk", desk_id);
										req_para.put("room", room_id);
										req_para.put("usr", MainActivity.UsrInfo.get("usrId"));
										req_para.put("actionType", "occupy");
										HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
									}

								})
								.show();
					}

				}else{
					if(seat_id.equals(temp))
						Toast.makeText(MyContext, "你已经拥有该座位", Toast.LENGTH_SHORT).show();
					else{
						String message = "该位置位于 " + room_id + "馆" + desk_id + "桌" + "\n" +
								"没有人使用该座位，你已经拥有一个座位，确认要换座吗？：";
						new AlertDialog.Builder(MyContext)
								.setTitle("请选择")
								.setMessage(message)
								.setPositiveButton("确认换座",new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
														int which) {
										// TODO Auto-generated method stub
										seatId = seat_id;
										final Map<String, String> req_para = new HashMap<String, String>();
										//请求类型1表示请求桌子数据
										ReqType = 2;
										req_para.put("reqType", "2");
										req_para.put("seat", seat_id);
										req_para.put("desk", desk_id);
										req_para.put("room", room_id);
										req_para.put("usr", MainActivity.UsrInfo.get("usrId"));
										req_para.put("actionType", "change");
										HttpUtil.sendHttpRequestPost(url, req_para, new MyListener());
									}

								})
								.show();
					}
				}
			}

		});

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
		addView(view, params);


		view.post(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				viewHeight = view.getHeight();

			}

		});

		SeatSlider.this.scrollTo(0, mScreenHeight);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if(!mEnabled){
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				downY = (int) event.getY();
				Log.d(TAG, "downY = " + downY);
				//如果完全显示的时候，让布局得到触摸监听，如果不显示，触摸事件不拦截，向下传递
				if(isShow){
					return true;
				}
				break;
			case MotionEvent.ACTION_MOVE:
				moveY = (int) event.getY();
				scrollY = moveY - downY;
				//向下滑动
				if (scrollY > 0) {
					if(isShow){
						scrollTo(0, -Math.abs(scrollY));
					}
				}else{
					if(mScreenHeight - this.getTop() <= viewHeight && !isShow){
						scrollTo(0, Math.abs(viewHeight - scrollY));
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				upY = (int) event.getY();
				if(isShow){
					if( this.getScrollY() <= -(viewHeight /2)){
						startMoveAnim(this.getScrollY(),-(viewHeight - this.getScrollY()), mDuration);
						isShow = false;
						Log.d("isShow", "false");
					} else {
						startMoveAnim(this.getScrollY(), -this.getScrollY(), mDuration);
						isShow = true;
						Log.d("isShow", "true");
					}
				}
				Log.d("this.getScrollY()", ""+this.getScrollY());
				changed();
				break;
			case MotionEvent.ACTION_OUTSIDE:
				Log.d(TAG, "ACTION_OUTSIDE");
				break;
			default:
				break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 拖动动画
	 * @param startY
	 * @param dy  移动到某点的Y坐标距离
	 * @param duration 时间
	 */
	public void startMoveAnim(int startY, int dy, int duration) {
		isMoving = true;
		mScroller.startScroll(0, startY, 0, dy, duration);
		invalidate();//通知UI线程的更新
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 更新界面
			postInvalidate();
			isMoving = true;
		} else {
			isMoving = false;
		}
		super.computeScroll();
	}

	/** 打开界面 */
	public void show(){
		if(!isShow && !isMoving){
			SeatSlider.this.startMoveAnim(-viewHeight,   viewHeight, mDuration);
			isShow = true;
			Log.d("isShow", "true");
			changed();
		}
	}

	/** 关闭界面 */
	public void dismiss(){
		if(isShow && !isMoving){
			SeatSlider.this.startMoveAnim(0, -viewHeight, mDuration);
			isShow = false;
			Log.d("isShow", "false");
			changed();
		}
	}

	/** 是否打开 */
	public boolean isShow(){
		return isShow;
	}

	/** 获取是否可以拖动*/
	public boolean isSlidingEnabled() {
		return mEnabled;
	}

	/** 设置是否可以拖动*/
	public void setSlidingEnabled(boolean enabled) {
		mEnabled = enabled;
	}

	/**
	 * 设置监听接口，实现遮罩层效果
	 */
	public void setOnStatusListener(onStatusListener listener){
		this.statusListener = listener;
	}

	public void setOutsideTouchable(boolean touchable) {
		mOutsideTouchable = touchable;
	}

	/**
	 * 显示状态发生改变时候执行回调借口
	 */
	public void changed(){
		if(statusListener != null){
			if(isShow){
				statusListener.onShow();
			}else{
				statusListener.onDismiss();
			}
		}
	}

	/** 监听接口*/
	public onStatusListener statusListener;

	/**
	 * 监听接口，来在主界面监听界面变化状态
	 */
	public interface onStatusListener{
		/**  开打状态  */
		public void onShow();
		/**  关闭状态  */
		public void onDismiss();
	}



	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		// TODO Auto-generated method stub
		super.onLayout(changed, l, t, r, b);
	}

	public void setGridViewData(List< Map<String,Object> > data){
		gridViewData.clear();
		gridViewData.addAll(data);
		((Activity) MyContext).runOnUiThread(new Runnable(){
			@Override
			public void run(){
				adapter.notifyDataSetChanged();
			}

		});

	}
}

