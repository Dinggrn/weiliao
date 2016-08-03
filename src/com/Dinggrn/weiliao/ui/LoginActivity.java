package com.Dinggrn.weiliao.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.util.BmobNetUtil;
import cn.bmob.v3.listener.SaveListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.bean.User;
import com.dd.CircularProgressButton;

public class LoginActivity extends BaseActivity {

	@Bind(R.id.headerview)
	View headerview;//������HeaderView

	@Bind(R.id.et_uname)
	EditText etUsername;//�����û���

	@Bind(R.id.et_upwd)
	EditText etPassword;//��������

	@Bind(R.id.btn_login_login)
	CircularProgressButton btLogin;//��¼��ť

	@Bind(R.id.tv_login_regist)
	TextView tvRegist;//ע��

	long firstPress;//��¼��һ�ΰ��¡�back������ʱ���

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ButterKnife.bind(this);
//		initHeaderView();
	}

	/**
	 * ����headerView�е�����
	 */
	private void initHeaderView() {
		setHeaderTitle(headerview, "��ӭʹ��");
		//�Ƿ�����ͼ��ͼ����ʲô��ɫ���Լ����Ű�
	}

	@OnClick(R.id.tv_login_regist)
	public void toRegist(View v){
		jump(RegisterActivity.class,true);
	}

	@OnClick(R.id.btn_login_login)
	public void doLogin(View v){
		//1) �п�
		if(isEmpty(etPassword,etUsername)){
			return;
		}
		//2) ����
		if(!BmobNetUtil.isNetworkAvailable(this)){
			toast("��ǰ���粻����");
			return;
		}
		//3) ��������ע�ᣬ����ProgressButton��״̬
		btLogin.setIndeterminateProgressMode(true);
		btLogin.setProgress(50);
		// 3.1) �����û�������û��������룬����һ��BmobChatUser����
		BmobChatUser user = new BmobChatUser();
		user.setUsername(etUsername.getText().toString());
		user.setPassword(etPassword.getText().toString());
		// 3.2) BmobUserManager�е�login�������е�¼����3.1�����Ķ����룬ͬʱ�ṩһ��������
		bmobUserManager.login(user, new SaveListener() {

			@Override
			public void onSuccess() {
				// 3.3) �����¼�ɹ�������ProgressButton��״̬��Ҫ�����û���λ�ã�Ҫ��ת���浽MainActivity
				btLogin.setProgress(100);
				updateUserLocation(bmobUserManager.getCurrentUser(User.class));
				jump(MainActivity.class, true);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// 3.4) �����¼ʧ�ܣ�����ProgressButton��״̬��Ҫ���û���ʾ��Ϣ��Խ׼ȷԽ�ã�
				btLogin.setProgress(-1);
				new Handler().postDelayed(new Runnable() {

					@Override
					public void run() {
						btLogin.setProgress(0);
					}
				}, 1500);

				switch (arg0) {
				case 101:
					toastAndLog("�û������������", arg0+": "+arg1);
					break;

				default:
					toastAndLog("��¼ʧ��", arg0+": "+arg1);
					break;
				}

			}
		});
	}

	/**
	 * ���ΰ�back���˳�
	 */

	@Override
	public void onBackPressed() {

		if(firstPress+2000>System.currentTimeMillis()){
			super.onBackPressed();
		}else{
			firstPress = System.currentTimeMillis();
			toast("����ٰ�һ����");
		}

	}


}
