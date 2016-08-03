package com.Dinggrn.weiliao.ui;

import java.io.File;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.view.NumberProgressBar;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.nostra13.universalimageloader.core.ImageLoader;

public class UserInfoActivity extends BaseActivity {
	
	String from;//me,friend,stranger�����ŴӲ�ͬ�ط���
	String username;//�û���
	
	@Bind(R.id.headerview)
	View headerView;
	@Bind(R.id.iv_userinfo_avatar)
	ImageView ivAvatar;//ͷ��
	@Bind(R.id.iv_userinfo_avatareidtor)
	ImageView ivAvatarEditor;//�༭ͷ���Ǧ��
	
	@Bind(R.id.tv_userinfo_nickname)
	TextView tvNickname;//��ʾ�ǳƵ�TextView
	@Bind(R.id.iv_userinfo_nicknameeidtor)
	ImageView ivNicknameEditor;//�༭�ǳƵ�Ǧ��
	@Bind(R.id.ll_userinfo_editnicknamecontainer)
	View nicknameEditContainer;//LinearLayout��������ű༭�ǳƵ�EditText��������ť
	@Bind(R.id.et_userinfo_eidtnickname)
	EditText etNickname;//�ǳƵı༭��
	@Bind(R.id.btn_userinfo_savenickname)
	ImageButton btnSaveNickname;//�����ǳư�ť
	@Bind(R.id.btn_userinfo_cancelnickname)
	ImageButton btnCancelNickname;//�����༭�ǳ�
	
	@Bind(R.id.tv_userinfo_username)
	TextView tvUsername;//��ʾ�û���
	
	@Bind(R.id.iv_userinfo_gender)
	ImageView ivGender;//��ʾ�Ա�
	
	@Bind(R.id.btn_userinfor_updateinfo)
	Button btnUpdateInfo;//�����û�����
	
	@Bind(R.id.btn_userinfor_startchat)
	Button btnStartChat;//��ʼ����
	
	@Bind(R.id.btn_userinfor_addblack)
	Button btnAddBlack;//���������
	
	@Bind(R.id.npb_userinfo_progressbar)
	NumberProgressBar npb;
	
	ProgressDialog pd;
	String camPath;
	String filePath;
	String avatarUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_info);
		ButterKnife.bind(this);
		from = getIntent().getStringExtra("from");
		if("me".equals(from)){
			username = bmobUserManager.getCurrentUserName();
		}else{
			username = getIntent().getStringExtra("username");
		}
		initHeaderView();
		initView();
		
	}


	private void initHeaderView() {
		String text = "";
		if("me".equals(from)){
			text = "�ҵ�����";
		}else{
			text = username;
		}
		setHeaderTitle(headerView, text, Position.CENTER);
		
		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	
	}


	private void initView() {
		//���û���ʾ���ڼ���username��Ӧ���û���Ϣ
		if(pd==null){
			pd = ProgressDialog.show(this, "", "���ڼ����û���Ϣ�����Ժ�...");
		}
		pd.show();
		//1)Ҫ����username--->��ö�Ӧ��User����
		bmobUserManager.queryUser(username, new FindListener<User>() {

			@Override
			public void onError(int arg0, String arg1) {
				pd.dismiss();
				toastAndLog("����ʧ�ܣ��Ժ�����", arg0+":"+arg1);
			}

			@Override
			public void onSuccess(List<User> arg0) {
				//2)����User�����е����ԣ��ŵ���Ӧ����ͼ����ʾ
				pd.dismiss();
				User user = arg0.get(0);
				showUserInfo(user);
			}
			
		});
		
		if("me".equals(from)){
			ivAvatarEditor.setVisibility(View.VISIBLE);
			ivNicknameEditor.setVisibility(View.VISIBLE);
		}else{
			ivAvatarEditor.setVisibility(View.INVISIBLE);
			ivNicknameEditor.setVisibility(View.INVISIBLE);
		}
		
		if("me".equals(from)){
			btnUpdateInfo.setVisibility(View.VISIBLE);
		}else{
			btnUpdateInfo.setVisibility(View.INVISIBLE);
		}
		
		if("friend".equals(from)){
			btnStartChat.setVisibility(View.VISIBLE);
			btnAddBlack.setVisibility(View.VISIBLE);
		}else{
			btnStartChat.setVisibility(View.INVISIBLE);
			btnAddBlack.setVisibility(View.INVISIBLE);
		}
		//���ֽ�������ʼ���ɼ�
		npb.setVisibility(View.INVISIBLE);
		
	}

	/**
	 * ���û���Ϣ�ŵ������и�����ͼ����
	 * @param user
	 */
	protected void showUserInfo(User user) {
		//ͷ��
		if(TextUtils.isEmpty(user.getAvatar())){
			ivAvatar.setImageResource(R.drawable.ic_launcher);
		}else{
			ImageLoader.getInstance().displayImage(user.getAvatar(), ivAvatar);
		}
		//�ǳ�
		tvNickname.setText(user.getNick());
		//�û���
		tvUsername.setText(user.getUsername());
		//�Ա�
		if(user.getGender()){
			ivGender.setImageResource(R.drawable.boy);
		}else{
			ivGender.setImageResource(R.drawable.girl);
		}
	}
	
	@OnClick(R.id.iv_userinfo_nicknameeidtor)
	public void setNickname(View v){
		
		tvNickname.setVisibility(View.INVISIBLE);
		String nickname = tvNickname.getText().toString();
		
		nicknameEditContainer.setVisibility(View.VISIBLE);
		if(!TextUtils.isEmpty(nickname)){
			etNickname.setText(nickname);
		}
	}
	
	@OnClick(R.id.btn_userinfo_savenickname)
	public void saveNickname(View v){
		String nickname = etNickname.getText().toString();
		etNickname.setText("");
		nicknameEditContainer.setVisibility(View.INVISIBLE);
		tvNickname.setVisibility(View.VISIBLE);
		tvNickname.setText(nickname);
	}
	
	@OnClick(R.id.btn_userinfo_cancelnickname)
	public void cancelNickname(View v){
		etNickname.setText("");
		nicknameEditContainer.setVisibility(View.INVISIBLE);
		tvNickname.setVisibility(View.VISIBLE);
	}
	
	@OnClick(R.id.iv_userinfo_avatareidtor)
	public void setAvatar(View v){
		
		Intent intent1 = new Intent(Intent.ACTION_PICK);
		intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		
		Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),System.currentTimeMillis()+".png");
		camPath = file.getAbsolutePath();
		Uri imageUri = Uri.fromFile(file );
		intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri );
		
		Intent chooser = Intent.createChooser(intent1, "ѡ��ͷ��");
		chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
		startActivityForResult(chooser, 101);
		
	}
	
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0==101 && arg1==RESULT_OK){
			if(arg2!=null){
				Uri uri = arg2.getData();
				Cursor c = getContentResolver().query(uri,new String[]{MediaStore.Images.Media.DATA}, null,null,null);
				c.moveToNext();
				filePath = c.getString(0);
				c.close();
			}else{
				filePath = camPath;
			}
			//���ֽ������ɼ�
			npb.setVisibility(View.VISIBLE);
			BmobProFile.getInstance(this).upload(filePath, new UploadListener() {
				
				@Override
				public void onError(int arg0, String arg1) {
					toast("ͷ���ϴ�ʧ�ܣ��Ժ�����");
					avatarUrl="";
					npb.setVisibility(View.INVISIBLE);
					npb.setProgress(0);
				}
				
				@Override
				public void onSuccess(String arg0, String arg1, BmobFile arg2) {
					avatarUrl = arg2.getUrl();
					ImageLoader.getInstance().displayImage(avatarUrl, ivAvatar);
					npb.setVisibility(View.INVISIBLE);
					npb.setProgress(0);
				}
				
				@Override
				public void onProgress(int arg0) {
					npb.setPrefix("���ϴ�");
					npb.setProgress(arg0);
					npb.setSuffix("����");
				}
			});
		}
	}
	
	@OnClick(R.id.btn_userinfor_updateinfo)
	public void updateInfo(View v){
		if(pd==null){
			pd = ProgressDialog.show(this, "","���ڼ����û���Ϣ�����Ժ�..." );
		}
		pd.show();
		User newuser = new User();
		newuser.setAvatar(avatarUrl);
		newuser.setNick(tvNickname.getText().toString());
		newuser.update(this, bmobUserManager.getCurrentUserObjectId(), new UpdateListener() {
			
			@Override
			public void onSuccess() {
				pd.dismiss();
				toast("���ϸ������");
				
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				pd.dismiss();
				toastAndLog("���ϸ���ʧ��",arg0+": "+arg1);
			}
		});
		
	}
	
	
	@OnClick(R.id.btn_userinfor_startchat)
	public void startChat(View v){
		//���ݴ��ݹ������û�������ø��û�����Ӧ���û�
		//�����û����ݵ�ChatActivity��
		List<BmobChatUser> friends = bmobDB.getContactList();
		for(BmobChatUser bcu:friends){
			if(bcu.getUsername().equals(username)){
				Intent intent = new Intent(this,ChatActivity.class);
				intent.putExtra("user", bcu);
				jump(intent, true);
				break;
			}
		}
		
	}
}
