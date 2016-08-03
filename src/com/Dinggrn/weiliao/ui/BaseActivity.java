package com.Dinggrn.weiliao.ui;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.app.MyApp;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;


public class BaseActivity extends FragmentActivity{
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int CENTER = 2;

	BmobUserManager bmobUserManager;
	BmobChatManager bmobChatManager;
	BmobDB bmobDB;

	Toast toast;

	

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		toast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		bmobUserManager = BmobUserManager.getInstance(this);
		bmobChatManager = BmobChatManager.getInstance(this);
		bmobDB = BmobDB.create(this);
	}

	//Toast��Log�����
	public void toast(String text){
		toast.setText(text);
		toast.show();
	}

	public void log(String log){
		Log.d("TAG", this.getClass().getName()+"�����"+log);
	}

	public void toastAndLog(String text,String log){
		toast(text);
		log(log);
	}


	//�������ת
	public void jump(Class<?> clazz,boolean isFinish){
		Intent intent  = new Intent(this,clazz);
		startActivity(intent);
		if(isFinish){
			this.finish();
		}
	}

	public void jump(Intent intent,boolean isFinish){
		startActivity(intent);
		if(isFinish){
			this.finish();
		}
	}

	//����ͷ������
	//����ͷ�����⣬���������ʾ
	public void setHeaderTitle(View headerView,String text){
//		TextView tv = (TextView) headerView.findViewById(R.id.tv_headerview_title);
//		if(text==null){
//			tv.setText("");
//		}else{
//			tv.setText(text);
//		}
		setHeaderTitle(headerView, text, Position.CENTER);
	}
	//����ͷ����������ط�����������ȷָ�������λ��
	public void setHeaderTitle(View headerView,String text,Position pos){
		TextView tv = (TextView) headerView.findViewById(R.id.tv_headerview_title);
		//		switch (pos) {
		//		case 0:
		//			tv.setGravity(LEFT);
		//			break;
		//
		//		default:
		//			break;
		//		}

		switch (pos) {
		case LEFT:
			tv.setGravity(Gravity.LEFT);
			break;
		case RIGHT:
			tv.setGravity(Gravity.RIGHT);
			break;
		case CENTER:
			tv.setGravity(Gravity.CENTER);
			break;
		}
		
		if(text==null){
			tv.setText("");
		}else{
			tv.setText(text);
		}
	}
	/**
	 * ��������ͷ�������Ҳ��ͼ��
	 * @param headerView ͷ��headerview
	 * @param resId ��ʾͼ�����Դid
	 * @param pos pos�����LEFT����������ͼ�������CENTER��RIGHT��Ϊ�����Ҳ�ͼ��
	 */
	public void setHeaderImage(View headerView,int resId,Position pos,OnClickListener listener){
		ImageView iv = null;
		switch (pos) {
		
		case LEFT:
			iv = (ImageView) headerView.findViewById(R.id.iv_haderview_left);
			break;
		
		case CENTER:
		case RIGHT:
			iv = (ImageView) headerView.findViewById(R.id.iv_haderview_right);
			break;
		}
		iv.setImageResource(resId);
		iv.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
		
		if(listener!=null){
			iv.setOnClickListener(listener);
		}
	}
	
	/**
	 * �п�
	 * ����true����˵����EditTextδ��������
	 * ����false����˵��EditText��������
	 */
	public boolean isEmpty(EditText... ets){
		
		for (EditText editText : ets) {
			if(TextUtils.isEmpty(editText.getText().toString())){
				//���ֱ��ΪsetError�����ṩStirng���Ͳ���
				//�п��ܳ��ֵ���ʾ����ʹ�õ���ɫ���������ʾ��ı���ɫ�ظ�
				//�����ʾ���ֲ��ɼ�
				//���ǣ�setError���ܵĲ���������CharSequence����
				//���ԣ�����һ�²��������ͣ������ñ�׼��Stirng������ʹ�ð�׿�ṩ��
				//����չString��Ϊ����չStirngָ��һ����ͬ����ʾ�򱳾�����ɫ
				//<font color="��ɫֵ">
				editText.setError(Html.fromHtml("<font color=#ff0000>����������</font>"));
				return true;
			}
		}
		
		return false;
	}
	/**
	 * ����ָ���û���λ����Ϣ
	 * @param user
	 */
	public void updateUserLocation(User user) {
		//����һ���û���λ�ã���λ�����ó�MyApp.lastPoint
		//����˵�ǰ�ĵ�¼�û�
		//User user = bmobUserManager.getCurrentUser(User.class);
		//��������û�point���Ե�ֵ
		User user2 = new User();
		user2.setPoint(MyApp.lastPoint);
		user2.update(this, user.getObjectId(), new UpdateListener() {
			
			@Override
			public void onSuccess() {
				toast("λ�ø��³ɹ�");//???
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				toastAndLog("λ�ø���ʧ��", arg0+": "+arg1);
			}
		});
	}
}
