package com.Dinggrn.weiliao.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.ui.LocationActivity;
import com.Dinggrn.weiliao.util.ChatUtil;
import com.Dinggrn.weiliao.util.TimeUtil;
import com.nostra13.universalimageloader.core.ImageLoader;


public class ChatAdapter extends MyBaseAdapter<BmobMsg>{

	private static final int SEND_TEXTMESSAGE = 0;
	private static final int RECEIVE_TEXTMESSAGE = 1;
	private static final int SEND_IMAGEMESSAGE = 2;
	private static final int RECEIVE_IMAGEMESSAGE = 3;
	private static final int SEND_LOCATIONMESSAGE = 4;
	private static final int RECEIVE_LOCATIONMESSAGE = 5;
	private static final int SEND_VOICEMESSAGE = 6;
	private static final int RECEIVE_VOICEMESSAGE = 7;
	String myid;//�ǵ�ǰ��¼�û���objectId

	public ChatAdapter(Context context, List<BmobMsg> datasource) {
		super(context, datasource);
		myid = BmobUserManager.getInstance(context).getCurrentUserObjectId();
	}

	@Override
	public View getItemView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder vh;
		BmobMsg msg = getItem(position);
		int messageType = msg.getMsgType();
		switch (messageType) {
		case 1:
			if(convertView==null){
				int type = getItemViewType(position);
				if(type == SEND_TEXTMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_send_textmessage, parent,false);
				}
				if(type == RECEIVE_TEXTMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_receive_textmessage, parent,false);
				}
				vh = new ViewHolder();
				vh.tvChatTime = (TextView) convertView.findViewById(R.id.tv_chat_item_time);
				vh.tvContent = (TextView) convertView.findViewById(R.id.tv_chat_item_content);
				vh.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_chat_item_avatar);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}

			break;
		case 2:
			if(convertView==null){
				int type = getItemViewType(position);
				if(type == SEND_IMAGEMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_send_imagemessage, parent,false);
				}
				if(type == RECEIVE_IMAGEMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_receive_imagemessage, parent,false);
				}
				vh = new ViewHolder();
				vh.tvChatTime = (TextView) convertView.findViewById(R.id.tv_chat_item_time);
				vh.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_chat_item_avatar);
				vh.ivImgContent = (ImageView) convertView.findViewById(R.id.iv_chat_item_content);
				//������ͼ����Ϣ��ʱ��convertviewʹ����಼��ʱ��vh.pbSending��ֵ��null
				vh.pbSending = (ProgressBar) convertView.findViewById(R.id.pb_chat_item_sending);

				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}

			break;
		case 3:
			
			if(convertView==null){
				int type = getItemViewType(position);
				if(type == SEND_LOCATIONMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_send_locationmessage, parent,false);
				}
				if(type == RECEIVE_LOCATIONMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_receive_locationmessage, parent,false);
				}
				vh = new ViewHolder();
				vh.tvChatTime = (TextView) convertView.findViewById(R.id.tv_chat_item_time);
				vh.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_chat_item_avatar);
				vh.ivImgContent = (ImageView) convertView.findViewById(R.id.iv_chat_item_content);
				vh.tvAddress = (TextView) convertView.findViewById(R.id.tv_chat_item_address);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			break;
		
		case 4:
			if(convertView==null){
				int type = getItemViewType(position);
				if(type == SEND_VOICEMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_send_voicemessage, parent,false);
				}
				if(type == RECEIVE_VOICEMESSAGE){
					convertView = inflater.inflate(R.layout.item_chat_receive_voicemessage, parent,false);
				}
				vh = new ViewHolder();
				vh.tvChatTime = (TextView) convertView.findViewById(R.id.tv_chat_item_time);
				vh.ivAvatar = (ImageView) convertView.findViewById(R.id.iv_chat_item_avatar);
				vh.ivImgContent = (ImageView) convertView.findViewById(R.id.iv_chat_item_content);
				//������������Ϣ��ʱ��convertviewʹ����಼��ʱ��vh.pbSending��ֵ��null
				vh.pbSending = (ProgressBar) convertView.findViewById(R.id.pb_chat_item_sending);
				vh.tvVoiceLength = (TextView) convertView.findViewById(R.id.tv_chat_item_voicelength);
				convertView.setTag(vh);
			}else{
				vh = (ViewHolder) convertView.getTag();
			}
			
			break;
			
		default:
			vh = null;
			break;
		}


		switch (messageType) {
		case 1:
			//������Ϣ��ʱ��
			vh.tvChatTime.setText(TimeUtil.getTime(Long.parseLong(msg.getMsgTime())));
			//ͷ��
			if(TextUtils.isEmpty(msg.getBelongAvatar())){
				vh.ivAvatar.setImageResource(R.drawable.ic_launcher);
			}else{
				ImageLoader.getInstance().displayImage(msg.getBelongAvatar(), vh.ivAvatar);
			}
			//��������
			//vh.tvContent.setText(msg.getContent());
			//��ʾ����������ݣ���Ҫ����ChatUtil��getSpannableString
			vh.tvContent.setText(ChatUtil.getSpannableString(msg.getContent()));
			break;

		case 2:
			//������Ϣ��ʱ��
			vh.tvChatTime.setText(TimeUtil.getTime(Long.parseLong(msg.getMsgTime())));
			//ͷ��
			if(TextUtils.isEmpty(msg.getBelongAvatar())){
				vh.ivAvatar.setImageResource(R.drawable.ic_launcher);
			}else{
				ImageLoader.getInstance().displayImage(msg.getBelongAvatar(), vh.ivAvatar);
			}
			//ͼ��������������msg.getContent;
			//�Խ��շ���˵�����ľ���ͼ���ڷ������ϵĵ�ַ
			//            ���磺http://s.bmob.cn/xdQTrw
			//�Է��ͷ���˵�����Ļ���ݷ��ͽ׶εĲ�ͬ����һ��
			//�ڷ��Ϳ�ʼ�׶Σ�statusΪBombConfig.SEND_START��ʱ
			//������һ��ͼ���ڷ������豸�ϵı���·��
			//             ���磺file:////storage/emulated/0/Samsung/Image/008.jpg
			//�ڶԷ��Ѿ��յ��׶�(statusΪBmobConfig.SEND_RECEIVED)ʱ
			//������ͼ���ڷ������豸�ϵı���·��+"&"+��ͼ���ڷ������ϴ洢�ĵ�ַ
			//             ���磺file:////storage/emulated/0/Samsung/Image/008.jpg&http://s.bmob.cn/xdQTrw

			String imgAddress = msg.getContent();
			if(imgAddress.indexOf("&")>0){
				//���imgAddress�а���"&"
				//˵����ǰimgAddress�ĸ�ʽΪ��file:////storage/emulated/0/Samsung/Image/008.jpg&http://s.bmob.cn/xdQTrw
				String url = imgAddress.split("&")[1];//http://s.bmob.cn/xdQTrw
				ImageLoader.getInstance().displayImage(url, vh.ivImgContent);
			}else{
				if(msg.getBelongId().equals(myid)){
					//���ڷ��ͷ���˵��imgAddress�ĸ�ʽΪfile:////storage/emulated/0/Samsung/Image/008.jpg
					String url = imgAddress.split("///")[1];
					Bitmap bitmap = BitmapFactory.decodeFile(url);
					vh.ivImgContent.setImageBitmap(bitmap);
				}else{
					//���ڽ��շ���˵��imgAddress�ĸ�ʽΪhttp://s.bmob.cn/xdQTrw
					ImageLoader.getInstance().displayImage(imgAddress, vh.ivImgContent);
				}
			}

			if(vh.pbSending!=null){
				//����msg��status����pbSending�Ƿ�ɼ�
				if(msg.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){
					vh.pbSending.setVisibility(View.INVISIBLE);
				}else{
					vh.pbSending.setVisibility(View.VISIBLE);
				}
			}
			break;
			
		case 3:
			
			//������Ϣ��ʱ��
			vh.tvChatTime.setText(TimeUtil.getTime(Long.parseLong(msg.getMsgTime())));
			//ͷ��
			if(TextUtils.isEmpty(msg.getBelongAvatar())){
				vh.ivAvatar.setImageResource(R.drawable.ic_launcher);
			}else{
				ImageLoader.getInstance().displayImage(msg.getBelongAvatar(), vh.ivAvatar);
			}
			//λ��������������msg.getContent;
			//������·&http://s.bmob.cn/XXXXX.jpg&γ��&����

			final String location = msg.getContent();
			
			vh.tvAddress.setText(location.split("&")[0]);
			ImageLoader.getInstance().displayImage(location.split("&")[1], vh.ivImgContent);
			
			//Ϊvh.ivImgContent��ӵ����¼��������������ת��LocationActivity
			//��LocationActivity�аѸ�������Ϣ����������λ����Ϣ(�ֵ����ƣ�γ�ȣ�����)��ʾ�ڵ�ͼ��
			vh.ivImgContent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(context,LocationActivity.class);
					intent.putExtra("type", "item");
					intent.putExtra("address", location.split("&")[0]);
					intent.putExtra("lat", location.split("&")[2]);
					intent.putExtra("lng", location.split("&")[3]);
					context.startActivity(intent);
				}
			});
			
			break;
		case 4:
			//������Ϣ��ʱ��
			vh.tvChatTime.setText(TimeUtil.getTime(Long.parseLong(msg.getMsgTime())));
			//ͷ��
			if(TextUtils.isEmpty(msg.getBelongAvatar())){
				vh.ivAvatar.setImageResource(R.drawable.ic_launcher);
			}else{
				ImageLoader.getInstance().displayImage(msg.getBelongAvatar(), vh.ivAvatar);
			}
			//����������������msg.getContent;
			//�Խ��շ���˵�����ľ��ǣ������ļ��ڷ������ϵĵ�ַ&�����ļ��ĳ���
			//            ���磺http://s.bmob.cn/xdQTrw&3
			//�Է��ͷ���˵�����Ļ���ݷ��ͽ׶εĲ�ͬ����һ��
			//�ڷ��Ϳ�ʼ�׶Σ�statusΪBombConfig.SEND_START��ʱ
			//������һ�������ļ��ڷ������豸�ϵı���·��&�����ļ��ĳ���
			//             ���磺/storage/emulated/0/BmobChat/voice/fe23343a53c23173cab71b641adca0b9/24f1a6ccea/1445339948947.amr&3
			//�ڶԷ��Ѿ��յ��׶�(statusΪBmobConfig.SEND_RECEIVED)ʱ
			//�����������ļ��ڷ������豸�ϵı���·��&�������ڷ������ϴ洢�ĵ�ַ&�����ļ��ĳ���
			//             ���磺/storage/emulated/0/BmobChat/voice/fe23343a53c23173cab71b641adca0b9/24f1a6ccea/1445339948947.amr&http://s.bmob.cn/dJrfaF&3

			final String voice = msg.getContent();

			if(vh.pbSending!=null){
				//˵�������Ƿ���һ��������Ϣ(�Ҳ಼��)
				//����msg��status����pbSending�Ƿ�ɼ�
				if(msg.getStatus()==BmobConfig.STATUS_SEND_RECEIVERED){
					vh.pbSending.setVisibility(View.INVISIBLE);
					vh.tvVoiceLength.setVisibility(View.VISIBLE);
					vh.tvVoiceLength.setText(voice.split("&")[2]+"'");
				}else{
					vh.pbSending.setVisibility(View.VISIBLE);
					vh.tvVoiceLength.setVisibility(View.INVISIBLE);
				}
			}else{
				//�����ļ��Ľ��շ�
				vh.tvVoiceLength.setText(voice.split("&")[1]+"'");
			}
			
			vh.ivImgContent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					//1)���������ļ�
					//2)���������ļ������У�����һ��֡����
					String url="";
					Position pos;
					if(getItemViewType(position)==SEND_VOICEMESSAGE){
						pos = Position.RIGHT;
						url = voice.split("&")[1];
					}else{
						pos = Position.LEFT;
						url = voice.split("&")[0];
					}
					playVoiceMessage(url, pos, vh.ivImgContent);
					
				}
			});
			
			
			break;
		default:
			break;
		}




		return convertView;
	}

	/**
	 * ���������ļ�ͬʱ����֡����
	 * @param url Ҫ���ŵ������ļ��ĵ�ַ
	 * @param position LEFT��RIGHT
	 * @param iv ����֡������ImageView
	 */
	protected void playVoiceMessage(String url,final Position pos,final ImageView iv) {
		try {
			MediaPlayer mp = new MediaPlayer();
			mp.setOnPreparedListener(new OnPreparedListener() {
				
				@Override
				public void onPrepared(MediaPlayer mp) {
					mp.start();
					//֡����
					if(pos==Position.LEFT){
						Drawable drawable = context.getResources().getDrawable(R.drawable.voice_left_anim);
						iv.setImageDrawable(drawable);
						((AnimationDrawable)drawable).start();
					}else{
						Drawable drawable = context.getResources().getDrawable(R.drawable.voice_right_anim);
						iv.setImageDrawable(drawable);
						((AnimationDrawable)drawable).start();
					}
				}
			});
			
			mp.setOnCompletionListener(new OnCompletionListener() {
				
				@Override
				public void onCompletion(MediaPlayer mp) {
					mp.stop();
					Drawable drawable = iv.getDrawable();
					((AnimationDrawable)drawable).stop();
					if(pos == Position.LEFT){
						iv.setImageResource(R.drawable.voice_right3);
					}else{
						iv.setImageResource(R.drawable.voice_left3);
					}
					mp.release();
				}
			});
			mp.setDataSource(url);
			mp.prepareAsync();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public int getViewTypeCount() {
		//��ListView��Ҫ��ʾ���ֲ�ͬ����Ŀ��ͼ
		//�÷����ͷ��ؼ�
		return 8;
	}

	@Override
	public int getItemViewType(int position) {
		//����ʾ����Դ���±�Ϊposition������ʱ
		//Ӧ��ʹ��������Ŀ��ͼ
		BmobMsg msg = getItem(position);
		//objId����ʱ����msg����������Ϣ���û���objectId
		String objId = msg.getBelongId();
		//messageType������Ϣ������
		//ȡֵ��Χ��1 �ı��� 2 ͼ�� 3 λ�ã� 4 ����
		int messageType = msg.getMsgType();
		switch (messageType) {
		case 1:
			if(objId.equals(myid)){
				//֤��msg������Ϣ���ҷ�����
				return SEND_TEXTMESSAGE;
			}else{
				//֤��msg������Ϣ�����ҷ�����(���յ���)
				return RECEIVE_TEXTMESSAGE;
			}
		case 2:
			if(objId.equals(myid)){
				return SEND_IMAGEMESSAGE;
			}else{
				return RECEIVE_IMAGEMESSAGE;
			}
		case 3: 
			//λ����Ϣ���
			if(objId.equals(myid)){
				return SEND_LOCATIONMESSAGE;
			}else{
				return RECEIVE_LOCATIONMESSAGE;
			}
		case 4: 
			//������Ϣ���
			if(objId.equals(myid)){
				return SEND_VOICEMESSAGE;
			}else{
				return RECEIVE_VOICEMESSAGE;
			}
		default:
			//return -1;
			throw new RuntimeException("�������Ϣ����");
		}
	}

	public class ViewHolder{
		TextView tvChatTime;
		ImageView ivAvatar;
		TextView tvContent;

		ImageView ivImgContent;//��ʾ������Ϣ�е�ͼ��
		ProgressBar pbSending;//����ͼ��������Ϣʱ����ʾ

		TextView tvAddress;//��ʾλ������������Ϣ�ĵ�ַ��Ϣ
		
		TextView tvVoiceLength;//��ʾ��������������Ϣ����������
		
	}

}
