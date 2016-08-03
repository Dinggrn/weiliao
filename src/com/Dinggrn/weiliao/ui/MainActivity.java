package com.Dinggrn.weiliao.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.ImageView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.inteface.EventListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.MyPagerAdapter;
import com.Dinggrn.weiliao.app.MyApp;
import com.Dinggrn.weiliao.fragment.FriendFragment;
import com.Dinggrn.weiliao.fragment.MessageFragment;
import com.Dinggrn.weiliao.receiver.MyReceiver;
import com.Dinggrn.weiliao.util.SPUtil;
import com.Dinggrn.weiliao.view.BadgeView;
import com.Dinggrn.weiliao.view.MyTabIcon;


public class MainActivity extends BaseActivity implements EventListener{
	@Bind(R.id.vp_main_viewpager)
	ViewPager viewPager;
	MyPagerAdapter adapter;

	@Bind(R.id.myicon_main_message)
	MyTabIcon mtiMessage;
	@Bind(R.id.myicon_main_friend)
	MyTabIcon mtiFriend;
	@Bind(R.id.myicon_main_find)
	MyTabIcon mtiFind;
	@Bind(R.id.myicon_main_setting)
	MyTabIcon mtiSetting;

	MyTabIcon[] icons = new MyTabIcon[4];
	
	
	@Bind(R.id.iv_main_newfriend_tips)
	ImageView ivTips;

	SPUtil sp;
	
	@Bind(R.id.bv_main_messageunread)
	BadgeView badgeView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Log.d("TAG","���ȣ�"+MyApp.lastPoint.getLongitude()+" ,γ�ȣ�"+MyApp.lastPoint.getLatitude());
		ButterKnife.bind(this);
		icons[0] = mtiMessage;
		icons[1] = mtiFriend;
		icons[2] = mtiFind;
		icons[3] = mtiSetting;
		sp = new SPUtil(bmobUserManager.getCurrentUserObjectId());
		initViewPager();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//MainActivity��ΪMyReceiver�����߶����е�һԱ
		MyReceiver.regist(this);
		
		//�����δ�������Ӻ���������ô�ͻ���ʾivTips
		if(bmobDB.hasNewInvite()){
			ivTips.setVisibility(View.VISIBLE);
		}else{
			ivTips.setVisibility(View.INVISIBLE);
		}
		
		//�����δ������Ϣ����ô�ͻ���ʾbadegeView
		if(bmobDB.hasUnReadMsg()){
			//1����badgeView�ɼ���
			badgeView.setVisibility(View.VISIBLE);
			//2) bmobDB.getAllUnreadedCount�����������δ����Ϣ�����������õ�badgeView��
			//   ����BadgeView�����ķ�����setBadgeCount(int)����
			setbadgeViewCount();
		}else{
			//
			badgeView.setVisibility(View.INVISIBLE);
		}
	}
	
	/**
	 * ����badgeView��Ҫ��ʾ������
	 */
	public void setbadgeViewCount() {
		//��ñ������ݿ���Recent���ݱ����ܵ�δ����Ϣ����
		int count = bmobDB.getAllUnReadCount();
		if(count > 0){
			badgeView.setVisibility(View.VISIBLE);
			badgeView.setBadgeCount(count);
		}else{
			badgeView.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		//MainActivity������MyReceiver�����߶����е�һԱ
		MyReceiver.unregist(this);
	}
	
	private void initViewPager() {
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPager.setAdapter(adapter);
		//��ʼ����ʱ��Ӧ���õ�һ��MyTabIcon��ʾ��ɫ
		//����Ķ��ǻ�ɫ
		for(int i=0;i<icons.length;i++){
			icons[i].setMyTabIconAlpha(0);
		}
		icons[0].setMyTabIconAlpha(255);
		//ΪViewPager��ӻ���������
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				//��ҳ��ѡ����Ӧ���õ�ǰҳ���Ӧ�İ�ť����ɫ��
				//���඼�ǻ�ɫ��
				for(int i=0;i<icons.length;i++){
					icons[i].setMyTabIconAlpha(0);
				}
				icons[arg0].setMyTabIconAlpha(255);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// ��ViewPager�Ļ��������У��÷����ᱻ���ϵĵ���
				//�����ĺ��壺arg0 ��ǰ��ҳ��;arg1 �����İٷֱ� 0--->1;arg2���ݻ����ٷֱȼ�������Ļ�������
				
				//log(arg0+" / "+arg1+" / "+arg2);
				
				if(arg0<adapter.getCount()-1){
					icons[arg0].setMyTabIconAlpha((int)((1-arg1)*255));
					icons[arg0+1].setMyTabIconAlpha((int)(arg1*255));
				}
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}
	
	@OnClick({R.id.myicon_main_message,R.id.myicon_main_friend,R.id.myicon_main_find,R.id.myicon_main_setting})
	public void setCurrentFragment(View v){
		switch (v.getId()) {
		case R.id.myicon_main_message:
			viewPager.setCurrentItem(0,false);
			break;
		case R.id.myicon_main_friend:
			viewPager.setCurrentItem(1,false);
			break;
		case R.id.myicon_main_find:
			viewPager.setCurrentItem(2,false);
			break;
		case R.id.myicon_main_setting:
			viewPager.setCurrentItem(3,false);
			break;
		}
	}


	@Override
	public void onMessage(BmobMsg message) {
		//MyReceiver�յ��˷��������͹����������͵���Ϣ
		//1)��mtiMessage��ť�����Ϸ���ʾһ��δ����Ϣ������
		//  BadgeView(ʵ�ʾ���һ��TextView)
		setbadgeViewCount();
		//2)����MessageFragment����ʾ������
		//  ����MessageFragment��refresh����
		MessageFragment fragment = (MessageFragment) adapter.getItem(0);
		fragment.refresh();
	}


	@Override
	public void onReaded(String conversionId, String msgTime) {
		//MainActivityû�з���������Ϣ�Ľ���
		//���Ը÷�����MainActivityû��
	}


	@Override
	public void onNetChange(boolean isNetConnected) {
		//��������ʱ����
		//��MainActivity�������û��ɽ�����ʱ��
		//MainActivity��MyReceiver�����߶����е�һԱ
		//MyReceiver��onReceive�������ý������������͹���������֮ǰ
		//MyReceiver���ж�һ�������״��
		//���û�����磬�����ö����߶����ж����ߵ�onNetChange����
		//��ʱ��MyReceiver�����߶��У�����һ�������߾���MainActivity
		//���ԣ�MainActivity��onNetChange�����ᱻ����
		toast("��ǰ���粻����");
	}


	@Override
	public void onAddUser(BmobInvitation message) {
		//�����Ϊ����
		//1)��mtiFriend�����Ͻ���ʾһ�����
		ivTips.setVisibility(View.VISIBLE);
		//�������������ʾ��ʹ��MyApp�е�mediaPlayer����һ����ʾ��
		if(sp.isAllowSound()){
			MyApp.mediaPlayer.start();
		}
		//2)��FriendFragment�ġ��µĺ��ѡ�ͼ�����Ͻ�Ҳ��ʾһ�����
		FriendFragment fragment = (FriendFragment) adapter.getItem(1);
		fragment.hasNewInvitation();
	}


	@Override
	public void onOffline() {
		// MainActivity���ڿɽ���״̬
		// ��MyReceiver���յ���һ����offline�����͵���Ϣ
		// �ͻ���ø÷���
		//�÷�������һ���Ի�����û���ʾ�����û�����
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(android.R.drawable.ic_menu_info_details);
		builder.setTitle("֪ͨ");
		builder.setMessage("�����˺�������һ̨�豸��¼����ȷ���Ƿ��Ǳ��˲�����");
		builder.setCancelable(false);
		builder.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				bmobUserManager.logout();
				jump(SplashActivity.class,true);
			}
		});
		//��ʾ�Ի���
		builder.create().show();
		
		
	}
}
