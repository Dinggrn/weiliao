package com.Dinggrn.weiliao.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.bean.BmobInvitation;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.InvitationAdapter;
import com.Dinggrn.weiliao.constant.Constant.Position;


public class NewFriendActivity extends BaseActivity {
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.lv_newfriend_invitations)
	ListView listView;
	
	//����Դ
	List<BmobInvitation> invitations;
	//������
	InvitationAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_friend);
		ButterKnife.bind(this);
		initHeaderView();
		initListView();
	}

	private void initHeaderView() {
		setHeaderTitle(headerView, "��������",Position.CENTER);
		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}

	private void initListView() {
		invitations = new ArrayList<BmobInvitation>();
		adapter = new InvitationAdapter(this, invitations);
		listView.setAdapter(adapter);
		//TODO ��ӳ���ɾ��
		//listView.setOnItemLongClickListener(listener);
		//1)�����Ի�����ʾ�û���ȷʵҪɾ��XXX�ĺ��������𣿡�
		//2)�����ݿ���ɾ�� bmobDB.deleteInviteMsg(invitation.getFromId(), invitation.getTime()+"");
		//ɾ����ʱ����ֻɾ���û��������һ�������ǰ�������ص����붼ɾ������
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		//2��test,2������
		List<BmobInvitation> list = bmobDB.queryBmobInviteList();
		HashMap<String,BmobInvitation> map = new HashMap<String,BmobInvitation>();
		for(BmobInvitation bi:list){
			map.put(bi.getFromid(), bi);
		}
		list.clear();
		for(Entry<String,BmobInvitation> entry:map.entrySet() ){
			list.add(entry.getValue());
		}
		//����list��ֻ������Ԫ��1��test��1��laowang
		adapter.addAll(list,true);
	}


}
