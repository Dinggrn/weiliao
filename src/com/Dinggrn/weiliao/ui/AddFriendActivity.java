package com.Dinggrn.weiliao.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.AddFriendAdapter;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public class AddFriendActivity extends BaseActivity {
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.et_addfriend_username)
	EditText etUsername;
	@Bind(R.id.lv_addfriend_users)
	PullToRefreshListView ptrListView;

	ListView listView;

	//���ʹ��CircularProgressButton��Ҫ����2������

	//���ʹ����PrgressBar����Ҫ����һ������

	ProgressDialog pd;//���ع����У����û�һ����ʾ

	//����Դ
	List<BmobChatUser> users;
	//������ AddFriendAdapter
	AddFriendAdapter adapter;
	int page;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_friend);
		ButterKnife.bind(this);
		initHeaderView();
		initListView();
	}

	private void initHeaderView() {

		setHeaderTitle(headerView, "��Ӻ���", Position.CENTER);

		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

	}

	private void initListView() {
		listView = ptrListView.getRefreshableView();
		users = new ArrayList<BmobChatUser>();
		adapter= new AddFriendAdapter(this, users);
		listView.setAdapter(adapter);
		
		//ΪptrListView����һ�����Ƽ�����
		ptrListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				
				page += 1;
				
				bmobUserManager.queryUserByPage(
						true, 
						page, 
						etUsername.getText().toString(), 
						new FindListener<BmobChatUser>() {

							@Override
							public void onError(int arg0, String arg1) {
								ptrListView.onRefreshComplete();
								
							}

							@Override
							public void onSuccess(List<BmobChatUser> arg0) {
								ptrListView.onRefreshComplete();
								if(arg0.size()>0){
									adapter.addAll(arg0, false);
								}else{
									//��һҳ��ѯ�Ѿ�û�и����������
									//Ҳ��û�б�Ҫ�ټ������Ƽ��ظ�����
									//��ֹ����
									ptrListView.setMode(Mode.DISABLED);
									toast("û�ж���û���...");
								}
								
							}
						});
				
			}
		});
		
		//ΪlistView���һ����Ŀ�����������������ת��UserInfoActivity
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(AddFriendActivity.this,UserInfoActivity.class);
				intent.putExtra("from", "stranger");
				intent.putExtra("username",adapter.getItem(position-1).getUsername());
				jump(intent, false);
			}
		});
		
	}

	/**
	 * �û�����ȷ����
	 * @param v
	 */
	@OnClick(R.id.btn_addfriend_search)
	public void search(View v){
		//1)BmobUserManager�ķ���queryUserByName(name,������<BmobChatUser>);
		//2)BmobUserManager�ķ���queryUser(name,������<T>);
		if(isEmpty(etUsername)){
			return;
		}else{
			//���û�һ����ʾ�����ڹ�����
			if(pd==null){
				pd = ProgressDialog.show(AddFriendActivity.this, "", "���������У����Ժ�...");
			}
			pd.show();
			
			ptrListView.setMode(Mode.DISABLED);
			
			bmobUserManager.queryUserByName(etUsername.getText().toString(), new FindListener<BmobChatUser>() {

				@Override
				public void onSuccess(List<BmobChatUser> arg0) {
					pd.dismiss();
					if(arg0.size()>0){
						adapter.addAll(arg0, true);
					}else{
						toast("���޴���");
					}
				}

				@Override
				public void onError(int arg0, String arg1) {
					pd.dismiss();
					toastAndLog("��������æ�Ժ�����", arg0+": "+arg1);
				}
			});
		}
	}

	/**
	 * �û�����ģ������
	 * @param v
	 */
	@OnClick(R.id.btn_addfriend_searchmore)
	public void searchMore(View v){
		//�п�
		if(isEmpty(etUsername)){
			return;
		}
		//��ʾ��ʾ��
		if(pd==null){
			pd = ProgressDialog.show(this, "", "���������У����Ժ�...");
		}
		pd.show();
		//����ҳ��ӵ�һҳ��ʼ��ѯ
		page = 0;
		ptrListView.setMode(Mode.PULL_FROM_END);
		//���������ѯ
		bmobUserManager.queryUserByPage(
				false, 
				page, 
				etUsername.getText().toString(), 
				new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, String arg1) {
						pd.dismiss();
						toastAndLog("��ѯʧ�ܣ��Ժ�����", arg0+": "+arg1);

					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						pd.dismiss();
						if(arg0.size()>0){
							adapter.addAll(arg0, true);
						}else{
							toast("���޴���");
						}

					}
				});
	}

}
