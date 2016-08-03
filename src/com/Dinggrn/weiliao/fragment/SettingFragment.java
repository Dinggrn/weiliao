package com.Dinggrn.weiliao.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.ui.LoginActivity;
import com.Dinggrn.weiliao.ui.SplashActivity;
import com.Dinggrn.weiliao.ui.UserInfoActivity;
import com.Dinggrn.weiliao.util.SPUtil;


public class SettingFragment extends BaseFragment{
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.tv_setting_username)
	TextView tvUsername;
	
	@Bind(R.id.iv_setting_notificationswitch)
	ImageView ivNotification;
	@Bind(R.id.iv_setting_soundswitch)
	ImageView ivSound;
	@Bind(R.id.iv_setting_vibrateswitch)
	ImageView ivVibrate;
	
	SPUtil sp;//������ǰ��¼�û���Ӧ��ƫ�������ļ�
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view  = inflater.inflate(R.layout.fragment_setting, container,false);
		ButterKnife.bind(this,view);
		initHeaderView();
		initView();
		return view;
	}

	private void initView() {
		tvUsername.setText(bmobUserManager.getCurrentUserName());
		sp = new SPUtil(bmobUserManager.getCurrentUserObjectId());
		if(sp.isAcceptNotify()){
			ivNotification.setImageResource(R.drawable.ic_switch_on);
		}else{
			ivNotification.setImageResource(R.drawable.ic_switch_off);
		}
		if(sp.isAllowSound()){
			ivSound.setImageResource(R.drawable.ic_switch_on);
		}else{
			ivSound.setImageResource(R.drawable.ic_switch_off);
		}
		if(sp.isAllowVibrate()){
			ivVibrate.setImageResource(R.drawable.ic_switch_on);
		}else{
			ivVibrate.setImageResource(R.drawable.ic_switch_off);
		}
	}

	private void initHeaderView() {
		setHeaderTitle(headerView, "����", Position.CENTER);
	}
	
	@OnClick(R.id.btn_setting_logout)
	public void logout(View v){
		//�ǳ���û�е�ǰ��¼�û��ˣ�
		bmobUserManager.logout();
		//��ת����
		jump(LoginActivity.class, true);
	}
	
	@OnClick(R.id.iv_setting_infoedit)
	public void browseUserInfo(View v){
		Intent intent = new Intent(getActivity(),UserInfoActivity.class);
		intent.putExtra("from", "me");//��ת��UserInfoActivityʱ����ʾ�༭���������Ϣ�Ľ���
		jump(intent, false);
	}
	
	@OnClick(R.id.iv_setting_blackedit)
	public void toBlackActivity(View v){
		//TODO ��ʱ�Ȳ�д�ý���
	}
	
	@OnClick(R.id.iv_setting_notificationswitch)
	public void switchNotification(View v){
		if(sp.isAcceptNotify()){
			closeNotification();
		}else{
			openNotification();
		}
	}

	private void openNotification() {
		//��֪ͨ��ť
		ivNotification.setImageResource(R.drawable.ic_switch_on);
		//�޸�ƫ�������ļ�
		sp.setAcceptNotify(true);
		//�����û���������������𶯰�ť���޸�״̬
		ivSound.setClickable(true);
		ivVibrate.setClickable(true);
	}

	private void closeNotification() {
		//�ر�֪ͨ��ť����Ҫ�ر�����,��
		ivNotification.setImageResource(R.drawable.ic_switch_off);
		ivSound.setImageResource(R.drawable.ic_switch_off);
		ivVibrate.setImageResource(R.drawable.ic_switch_off);
		//�޸�ƫ�������ļ�
		sp.setAcceptNotify(false);
		sp.setAllowSound(false);
		sp.setAllowVibrate(false);
		//����������������,��
		ivSound.setClickable(false);
		ivVibrate.setClickable(false);
	}
	
	@OnClick(R.id.iv_setting_soundswitch)
	public void switchSound(View v){
		if(sp.isAllowSound()){
			ivSound.setImageResource(R.drawable.ic_switch_off);
			sp.setAllowSound(false);
		}else{
			ivSound.setImageResource(R.drawable.ic_switch_on);
			sp.setAllowSound(true);
		}
	}
	
	@OnClick(R.id.iv_setting_vibrateswitch)
	public void switchVibrate(View v){
		if(sp.isAllowVibrate()){
			ivVibrate.setImageResource(R.drawable.ic_switch_off);
			sp.setAllowVibrate(false);
		}else{
			ivVibrate.setImageResource(R.drawable.ic_switch_on);
			sp.setAllowVibrate(true);
		}
	}
}
