package com.Dinggrn.weiliao.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import cn.bmob.im.BmobRecordManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnRecordChangeListener;
import cn.bmob.im.inteface.UploadListener;
import cn.bmob.im.util.BmobNetUtil;
import cn.bmob.v3.listener.PushListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.ChatAdapter;
import com.Dinggrn.weiliao.adapter.EmoAdapter;
import com.Dinggrn.weiliao.adapter.EmoPagerAdapter;
import com.Dinggrn.weiliao.bean.Emo;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.receiver.MyReceiver;
import com.Dinggrn.weiliao.util.ChatUtil;
import com.viewpagerindicator.CirclePageIndicator;

public class ChatActivity extends BaseActivity implements EventListener{
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.lv_chat_messages)
	ListView listView;
	@Bind(R.id.et_chat_input)
	EditText etInput;//�ı�������Ϣ�������
	@Bind(R.id.btn_chat_add)
	Button btnAdd;//���Ӻš���ť
	@Bind(R.id.btn_chat_send)
	Button btnSend;//�ı�������Ϣ�ķ��Ͱ�ť�����ɻ�����ť��

	List<BmobMsg> messages;//listView����ʾ����ĳ���û�֮�������������Ϣ
	ChatAdapter adapter;//������

	BmobChatUser targetUser;//����������û�
	String targetId;//targetUser��objectId��
	String targetUsername;//targetUser���û���
	String myId;//��ǰ��¼�û���objectId


	//�������
	@Bind(R.id.ll_chat_morelayoutcontainer)
	LinearLayout morelayoutContainer;//��Ӧʾ��ͼ�С���ɫ��������

	RelativeLayout emoContainer;//��Ӧʾ��ͼ�С���ɫ��������
	ViewPager emoViewPager;//emoContainer�е�viewPager
	EmoPagerAdapter emoPagerAdapter;//��emoViewPager��Ϲ���ʾ��ͼ�С���ɫ�������pagerAdapter
	CirclePageIndicator pageIndicator;//viewPager�ġ�ָʾ����

	//���ͼƬ����Ƭ��ء�λ�����
	LinearLayout piclocContainer;
	String cameraPath;
	String filePath;

	//����������ص�����
	@Bind(R.id.ll_textinput_container)
	LinearLayout textinputContainer;//�ı�������ص�����
	@Bind(R.id.ll_voicinput_container)
	LinearLayout voiceinputContainer;//����������ص�����
	@Bind(R.id.ll_chat_voicerecordcontainer)
	LinearLayout voicerecordContainer;

	@Bind(R.id.iv_chat_recordvolume)
	ImageView ivRecordVolume;//¼��ʱ����������С��ʾ��ͬ��ͼƬ
	@Bind(R.id.tv_chat_recordtips)
	TextView tvRecordTips;//¼��ʱ������ָ��λ����ʾ��ͬ����ʾ��Ϣ
	@Bind(R.id.btn_chat_voiceinput)
	Button btnVoiceInput;//������˵������ť

	BmobRecordManager bmobRecordManager;//BmobIMSDK��¼��������
	Drawable[] recordDrawables;//��ű�ʾ��ͬ������ͼƬ


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);
		ButterKnife.bind(this);
		targetUser = (BmobChatUser) getIntent().getSerializableExtra("user");
		targetId = targetUser.getObjectId();
		targetUsername = targetUser.getUsername();
		myId = bmobUserManager.getCurrentUserObjectId();
		initHeaderView();
		initListView();
		initView();
	}


	private void initView() {
		// 1)ΪetInput���һ��TextWathcer������
		// �ü����������etInput���ı����ݵı仯������Ӧ�ķ���
		etInput.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				//��etInput���ݷ����ı��etInput�е��ı�
				//����Ϊ��������÷�����
				if(s.length()>0){
					//etInput��������
					btnAdd.setVisibility(View.INVISIBLE);
					btnSend.setVisibility(View.VISIBLE);
				}else{
					btnAdd.setVisibility(View.VISIBLE);
					btnSend.setVisibility(View.INVISIBLE);
				}


			}
		});

		//2Ϊ�����顱��ص����Ը�ֵ
		emoContainer = (RelativeLayout) getLayoutInflater().inflate(R.layout.emo_layout, morelayoutContainer,false);
		emoViewPager = (ViewPager) emoContainer.findViewById(R.id.vp_emolayout);
		pageIndicator = (CirclePageIndicator) emoContainer.findViewById(R.id.pagerindicator_emolayout);

		List<View> views = new ArrayList<View>();
		for(int i=0;i<2;i++){
			View view = getLayoutInflater().inflate(R.layout.emo_gridview_layout, emoContainer,false);

			GridView gridView = (GridView) view.findViewById(R.id.gv_emogridview);

			List<Emo> emos = new ArrayList<Emo>();
			if(i==0){
				//emos�д��ChatUtil.emos��ǰ21������
				emos = ChatUtil.emos.subList(0,21);
			}else{
				//emos�д��ChatUtil.emos�к�21������
				emos = ChatUtil.emos.subList(21, ChatUtil.emos.size());
			}
			final EmoAdapter adapter = new EmoAdapter(this, emos);
			gridView.setAdapter(adapter);
			//������飬������ŵ�etInput����ʾ
			gridView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Emo emo = adapter.getItem(position);
					String emoId = emo.getId();//[emo]ue057
					etInput.append(ChatUtil.getSpannableString(emoId));
					//					String string = etInput.getText().toString();
					//					etInput.setText(ChatUtil.getSpannableString(string));
				}
			});
			views.add(view);
		}
		emoPagerAdapter = new EmoPagerAdapter(views );
		emoViewPager.setAdapter(emoPagerAdapter);
		//pageIndicator��viewPager���а󶨵����
		//һ��Ҫд��viewPager��PagerAdapter�󶨵����ĺ���
		pageIndicator.setViewPager(emoViewPager,0);
		//ָ��pageIndicator��ʲô��ɫ����䡰Ȧ��
		pageIndicator.setFillColor(Color.BLACK);

		//3 ΪͼƬ��λ����ص����Ը�ֵ
		piclocContainer = (LinearLayout) getLayoutInflater().inflate(R.layout.add_layout, morelayoutContainer,false);
		View takePic = piclocContainer.findViewById(R.id.tv_picloc_takepic);
		takePic.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_PICK);
				intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent, 101);
			}
		});
		View makePhoto = piclocContainer.findViewById(R.id.tv_picloc_makephoto);
		makePhoto.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),System.currentTimeMillis()+".png");
				cameraPath = file.getAbsolutePath();
				Uri imgUri = Uri.fromFile(file);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
				startActivityForResult(intent, 101);

			}
		});
		View location = piclocContainer.findViewById(R.id.tv_picloc_location);
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// ��ת����ͼ������ж�λ
				Intent intent = new Intent(ChatActivity.this,LocationActivity.class);
				intent.putExtra("type", "button");
				startActivityForResult(intent, 102);


			}
		});

		//4. ������������ص����Ը�ֵ
		recordDrawables = new Drawable[]{
				getResources().getDrawable(R.drawable.chat_icon_voice1),
				getResources().getDrawable(R.drawable.chat_icon_voice2),
				getResources().getDrawable(R.drawable.chat_icon_voice3),
				getResources().getDrawable(R.drawable.chat_icon_voice4),
				getResources().getDrawable(R.drawable.chat_icon_voice5),
				getResources().getDrawable(R.drawable.chat_icon_voice6)
		};

		bmobRecordManager = BmobRecordManager.getInstance(this);
		bmobRecordManager.setOnRecordChangeListener(new OnRecordChangeListener() {

			@Override
			public void onVolumnChanged(int value) {
				//¼�������У������������仯ʱ���÷����ᱻ�ص�
				//���������е�value�Ͷ�Ӧ��ǰbmobRecordManager����⵽��������С
				//valueȡֵ��Χ0-5��ǡ�ö�ӦrecordDrawables������ͼ����±�ֵ
				ivRecordVolume.setImageDrawable(recordDrawables[value]);
			}

			@Override
			public void onTimeChanged(int value, String localPath) {
				//¼�������У�����ʱ������ţ�ÿһ���ӻᱻ����һ��
				//value����ǰ¼���ĳ��ȣ��������ֵ���ܳ���60��
				//���һ��valueֵ����60�ˣ�Ҫǿ�н�����ǰ������¼�ƣ������Ѿ�¼�ƺõ�60�����ݷ��ͳ�ȥ
				//localPath����ǰ¼���ļ��������·��
				if(value>BmobRecordManager.MAX_RECORD_TIME){

					btnVoiceInput.setPressed(false);
					btnVoiceInput.setClickable(false);
					voicerecordContainer.setVisibility(View.INVISIBLE);
					sendVoiceMessage(value,localPath);
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							btnVoiceInput.setClickable(true);
						}
					}, 1500);
					toast("�Ѵﵽ�¼��ʱ��");
				}
			}
		});

	}

	/**
	 * �����������͵�������Ϣ
	 * @param value
	 * @param localPath
	 */
	protected void sendVoiceMessage(int length, String localPath) {
		// ����������Ϣ��ҵ���߼��뷢��ͼ�����͵�����һģһ����
		bmobChatManager.sendVoiceMessage(targetUser, localPath, length, new UploadListener() {
			
			@Override
			public void onSuccess() {
				// �������ļ��ϴ��ɹ������ø�onSucess����֮ǰ
				// BmobIMSDK��ȥ��д���ݿ��б����ԭmsg����Ϣ
				// �޸����ݱ���msg��content�ֶε�ֵ��
				// ��ΪlocalPath&url&length
				// ���磺/storage/emulated/0/BmobChat/voice/fe23343a53c23173cab71b641adca0b9/24f1a6ccea/1445339948947.amr&http://s.bmob.cn/dJrfaF&3
				// �޸����ݱ���msg��status�ֶε�ֵ��
				// ��ΪBmobConfig.STATUS_SEND_SUCCESS
				bmobDB.resetUnread(targetId);
			}
			
			@Override
			public void onStart(final BmobMsg msg) {
				// msg������BmobIMSDK���������ṩ�Ĳ������ɵ�
				// msg������(content)��localPath&length
				// ���磺/storage/emulated/0/BmobChat/voice/fe23343a53c23173cab71b641adca0b9/24f1a6ccea/1445339948947.amr&3
				msg.setStatus(BmobConfig.STATUS_SEND_START);
				bmobChatManager.saveReceiveMessage(false, msg);
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						adapter.add(msg);
					}
				});
			}
			
			@Override
			public void onFailure(int error, String arg1) {
				// TODO Auto-generated method stub
				
			}
		});

	}


	private void initHeaderView() {
		setHeaderTitle(headerView, targetUsername, Position.CENTER);
		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}


	private void initListView() {
		messages = new ArrayList<BmobMsg>();
		adapter = new ChatAdapter(this, messages);
		listView.setAdapter(adapter);

	}

	@Override
	protected void onResume() {
		super.onResume();
		MyReceiver.regist(this);
		refresh();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MyReceiver.unregist(this);
	}

	private void refresh() {
		adapter.addAll(bmobDB.queryMessages(targetId, 0),true);
		//�ô�����ListView���������һ��������Ϣ������ʾ
		listView.setSelection(adapter.getCount()-1);
	}


	@OnClick(R.id.btn_chat_send)
	public void sendTextMessage(View v){
		String string = etInput.getText().toString();
		if(TextUtils.isEmpty(string)){
			return;
		}
		if(!BmobNetUtil.isNetworkAvailable(this)){
			toast("��ǰ���粻����");
			return;
		}
		//���������ı�������������
		//����BmobMsg�е�һ������������string������BmobMsg����
		final BmobMsg msg = BmobMsg.createTextSendMsg(this, targetId, string);
		bmobChatManager.sendTextMessage(targetUser, msg, new PushListener() {

			@Override
			public void onSuccess() {
				//���ͳɹ�
				//1)���etInput���ѷ��͵�����
				etInput.setText("");
				//2)�ѸղŴ�����bmobmsg����ŵ�ListView����ʾ
				adapter.add(msg);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				//����ʧ��
				toastAndLog("��������æ�����Ժ�����",arg0+":"+arg1);
			}
		});

	}


	@Override
	public void onMessage(BmobMsg message) {
		//���յ��˱��˷����ҵ�������Ϣ��ʱ�򣬸÷����ᱻ����
		//���յ�����Ϣ�ŵ�ListView����ʾ
		//ֻ����ǰ�����û����͵�BmobMsg
		//�����˷��͵����ݲ�����
		//���뿪ChatActivity��ʱ����MainActivity��ȥ����
		if(message.getBelongId().equals(targetId)){
			adapter.add(message);
			//��message���Ѷ�δ��״̬��δ��--->�Ѷ�
			bmobDB.resetUnread(targetId);
		}
	}


	@Override
	public void onReaded(String conversionId, String msgTime) {
		//�÷��������ã���ζ�ŷ��ͷ����͵���Ϣ�Է��Ѿ����ܵ���
		//1) �����͵���Ϣ����֮ǰsendImageMessage�м�������onStart�����еĲ�������״̬��ΪSuccess_Received
		//BmobMsg message = bmobDB.getMessage(conversionId, msgTime);
		//message.setStatus(BmobConfig.STATUS_SEND_RECEIVERED);
		bmobDB.updateTargetMsgStatus(BmobConfig.STATUS_SEND_RECEIVERED, conversionId, msgTime);
		//2) ֪ͨChatAdapterȥ�ı䷢����Ϣ����ʾ״��������˵��progressBar�ģ���progressBar���أ�
		//adapter.notifyDataSetChanged();//ListView����������Դ������
		//��ʱִ��refresh������chat����ȡ������Ϣ
		//��ͼ����Ϣ��ʼ����ʱ�����ݷ����˱仯
		//content�仯�ˣ�status�仯��
		//ԭ��content�����ݣ�file:////storage/emulated/0/Samsung/Image/008.jpg
		//���ĺ��content�����ݣ�file:////storage/emulated/0/Samsung/Image/008.jpg&http://s.bmob.cn/xdQTrw
		//ԭ��status�����ݣ�BmobConfig.STATUS_SEND_START
		//���ĺ��status�����ݣ�BmobConfig.STATUS_SEND_RECEIVED
		log("���͵�ͼ����Ϣ�Է����յ�");
		refresh();
	}


	@Override
	public void onNetChange(boolean isNetConnected) {
		toast("��ǰ���粻����");

	}


	@Override
	public void onAddUser(BmobInvitation message) {
		//��ĳ�û�����ʱ������Ӻ��Ѳ�����
		//����ChatActivity���ص�MainActivity
		//����MainActivityȥ����

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
		builder.setMessage("�����˺��Ѿ�����һ̨�豸��¼��");
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

	@OnClick(R.id.btn_chat_emotion)
	public void showEmos(View v){
		if(morelayoutContainer.getVisibility()==View.VISIBLE){
			morelayoutContainer.setVisibility(View.GONE);
		}else{
			morelayoutContainer.removeAllViews();
			morelayoutContainer.setVisibility(View.VISIBLE);
			morelayoutContainer.addView(emoContainer);
		}
	}

	@OnClick(R.id.btn_chat_add)
	public void showPicLocation(View v){
		if(morelayoutContainer.getVisibility()==View.VISIBLE){
			morelayoutContainer.setVisibility(View.GONE);
		}else{
			//��������Ǵ��������������װ�ť��ʱ��
			//�������������װ�ť��Ϊ�ı��������װ�ť
			if(voiceinputContainer.getVisibility()==View.VISIBLE){
				voiceinputContainer.setVisibility(View.GONE);
				textinputContainer.setVisibility(View.VISIBLE);
			}
			morelayoutContainer.removeAllViews();
			morelayoutContainer.setVisibility(View.VISIBLE);
			morelayoutContainer.addView(piclocContainer);
		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg1 == RESULT_OK){
			if(arg0 == 101){
				if(arg2!=null){//��ͼ��ѡͼ
					Uri uri = arg2.getData();
					Cursor c = getContentResolver().query(uri, new String[]{Media.DATA}, null, null, null);
					c.moveToNext();
					filePath = c.getString(0);
					c.close();
				}else{//���������
					filePath = cameraPath;
				}
				//����filePath����ͼ�����͵�������Ϣ����
				//log("filePath:"+filePath);
				bmobChatManager.sendImageMessage(targetUser, filePath, new UploadListener() {

					@Override
					public void onSuccess() {
						//ͼ���Ѿ��ɹ��ϴ�����Bmob������
						//BmobChatManager��ͼ��ɹ��ϴ���Bmob��������
						//�ڵ���onSuccess����֮ǰ����͵͵����һ����
						//��onStart�����������Ǹ�BmobMsg���������ݿ��е����������޸�
						//��Ҫ�޸��������ݣ�content�ֶ�ֵ�޸��ˣ�status�ֶ�ֵҲ���޸�
						//ԭ��content�����ݣ�file:////storage/emulated/0/Samsung/Image/008.jpg
						//���ĺ��content�����ݣ�file:////storage/emulated/0/Samsung/Image/008.jpg&http://s.bmob.cn/xdQTrw
						//��msg�ġ��Ѷ�δ����״̬(isReaded���Դ���BmobMsg������Ѷ�δ��״̬),��Ĭ�ϵ�δ��״̬����Ϊ�Ѷ�
						//ԭ��status�����ݣ�BmobConfig.STATUS_SEND_START
						//���ĺ��status�����ݣ�BmobConfig.STATUS_SEND_SUCCESS
						bmobDB.resetUnread(targetId);
					}

					@Override
					public void onStart(final BmobMsg msg) {
						//msg�а�����Ҫ���͵�ͼ��������Ϣ�У�ͼ���·��
						//�趨��msg��statusΪ���Ϳ�ʼ
						msg.setStatus(BmobConfig.STATUS_SEND_START);
						//msg���浽chat��recent��
						bmobChatManager.saveReceiveMessage(false, msg);
						//�����msg�ŵ�listView����ʾ
						//����Ҫ��ui�߳�ִ�У���Ҫ����ui�̵߳�Handler�����
						new Handler(Looper.getMainLooper()).post(new Runnable() {
							@Override
							public void run() {
								adapter.add(msg);
							}
						});


					}

					@Override
					public void onFailure(int error, String arg1) {
						// TODO Auto-generated method stub

					}
				});
			}

			if(arg0==102){
				//����λ���������
				//��arg2��ȡֵ(��LocationActivity���ݹ���)
				String address = arg2.getStringExtra("address");
				String url = arg2.getStringExtra("url");//��ͼ��ͼ�ڷ�������ַ
				double lat = arg2.getDoubleExtra("lat", 0.0);
				double lng = arg2.getDoubleExtra("lng", 0.0);

				log("��ַ��"+address+" ,��ͼ��ַ��"+url+" ,γ��/����:("+lat+" , "+lng+")");

				//����BmobChatManager����һ��λ����ص�������Ϣ
				//Ĭ��ʱ��һ��λ�����͵�������Ϣ��content�ǣ�address&lat&lng
				//���ǹ�����λ�����͵�������Ϣ��content��: address&url&lat&lng
				final BmobMsg msg = BmobMsg.createLocationSendMsg(this, targetId, address+"&"+url, lat, lng);
				bmobChatManager.sendTextMessage(targetUser, msg, new PushListener() {

					@Override
					public void onSuccess() {
						adapter.add(msg);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub

					}
				});
			}
		}
	}

	@OnClick(R.id.btn_chat_voice)
	public void showVoiceInput(View v){
		textinputContainer.setVisibility(View.GONE);
		morelayoutContainer.setVisibility(View.GONE);
		voiceinputContainer.setVisibility(View.VISIBLE);
	}
	@OnClick(R.id.btn_chat_keyboard)
	public void showTextInput(View v){
		voiceinputContainer.setVisibility(View.GONE);
		textinputContainer.setVisibility(View.VISIBLE);
	}

	@OnTouch(R.id.btn_chat_voiceinput)
	public boolean record(View v,MotionEvent event){
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			//¼����ʼ
			
			btnVoiceInput.setPressed(true);
			voicerecordContainer.setVisibility(View.VISIBLE);
			tvRecordTips.setText("��ָ���ƣ�ȡ������");
			
			bmobRecordManager.startRecording(targetId);
			break;
		case MotionEvent.ACTION_MOVE:
			//������ָλ�õĲ�ͬ��tvRecordTipsҪ����ͬ����ʾ��Ϣ
			float y = event.getY();
			if(y<0){
				//��ָ�ڰ�ť������
				tvRecordTips.setText("�ɿ���ָ��ȡ������");
			}else{
				tvRecordTips.setText("��ָ���ƣ�ȡ������");
			}
			break;
		case MotionEvent.ACTION_UP:
			btnVoiceInput.setPressed(false);
			//��ָ̧���λ��
			if(event.getY()<0){
				bmobRecordManager.cancelRecording();
			}else{
				int recordLength = bmobRecordManager.stopRecording();
				if(recordLength<BmobRecordManager.MIN_RECORD_TIME){
					toast("¼��ʱ�����");
				}else{
					sendVoiceMessage(recordLength, bmobRecordManager.getRecordFilePath(targetId));
				}
			}
			voicerecordContainer.setVisibility(View.INVISIBLE);
			break;
		}
		return true;
	}
}
