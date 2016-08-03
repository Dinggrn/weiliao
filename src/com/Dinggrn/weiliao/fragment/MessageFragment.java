package com.Dinggrn.weiliao.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobRecent;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.RecentAdapter;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.ui.ChatActivity;
import com.Dinggrn.weiliao.ui.MainActivity;
/**
 * ���������һ��ListView��ʾ
 * ��ǰ��¼�û������ú���֮���������������һ����������
 * ���һ���������ݵ�����������BmobRecent
 * ����ListView������Դ����List<BmobRecent>
 * 
 * BmobRecent��ص�һЩ������
 * 
 * bmobDB.hasUnReadMsg ��ǰ���ݿ��У��Ƿ���δ������Ϣ
 * 
 * bmobDB.getAllUnReadedCount ��ǰ���ݿ��У�����δ����Ϣ������
 * 
 * bmobDB.queryRecents ��õ�ǰ���ݿ���Recent���ݱ��е��������ݣ���Щ���ݱ���װΪList<BmobRecnt>����
 * 
 * bmobDB.getUnreadCount(uid) �����ĳһ���û�֮�������δ����Ϣ�������������abc�û�֮���δ����Ϣ������ô����uid����ľ���abc��objectId
 * 
 * bmobDB.deleteRecent(uid) ɾ����ĳһ����֮������һ��������Ϣ��
 *                          ���磬Ҫɾ����abc����û�֮������һ��������Ϣ
 *                          ��ô��������uid����abc����û���objecetId
 * bmobDB.deleteMessages(uid) ɾ����ĳһ����֮���ȫ��������Ϣ��
 *                           ���磬Ҫɾ����abc����û�֮�������������Ϣ
 *                           ��ô��������uid����abc����û���objectId
 * 
 *
 * BmobRecent�л��е�һЩ���ԣ�
 * private String targetid;//Ŀ���û�id����������abc����û�֮������죬��ôtargetID����abc����û���objectId
 * private String userName;//Ŀ���û�������������abc����û�֮������죬��ôuserName����abc����û���userName
 * private String nick;//Ŀ���û����ǳ�
 * private String avatar;//Ŀ���û���ͷ��������Ե�ֵ���Ǹ��û�ͷ����Bmob�������ϴ洢�ĵ�ַ
 * private String message;//�������������Ϣ������
 * private long time;//���������Ϣ������ʱ�䡣��Ҫע�⣬������ֵ��10Ϊlong���ͣ�ֻ��ȷ���롣�����������ڴ����ʱ����Ҫ�ڸ���ֵ������*1000��תΪ����ֵ
 * private int type;//���������Ϣ����Ϣ���͡�������ȡֵֻ���������֣�1 �ı� 2 ͼ�� 3 λ�� 4 ��������ͬ���͵�BmobRecent������message���Ե�����ֵ�᲻һ��
 * 
 * 
 * @author pjy
 *
 */

public class MessageFragment extends BaseFragment{
	
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.lv_message_messages)
	ListView listView;
	
	List<BmobRecent> recents;//����Դ
	RecentAdapter adapter;//������
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view  = inflater.inflate(R.layout.fragment_message, container,false);
		ButterKnife.bind(this,view);
		initHeaderView();
		initListView();
		return view;
	}
	
	private void initHeaderView() {
		setHeaderTitle(headerView, "΢��",Position.CENTER);
	}

	private void initListView() {
		recents = new ArrayList<BmobRecent>();
		adapter = new RecentAdapter(getActivity(), recents);
		listView.setAdapter(adapter);
		//TODO ΪlistView��ӵ����������¼�������
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				//1)���¸ûỰ������δ����Ϣ-->�Ѷ���Ϣ
					BmobRecent recent = adapter.getItem(position);
					bmobDB.resetUnread(recent.getTargetid());
				//2)����MainActivity��setbadgeViewCount�����������ܵ�δ����Ϣ��
					((MainActivity)getActivity()).setbadgeViewCount();
				//3)��ת��ChatActivity���棬��תʱҪ������ز���
				//  ֱ�ӽ�recent����Ӧ���Ǹ��û����ݵ�ChatActivity
					List<BmobChatUser> friends = bmobDB.getContactList();
					for(BmobChatUser bcu:friends){
						if(bcu.getUsername().equals(recent.getUserName())){
							Intent intent = new Intent(getActivity(),ChatActivity.class);
							intent.putExtra("user", bcu);
							jump(intent, false);
							break;
						}
					}
			}
		});
		
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setIcon(android.R.drawable.ic_menu_info_details);
				builder.setTitle("ɾ��");
				builder.setMessage("���Ƿ�Ҫɾ����"+adapter.getItem(position).getUserName()+"֮���������Ϣ��");
				builder.setPositiveButton("ɾ֮", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						BmobRecent recent = adapter.getItem(position);
						//1)�ӱ������ݿ��Recent����ɾ��
						bmobDB.deleteRecent(recent.getTargetid());
						//2)�ӱ������ݿ��Chat����ɾ��
						bmobDB.deleteMessages(recent.getTargetid());
						//3)��ListView������Դ��ɾ
						adapter.remove(recent);
						//4)����һ��MainActivity��badgeView�е�����
						((MainActivity)getActivity()).setbadgeViewCount();
					}
				});
				
				builder.setNegativeButton("������", null);
				
				builder.create().show();
				
				return true;
			}
		});
		
	}

	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	public void refresh() {
		//Ϊ����Դ�������������
		//bmobDB.queryRecents
		adapter.addAll(bmobDB.queryRecents(), true);
	}
}
