package com.Dinggrn.weiliao.fragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.FriendAdapter;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.ui.AddFriendActivity;
import com.Dinggrn.weiliao.ui.BaseActivity;
import com.Dinggrn.weiliao.ui.NearFriendActivity;
import com.Dinggrn.weiliao.ui.NewFriendActivity;
import com.Dinggrn.weiliao.ui.TestActivity;
import com.Dinggrn.weiliao.ui.UserInfoActivity;
import com.Dinggrn.weiliao.util.PinYinUtil;
import com.Dinggrn.weiliao.view.MyLetterView;
import com.Dinggrn.weiliao.view.MyLetterView.OnTouchLetterListener;


public class FriendFragment extends BaseFragment{
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.lv_friend_frineds)
	ListView listView;
	@Bind(R.id.tv_friend_letter)
	TextView tvLetter;//ListView�Ϸ����ǵĴ���ĸ
	@Bind(R.id.mlv_friend_letters)
	MyLetterView mlvLetters;//�����������Զ���View
	/*@Bind(R.id.menuview)
	View menuview;*/
	//��Ϊ��ListView����ʾfriends��ʱ��
	//��Ҫ��friends�е��û������û�����ƴ����ʽ��������
	//������User������������pyname�������
	//����ԴҪʹ��User��������
	List<User> friends;//����Դ
	FriendAdapter adapter;//������
	
	
	ImageView ivNewFriendTips;//ListView��headerView�е�С���
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view  = inflater.inflate(R.layout.fragment_friend, container,false);
		ButterKnife.bind(this, view);
		initHeaderView();
		initListView();
		//mlvLetters��Ӽ�����
		mlvLetters.setListener(new OnTouchLetterListener() {
			
			@Override
			public void onTouchLetter(String str) {
				//1)Ų��ListView
				listView.setSelection(adapter.getPositionForSection(str.charAt(0)));
				//2)��ʾ����ĸ
				tvLetter.setVisibility(View.VISIBLE);
				tvLetter.setText(str);
			}

			@Override
			public void onFinishTouch() {
				tvLetter.setVisibility(View.INVISIBLE);
			}
		});
		return view;
	}


	private void initListView() {
		friends = new ArrayList<User>();
		adapter = new FriendAdapter(getActivity(), friends);
		//ΪListView���һ��headerview
		View listHeader = getActivity().getLayoutInflater().inflate(R.layout.listview_header_friend_layout,listView,false);
		View llNewFriend = listHeader.findViewById(R.id.ll_listview_header_newfriend);
		View llNearFriend = listHeader.findViewById(R.id.ll_header_listview_near);
		llNewFriend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				jump(NewFriendActivity.class, false);
			}
		});
		llNearFriend.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				jump(TestActivity.class, false);
			}
		});
		
		listView.addHeaderView(listHeader);
		listView.setAdapter(adapter);
		ivNewFriendTips = (ImageView) listHeader.findViewById(R.id.iv_friend_newfriend_tips);
	
		//listView�����Ŀ������������ת��UserInfoActivity
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Intent intent = new Intent(getActivity(),UserInfoActivity.class);
				intent.putExtra("from", "friend");
				//�����㹹������Ĳ�ͬ����ͬ
				//����ΪListView�����һ��Header
				//������ȡ���ݵ�ʱ��position-1
				intent.putExtra("username", adapter.getItem(position-1).getUsername());
				jump(intent, false);
			}
		});
		//listView�����Ŀ����������������ʾ�Ի���ѯ���Ƿ�ɾ������
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					final int position, long id) {
				
				AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				builder.setIcon(android.R.drawable.ic_menu_info_details);
				builder.setTitle("֪ͨ");
				builder.setMessage("��ȷʵҪɾ��"+adapter.getItem(position-1).getUsername()+"��?");
				builder.setNegativeButton("���Ű�", null);
				builder.setPositiveButton("ɾ֮", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						bmobUserManager.deleteContact(adapter.getItem(position-1).getObjectId(), new UpdateListener() {
							
							@Override
							public void onSuccess() {
								
								log("ɾ���û���id------��"+adapter.getItem(position-1).getObjectId());
								
								//Ŀǰ�����ѽ����Ǵӷ������ͱ������ݿ���ɾ������
								//����ListView������������������Դ��
								//Ϊ�˸���ListView����ʾ�������������Դ
								adapter.remove(adapter.getItem(position-1));
								((BaseActivity)getActivity()).toast("ɾ���ɹ�");
							}
							
							@Override
							public void onFailure(int arg0, String arg1) {
								((BaseActivity)getActivity()).toastAndLog("ɾ��ʧ��", arg0+": "+arg1);
							}
						});
						
					}
				});
				
				builder.create().show();
				
				return true;
			}
		});
	}

	private void initHeaderView() {
		setHeaderTitle(headerView, "΢��", Position.CENTER);
//		menuview.setVisibility(View.INVISIBLE);
		//�����Ҳ�ͼ��
		setHeaderImage(headerView, R.drawable.add_normal, Position.RIGHT, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*if(menuview.getVisibility()==View.VISIBLE){
					menuview.setVisibility(View.INVISIBLE);
				}else{
					menuview.setVisibility(View.VISIBLE);
				}*/
				jump(AddFriendActivity.class, false);
			}
			
		});
	/*	//����Ŀ¼�ĵ���¼�
		if(menuview.getVisibility()==View.VISIBLE){
			menuview.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					switch(getId()){
					case R.id.togetherchat:
						// �����ת������Ӻ��ѡ�����AddFriendActivity
					case R.id.add_newfriend:
						jump(AddFriendActivity.class, false);
						break;
					case R.id.scan:
					case R.id.feedback:
						break;
					}
				}
			});
		}*/
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refresh();
		//����С������ʾ
		hasNewInvitation();
	}
	
	public void hasNewInvitation() {
		if(bmobDB.hasNewInvite()){
			ivNewFriendTips.setVisibility(View.VISIBLE);
		}else{
			ivNewFriendTips.setVisibility(View.INVISIBLE);
		}
	}


	/**
	 * ����ȥˢ�º�������
	 */
	private void refresh() {
		//��ǰ��¼�û��ĺ����б��ڷ���������һ��
		bmobUserManager.queryCurrentContactList(new FindListener<BmobChatUser>() {
			
			@Override
			public void onSuccess(List<BmobChatUser> list) {
				friends.clear();
				for(BmobChatUser bcu:list){
					User user = new User();
					user.setObjectId(bcu.getObjectId());
					user.setUsername(bcu.getUsername());
					user.setAvatar(bcu.getAvatar());
					user.setPyname(PinYinUtil.getPinYin(user.getUsername()));
					user.setSortLetter(user.getPyname().charAt(0));
					friends.add(user);
				}
				//��friends�е����ݸ���pyname��������
				Collections.sort(friends, new Comparator<User>() {

					@Override
					public int compare(User lhs, User rhs) {
						return lhs.getPyname().compareTo(rhs.getPyname());
					}
				});
				//������õ�������ListView����ʾ
				adapter.notifyDataSetChanged();
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});
		//�ڱ������ݿ���Ҳ��һ��
		//bmobDB.getContactList();
		//���������ַ�ʽ��ѯ����ѯ����������������BmobChatUser����
		//List<BmobChatUser> list = bmobDB.getContactList();
		//���ݷ��ص�list���Լ�����friends�е�����
		
	}
}
