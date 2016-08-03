package com.Dinggrn.weiliao.receiver;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.im.util.BmobNetUtil;
import cn.bmob.push.PushConstants;
import cn.bmob.v3.listener.FindListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.ui.MainActivity;
import com.Dinggrn.weiliao.ui.SplashActivity;
import com.Dinggrn.weiliao.util.SPUtil;


public class MyReceiver extends BroadcastReceiver{
	//"������"����
	private static List<EventListener> list = new ArrayList<EventListener>();

	Context context;
	//BmobIMSDK�ṩ�ķ���֪ͨ�ġ������ࡱ
	BmobNotifyManager bmobNotifyManager;
	//���ƫ�������ļ��е�����
	SPUtil sp;

	String objectId;
	BmobUserManager bmobUserManager;
	BmobChatManager bmobChatManager;
	BmobDB bmobDB;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		bmobUserManager = BmobUserManager.getInstance(context);
		bmobChatManager = BmobChatManager.getInstance(context);
		objectId = bmobUserManager.getCurrentUserObjectId();
		bmobNotifyManager = BmobNotifyManager.getInstance(context);
		bmobDB = BmobDB.create(context);
		sp = new SPUtil(objectId);
		String action = intent.getAction();
		//"cn.bmob.push.action.MESSAGE"
		if(PushConstants.ACTION_MESSAGE.equals(action)){
			String json = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);

			if(!BmobNetUtil.isNetworkAvailable(context)){
				if(list.size()>0){
					for (EventListener listener : list) {
						listener.onNetChange(false);
					}
				}
			}else{
				//����json�ַ��������ݲ�ͬ���͵���Ϣ��������
				parse(json);
			}
		}

	}

	private void parse(final String json) {
		try{

			JSONObject jsonObject = new JSONObject(json);
			//����tag��ֵ����5����Ϣ����
			//String tag = jsonObject.getString("tag");
			String tag = BmobJsonUtil.getString(jsonObject, "tag");
			//offline������Ϣtag = "offline"
			if("offline".equals(tag)){
				if(list.size()>0){
					//����ж�����
					for(EventListener listener:list){
						listener.onOffline();
					}
				}else{
					//û�ж����ߣ��ͷ��� һ��֪ͨ
					bmobNotifyManager.showNotify(
							sp.isAllowSound(), 
							sp.isAllowVibrate(), 
							R.drawable.ic_notification, 
							"�˺����������豸��¼", 
							"�˺��쳣", 
							"�����˺����������豸��¼", 
							SplashActivity.class);
					//�õ�ǰ��¼�û��ǳ�
					bmobUserManager.logout();
				}
			}
			//add������Ϣ tag="add"
			if("add".equals(tag)){
				//ȷ���£�������Ϣ�ǲ��Ƿ�����ǰ��¼�û���
				//���������͹�����json�ַ����У�������˵�id
				String tId = BmobJsonUtil.getString(jsonObject, "tId");
				if(objectId.equals(tId)){
					//��ȡJson�ַ�����Ϣ������һ��BmobInvitation����
					BmobInvitation message = bmobChatManager.saveReceiveInvite(json, tId);
					if(list.size()>0){
						for(EventListener listener:list){
							listener.onAddUser(message);
						}
					}else{
						if(sp.isAcceptNotify()){
							bmobNotifyManager.showNotify(
									sp.isAllowSound(), 
									sp.isAllowVibrate(), 
									R.drawable.ic_notification, 
									message.getFromname()+"���������Ϊ����", 
									"��Ӻ���", 
									message.getFromname()+"���������Ϊ����",  
									MainActivity.class);
						}
					}
				}
			}
			//agree������Ϣ tag="agree"
			if("agree".equals(tag)){
				//����ͬ��������Ӻ��ѵ�����
				//�жϣ�1������ͬ����Ϣ�ǲ��Ƿ����ҵģ�2���Է��ǲ����Ѿ����ҵĺ�����
				String tId = BmobJsonUtil.getString(jsonObject, "tId");
				String fId = BmobJsonUtil.getString(jsonObject, "fId");
				if(objectId.equals(tId) && !isFriend(fId)){
					//�ѱ������Ϊ�ҵĺ���
					final String targetName = BmobJsonUtil.getString(jsonObject, "fu");
					//��������Ϻͱ������ݿ���ͬʱ�������
					bmobUserManager.addContactAfterAgree(targetName, new FindListener<BmobChatUser>() {
						
						@Override
						public void onSuccess(List<BmobChatUser> arg0) {
							//1)��һ��֪ͨ"XXXͬ�������Ϊ������"
							if(sp.isAcceptNotify()){
								bmobNotifyManager.showNotify(
										sp.isAllowSound(), 
										sp.isAllowVibrate(), 
										R.drawable.ic_notification, 
										targetName+"ͬ�������ĺ�������", 
										"�������", 
										targetName+"ͬ�������ĺ�������", 
										MainActivity.class);
							}
							//2)����һ������Է�֮���������Ϣ"��ͨ������ĺ�����֤�������ǿ��Կ�ʼ������!"
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
						}
						
						@Override
						public void onError(int arg0, String arg1) {
							//TODO
						}
					});
				}
			}
			//readed������Ϣtag = "readed"
			if("readed".equals(tag)){
				String tId = BmobJsonUtil.getString(jsonObject, "tId");
				if(objectId.equals(tId)){
					//����֮ǰ���͵�BmobMsg��״̬����һ��
					//����Ϣ��״̬��"������"��Ϊ���Է����յ���
					String conversionId = BmobJsonUtil.getString(jsonObject,"mId");
					String msgTime = BmobJsonUtil.getString(jsonObject, "ft");
					bmobChatManager.updateMsgStatus(conversionId, msgTime);
					if(list.size()>0){
						for(EventListener listener:list){
							listener.onReaded(conversionId, msgTime);
						}
					}
				}
			}
			
			//����������Ϣtag=""
			if(TextUtils.isEmpty(tag)){
				String tId = BmobJsonUtil.getString(jsonObject, "tId");
				if(objectId.equals(tId)){
					
					bmobChatManager.createReceiveMsg(json, new OnReceiveListener() {
						
						@Override
						public void onSuccess(BmobMsg msg) {
							if(list.size()>0){
								for(EventListener listener:list){
									listener.onMessage(msg);
								}
							}else{
								if(sp.isAcceptNotify()){
									//tickerText������������͵Ĳ�ͬ���в�ͬ����ʾ����
									String tickerText = "";
									//�������ͷ�Ϊ���֣�1 �ı� 2ͼ�� 3λ�� 4����
									int type = msg.getMsgType();
									switch (type) {
									case 1:
										//���ܻ��޸�
										tickerText = msg.getContent();
										break;

									case 2:
										tickerText = "[ͼƬ]";
										break;
									case 3:
										//TODO ���ܻ��޸�
										tickerText = "[λ��]";
										break;
									case 4:
										tickerText = "[����]";
										break;
									}
									bmobNotifyManager.showNotify(
											sp.isAllowSound(), 
											sp.isAllowVibrate(), 
											R.drawable.ic_notification, 
											tickerText,
											msg.getBelongUsername(),
											tickerText,
											MainActivity.class);
								}
							}
							
						}
						
						@Override
						public void onFailure(int code, String arg1) {
							// TODO Auto-generated method stub
							
						}
					});
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * �ж�fId������������Ƿ��Ѿ����ҵĺ���
	 * @param fId
	 * @return false ���Ǻ��� 
	 *         true �Ǻ���
	 */
	private boolean isFriend(String fId) {

		List<BmobChatUser> contacts = bmobDB.getAllContactList();

		for(BmobChatUser b:contacts){
			if(b.getObjectId().equals(fId)){
				return true;
			}
		}
		return false;
	}

	/**
	 * ע���Ϊ������
	 * @param listener
	 */
	public static void regist(EventListener listener){
		list.add(listener);
	}
	/**
	 * ע��������
	 * @param listener
	 */
	public static void unregist(EventListener listener){
		list.remove(listener);
	}
}
