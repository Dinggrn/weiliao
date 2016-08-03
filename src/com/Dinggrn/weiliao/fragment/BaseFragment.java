package com.Dinggrn.weiliao.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.db.BmobDB;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.app.MyApp;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.ui.MainActivity;

public class BaseFragment extends Fragment{
	
	BmobUserManager bmobUserManager;
	BmobChatManager bmobChatManager;
	BmobDB bmobDB;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		bmobUserManager = BmobUserManager.getInstance(MyApp.context);
		bmobChatManager = BmobChatManager.getInstance(MyApp.context);
		bmobDB = BmobDB.create(MyApp.context);
	}
	
	public void toast(String text){
		//����������Fragment��onCreate�������
		//��ΪgetActivity�п�����onCreate�����л�õ�null
		MainActivity activity = (MainActivity) getActivity();
		activity.toast(text);
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
			Intent intent  = new Intent(getActivity(),clazz);
			startActivity(intent);
			if(isFinish){
				getActivity().finish();
			}
		}

		public void jump(Intent intent,boolean isFinish){
			startActivity(intent);
			if(isFinish){
				getActivity().finish();
			}
		}
		
		
		public void setHeaderTitle(View headerView,String text){
//			TextView tv = (TextView) headerView.findViewById(R.id.tv_headerview_title);
//			if(text==null){
//				tv.setText("");
//			}else{
//				tv.setText(text);
//			}
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
	
}
