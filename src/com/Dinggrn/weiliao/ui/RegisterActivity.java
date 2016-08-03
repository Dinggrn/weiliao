package com.Dinggrn.weiliao.ui;

import java.io.File;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.im.util.BmobNetUtil;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.util.PinYinUtil;
import com.Dinggrn.weiliao.view.NumberProgressBar;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadListener;
import com.dd.CircularProgressButton;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RegisterActivity extends BaseActivity {
	
	@Bind(R.id.headerview)
	View headerView;
	
	@Bind(R.id.et_regist_username)
	EditText etUsername;
	
	@Bind(R.id.et_regist_password)
	EditText etPassword;
	
	@Bind(R.id.et_regist_repassword)
	EditText etRePassword;//�ٴ���������
	
	@Bind(R.id.rg_regist_gendergroup)
	RadioGroup rgGender;
	
	@Bind(R.id.btn_regist_regist)
	CircularProgressButton cpbRegist;//ע�ᰴť
	
	@Bind(R.id.iv_uimage)
	ImageView ivAvatar;
	@Bind(R.id.npb_userinfo_progressbar)
	NumberProgressBar npb;
	String camPath;
	String filePath;
	String avatarUrl;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		ButterKnife.bind(this);
		//���ֽ�������ʼ���ɼ�
		npb.setVisibility(View.INVISIBLE);
		initHeaderView();
		
	}


	private void initHeaderView() {

		setHeaderTitle(headerView, "���û�");
		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT,new OnClickListener() {
			@Override
			public void onClick(View v) {
				jump(LoginActivity.class,true);
			}
		});
	}
	/**
	 * ����ͷ��
	 */
	@OnClick(R.id.set_avatar)
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
	/**
	 * ע���û�
	 * @param v
	 */
	@OnClick(R.id.btn_regist_regist)
	public void registUser(View v){
		//1)�п�
		if(isEmpty(etUsername,etPassword,etRePassword)){
			return;
		}
		//2)���������Ƿ�һ��
		if(!etPassword.getText().toString().equals(etRePassword.getText().toString())){
			toast("�����������벻һ�£�����������");
			etRePassword.setText("");
			etPassword.setText("");
			return;
		}
		//3)������û������
		if(!BmobNetUtil.isNetworkAvailable(this)){
			toast("���粻���ã�������������");
			return;
		}
		//4)��������ע��
		
		//�ı�cpbRegist��ť��״̬��������״̬--->������״̬
		
		cpbRegist.setIndeterminateProgressMode(true);
		cpbRegist.setProgress(50);
		
		final User user = new User();
		user.setUsername(etUsername.getText().toString());
		user.setPassword(etPassword.getText().toString());//MD5���ܣ������
		if(rgGender.getCheckedRadioButtonId()==R.id.rb_regist_boy){
			user.setGender(true);
		}else{
			user.setGender(false);
		}
		//pyname
		user.setPyname(PinYinUtil.getPinYin(user.getUsername()));
		user.setSortLetter(user.getPyname().charAt(0));
		//�趨��ǰ�û�ʹ�õ��豸���ͣ�ֻ�����롰android����ios��
		user.setDeviceType("android");
		//�ѵ�ǰ�豸��ID���û�����һ����
		user.setInstallId(BmobInstallation.getInstallationId(this));
		//���������������ע��
		user.signUp(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				//cpbReigst��������״̬--->���н����ɹ�״̬
				cpbRegist.setProgress(100);
				//1)Ҫ���豸��(_Installation)�У�����ǰ��¼�û����û������豸ID�ٽ���һ�ΰ�
				//bindInstallationForRegister(user.getUsername())�÷���ִ��Ҳ����ζ������ˡ���¼������
				bmobUserManager.bindInstallationForRegister(user.getUsername());
				//2)�����û���λ��(��MyApp.lastPoint�����û���point���Ե�ֵ)
				updateUserLocation(user);
				//3)�û�ͷ��İ�
				user.setAvatar(avatarUrl);
				//4)������ת��MainActivity
				jump(LoginActivity.class,true);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				//cpbRegist��������״̬-->���н���ʧ��״̬
				cpbRegist.setProgress(-1);
				new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
					
					@Override
					public void run() {
						cpbRegist.setProgress(0);
					}
				}, 1500);
				
				switch (arg0) {
				case 202:
				case 401:
					toastAndLog("���û����Ѵ���", arg0+": "+arg1);
					break;

				default:
					toastAndLog("ע��ʧ�ܣ��Ժ�����", arg0+": "+arg1);
					break;
				}
				
			}
		});
	}
	
	/**
	 * ���ϵͳ��BACK������ø÷���
	 */
	@Override
	public void onBackPressed() {
//		super.onBackPressed();
		jump(LoginActivity.class, true);
	}
	

}
