package com.Dinggrn.weiliao.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

import com.Dinggrn.weiliao.app.MyApp;

public class SharedPreferenceUtil {
	public static Editor editor;
	public SharedPreferences sp;
	public SharedPreferenceUtil(String filename){
		sp = MyApp.context.getSharedPreferences(filename, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	
	public SharedPreferenceUtil(){
		sp = PreferenceManager.getDefaultSharedPreferences(MyApp.context);
		editor = sp.edit();
	}
	//�Ƿ��������֪ͨ
	public boolean isAllowNotification(){
		return sp.getBoolean("notification", true);
	}
	//����֪ͨʱ�Ƿ�����������ʾ
	public boolean isAllowSound(){
		return sp.getBoolean("sound", true);
	}
	//����֪ͨʱ�Ƿ���������ʾ
	public boolean isAllowVibrate(){
		return sp.getBoolean("vibrate", true);
	}
	//�����Ƿ����֪ͨ
	public void setAllowNotification(boolean flag){
		editor.putBoolean("notification", flag);
		editor.commit();
	}
	//�����Ƿ���������
	public void setAllowSound(boolean flag){
		editor.putBoolean("sound", flag);
		editor.commit();
	}
	
	//�����Ƿ�������
	public void setAllowVibrate(boolean flag){
		editor.putBoolean("vibrate", flag);
		editor.commit();
	}
	
	
}
