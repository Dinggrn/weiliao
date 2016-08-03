package com.Dinggrn.weiliao.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager{
	
	private boolean isScroll = true;
	
	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		//���return true��ʱ�򣬾ʹ�����ViewPagerҪ�����¼�
		return isScroll && super.onTouchEvent(arg0);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0) {
		//���return true��ʱ�򣬾ʹ�����ViewPager���¼��ػ���
		return isScroll && super.onInterceptTouchEvent(arg0);
	}
	
	public void setScrollEnable(boolean flag){
		this.isScroll = flag;
	}

}
