package com.lib.view;

import java.util.ArrayList;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

class GestureListener extends GestureDetector.SimpleOnGestureListener {
	private SSView mSSView;

	GestureListener(SSView paramSSView) {
		mSSView = paramSSView;
	}

	public boolean onDoubleTap(MotionEvent paramMotionEvent) {
		return super.onDoubleTap(paramMotionEvent);
	}

	public boolean onDoubleTapEvent(MotionEvent paramMotionEvent) {
		return super.onDoubleTapEvent(paramMotionEvent);
	}

	public boolean onDown(MotionEvent paramMotionEvent) {
		return false;
	}

	public boolean onFling(MotionEvent paramMotionEvent1,
						   MotionEvent paramMotionEvent2, float paramFloat1, float paramFloat2) {
		return false;
	}

	public void onLongPress(MotionEvent paramMotionEvent) {
	}

	public boolean onScroll(MotionEvent paramMotionEvent1,
							MotionEvent paramMotionEvent2, float x_scroll_distance, float y_scroll_distance) {
		//是否可以移动和点击
		if(!SSView.a(mSSView)){
			return false;
		}
		//显示缩略图
		SSView.showThum(mSSView,true);
		boolean bool1 = true;
		boolean bool2 = true;
		//当控件的宽度和高度小于mSSView的宽度和高度时，将不能继续移动
		if ((SSView.s(mSSView) < mSSView.getMeasuredWidth())
				&& (0.0F == SSView.v(mSSView))){
			bool1 = false;
		}

		if ((SSView.u(mSSView) < mSSView.getMeasuredHeight())
				&& (0.0F == SSView.w(mSSView))){
			bool2  = false;
		}

		if(bool1){
			int k = Math.round(x_scroll_distance);
			//修改排数x轴的偏移量
			SSView.c(mSSView, (float)k);
//			Log.i("TAG", SSView.v(mSSView)+"");
			//修改座位距离排数的横向距离
			SSView.k(mSSView, k);
//			Log.i("TAG", SSView.r(mSSView)+"");
			if (SSView.r(mSSView) < 0) {
				//滑到最左
				SSView.i(mSSView, 0);
				SSView.a(mSSView, 0.0F);
			}

			if(SSView.r(mSSView) + mSSView.getMeasuredWidth() > SSView.s(mSSView)){
				//滑到最右
				SSView.i(mSSView, SSView.s(mSSView) - mSSView.getMeasuredWidth());
				SSView.a(mSSView, (float)(mSSView.getMeasuredWidth() - SSView.s(mSSView)));
			}
		}

		if(bool2){
			//上负下正- 往下滑则减
			int j = Math.round(y_scroll_distance);
			//修改排数y轴的偏移量
			SSView.d(mSSView, (float)j);
			//修改可视座位距离顶端的距离
			SSView.l(mSSView, j);
			Log.i("TAG", SSView.t(mSSView)+"");
			if (SSView.t(mSSView) < 0){
				//滑到顶
				SSView.j(mSSView, 0);
				SSView.b(mSSView, 0.0F);
			}

			if (SSView.t(mSSView) + mSSView.getMeasuredHeight() > SSView
					.u(mSSView)){
				//滑到底
				SSView.j(mSSView, SSView.u(mSSView) - mSSView.getMeasuredHeight());
				SSView.b(mSSView, (float)(mSSView.getMeasuredHeight() - SSView.u(mSSView)));
			}
		}

		mSSView.invalidate();

//		Log.i("GestureDetector", "onScroll----------------------");
		return false;
	}

	public void onShowPress(MotionEvent paramMotionEvent) {
	}

	public boolean onSingleTapConfirmed(MotionEvent paramMotionEvent) {
		return false;
	}

	//点击座位时，将座位id以及状态作为参数传递给OnSeatClickListener
	public boolean onSingleTapUp(MotionEvent paramMotionEvent) {
//		Log.i("GestureDetector", "onSingleTapUp");
//		if(!SSView.a(mSSView)){
//			return false;
//		}

		//排数，通过点击位置的Y坐标换算得到
		int i = SSView.getRow(mSSView, (int) paramMotionEvent.getY());
		//列数
		int j = SSView.getColumn(mSSView, (int)paramMotionEvent.getX());

		if((i>=0 && i< SSView.b(mSSView).size())){
			if(j>=0 && j<((ArrayList<Integer>)(SSView.b(mSSView).get(i))).size()){
				Log.i("TAG", "排数："+ i + "列数："+j);
				ArrayList<Integer> localArrayListConditions = (ArrayList<Integer>) SSView.b(mSSView).get(i);//每排形成一个List
				switch (localArrayListConditions.get(j).intValue()) {
					//源码中设置为 当点击座位时，座位状态发生变化，我们不需要，只需要得到行列的信息，弹出该座位是否被占用就好
					case 1://可选
						//localArrayListConditions.set(j, Integer.valueOf(3));
						if(SSView.getListener(mSSView)!=null){
							SSView.getListener(mSSView).selectSeat((j - j / 4 - 1), i - (i / 3), "可以使用");
						}
						break;
					case 2://使用者离开
						//localArrayListConditions.set(j, Integer.valueOf(3));
						if(SSView.getListener(mSSView)!=null){
							SSView.getListener(mSSView).selectSeat((j - j / 4 - 1), i - (i / 3), "使用者暂时离开");
						}
						break;
					case 3://使用中
						//localArrayListConditions.set(j, Integer.valueOf(3));
						if(SSView.getListener(mSSView)!=null){
							SSView.getListener(mSSView).selectSeat((j - j / 4 - 1), i - (i / 3), "正在使用中");
						}
						break;
					default:
						break;
				}

			}
		}

		//显示缩略图
		SSView.showThum(mSSView,true);
		mSSView.invalidate();
		return false;
	}
}
