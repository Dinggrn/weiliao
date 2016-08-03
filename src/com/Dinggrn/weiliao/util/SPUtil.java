package com.Dinggrn.weiliao.util;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.Dinggrn.weiliao.app.MyApp;

/**
 * ��������ƫ�������ļ��Ĺ�����
 * @author pjy
 *
 */
public class SPUtil {
	
	private static Editor editor;
	private SharedPreferences sp;
	
	public SPUtil(String spName){
		sp = MyApp.context.getSharedPreferences(spName, Context.MODE_PRIVATE);
		editor = sp.edit();
	}
	/**
	 * �Ƿ����֪ͨ
	 * @return
	 */
	public boolean isAcceptNotify(){
		return sp.getBoolean("notify", true);
	}
	/**
	 * �Ƿ���������
	 * @return
	 */
	public boolean isAllowSound(){
		return sp.getBoolean("sound", true);
	}
	/**
	 * �Ƿ�������
	 * @return
	 */
	public boolean isAllowVibrate(){
		return sp.getBoolean("vibrate", true);
	}
	/**
	 * �����Ƿ��������֪ͨ
	 * @param flag
	 */
	public void setAcceptNotify(boolean flag){
		editor.putBoolean("notify", flag);
		editor.commit();
	}
	/**
	 * �����Ƿ���������
	 * @param flag
	 */
	public void setAllowSound(boolean flag){
		editor.putBoolean("sound", flag);
		editor.commit();
	}
	/**
	 * �����Ƿ�������
	 * @param flag
	 */
	public void setAllowVibrate(boolean flag){
		editor.putBoolean("vibrate", flag);
		editor.commit();
	}
}
