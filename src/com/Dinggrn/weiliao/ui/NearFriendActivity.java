package com.Dinggrn.weiliao.ui;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.v3.listener.FindListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.NearFriendAdapter;
import com.Dinggrn.weiliao.app.MyApp;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class NearFriendActivity extends BaseActivity {
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.lv_near_friend_listview)
	PullToRefreshListView ptrListView;
	ListView listView;
	List<User> users;
	NearFriendAdapter adapter;

	private int page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near);
		ButterKnife.bind(this);
		initHeaderView();
		initListView();
	}

	private void initHeaderView() {
		setHeaderTitle(headerView, "��������");
		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void initListView() {
		ptrListView.setMode(Mode.PULL_FROM_END);
		listView = ptrListView.getRefreshableView();
		users = new ArrayList<User>();
		adapter = new NearFriendAdapter(this, users);
		listView.setAdapter(adapter);
		ptrListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				page += 1;
				//�ο�AddFriendActivity��д��
				bmobUserManager.queryKiloMetersListByPage(
						true, 
						page, 
						"location",//_user����������ʾ�û�λ�õ��ֶ����� 
						MyApp.lastPoint.getLongitude(), 
						MyApp.lastPoint.getLatitude(), 
						false,//����ʾ�Ѿ��Ǻ��ѵ���Щ�û�
						10.0, 
						null,//�Ƿ��ѯʱ����Ҫ���Ӳ�ѯ����������"gender"���������Ҫ�ʹ�null 
						null,//�����һ���������ṩ��ѯ������ֵ������false
						new FindListener<User>() {

							@Override
							public void onError(int arg0, String arg1) {
								ptrListView.onRefreshComplete();
								toastAndLog("��������æ���Ժ�����", arg0+": "+arg1);

							}

							@Override
							public void onSuccess(List<User> arg0) {
								ptrListView.onRefreshComplete();
								if(arg0.size()>0){
									adapter.addAll(arg0, false);
								}else{
									toast("����û�и��������");
									ptrListView.setMode(Mode.DISABLED);
								}

							}
						});

			}
		});

	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {

		page = 0;
		Log.d("TAG",MyApp.lastPoint.getLongitude()+" / "+ 
				MyApp.lastPoint.getLatitude());
		bmobUserManager.queryKiloMetersListByPage(
				false, 
				page, 
				"location",//_user����������ʾ�û�λ�õ��ֶ����� 
				MyApp.lastPoint.getLongitude(), 
				MyApp.lastPoint.getLatitude(), 
				false,//����ʾ�Ѿ��Ǻ��ѵ���Щ�û�
				10.0, 
				null,//�Ƿ��ѯʱ����Ҫ���Ӳ�ѯ����������"gender"���������Ҫ�ʹ�null 
				null,//�����һ���������ṩ��ѯ������ֵ������false
				new FindListener<User>() {

					@Override
					public void onError(int arg0, String arg1) {
						toastAndLog("��������æ���Ժ�����", arg0+": "+arg1);

					}

					@Override
					public void onSuccess(List<User> arg0) {
						if(arg0.size()>0){
							adapter.addAll(arg0, true);
						}else{
							toast("����һ���˶�û��Ŷ/(��o��)/~~");
						}

					}
				});

	}
}
