package com.Dinggrn.weiliao.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.bean.Blog;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.view.NumberProgressBar;
import com.bmob.BmobProFile;
import com.bmob.btp.callback.UploadBatchListener;

public class PostBlogActivity extends BaseActivity {
	@Bind(R.id.headerview)
	View headerView;//ͷ����
	@Bind(R.id.et_postblog_content)
	EditText etContent;//����blog�ı�����

	@Bind(R.id.iv_postblog_blogimg0)
	ImageView ivBlogImg0;//��ʾ��һ��blog��ͼ
	@Bind(R.id.iv_postblog_blogimg1)
	ImageView ivBlogImg1;//��ʾ�ڶ���blog��ͼ
	@Bind(R.id.iv_postblog_blogimg2)
	ImageView ivBlogImg2;//��ʾ������blog��ͼ
	@Bind(R.id.iv_postblog_blogimg3)
	ImageView ivBlogImg3;//��ʾ������blog��ͼ

	@Bind(R.id.iv_postblog_delimg0)
	ImageView ivDelImg0;//ɾ����һ��blog��ͼ��С���
	@Bind(R.id.iv_postblog_delimg1)
	ImageView ivDelImg1;//ɾ���ڶ���blog��ͼ��С���
	@Bind(R.id.iv_postblog_delimg2)
	ImageView ivDelImg2;//ɾ��������blog��ͼ��С���
	@Bind(R.id.iv_postblog_delimg3)
	ImageView ivDelImg3;//ɾ��������blog��ͼ��С���

	@Bind(R.id.tv_postblog_picnumber)
	TextView tvPicNumber;//��ʾ�Ѿ�����˶��ٸ�ͼƬ
	@Bind(R.id.npb_postblog_progressbar)
	NumberProgressBar npbProgressBar;//�ϴ�blog�Ľ���

	@Bind(R.id.btn_postblog_plus)
	ImageButton ibPlus;//�ӺŰ�ť

	@Bind(R.id.btn_postblog_addpicture)
	ImageButton ibPicture;//���ͼƬ��ť
	@Bind(R.id.btn_postblog_addphoto)
	ImageButton ibCamera;//���հ�ť
	@Bind(R.id.btn_postblog_addlocation)
	ImageButton ibLocation;//���λ�ð�ť

	List<ImageView> ivBlogImgs;//����װ���ĸ���ʾblog��ͼ��IamgeView
	List<ImageView> ivDelImgs;//����װ���ĸ�ɾ��blog��ͼ��IamgeView

	boolean isExpanded;//��ǰͼƬ����λ����������ť�Ƿ�ɼ�
	boolean isAniming;//ͼƬ����λ����������ť�Ƿ��ڱ仯�Ķ�����
	boolean isPosting;//��ǰ�Ƿ������ϴ�Blog
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_post_blog);
		ButterKnife.bind(this);
		initView();
		initHeaderView();

	}

	private void initHeaderView() {
		setHeaderTitle(headerView, " ", Position.CENTER);
		setHeaderImage(headerView, R.drawable.back_arrow_2, Position.LEFT, new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();

			}
		});

		setHeaderImage(headerView, R.drawable.ic_upload,Position.RIGHT, new OnClickListener() {

			@Override
			public void onClick(View v) {
				//�ϴ�blog
				if(isPosting){
					return;
				}
				if(!TextUtils.isEmpty(etContent.getText().toString())||ivBlogImg0.getVisibility()==View.VISIBLE){
					isPosting = true;
					postPicture();//��ʼ�ϴ�blog����ͼ
				}else{
					toast("����ҷ���Щ���ݰ�^.^");
				}

			}
		});
	}

	/**
	 * �ϴ�blog����ͼ
	 */
	protected void postPicture() {
		if(ivBlogImg0.getVisibility()!=View.VISIBLE){
			postContent("");
			return;
		}
		
		List<String> paths = new ArrayList<String>();
		for(int i=0;i<ivBlogImgs.size();i++){
			if(ivBlogImgs.get(i).getVisibility()==View.VISIBLE){
				paths.add((String) ivBlogImgs.get(i).getTag());
			}
		}
		//��õ�ǰ����blog��ͼ�ڱ����豸�ϵĵ�ַ
		String[] filePaths = paths.toArray(new String[paths.size()]);
		//��������ʾ
		npbProgressBar.setVisibility(View.VISIBLE);
		npbProgressBar.setProgress(0);
		tvPicNumber.setText("��ʼ�ϴ�");
		
		BmobProFile.getInstance(this).uploadBatch(filePaths, new UploadBatchListener() {
			
			@Override
			public void onError(int arg0, String arg1) {
				// TODO 
				
			}
			/**
			 * ÿ�ϴ�һ��ͼƬ���������ɹ���onSuccess���ᱻ�ص�һ��
			 * ֻ������ÿ�λ���ʱ��arg0���ᴫ��false��ֻ�����һ��ͼҲ�ϴ��ɹ��ˣ�arg0�Ż���true
			 * arg1:�ϴ���ÿһ��ͼ����ļ���
			 * arg2:�ϴ���ÿһ��ͼ���ڷ������ϵ�url����url����ֱ��ʹ�ã���Ҫ����MD5���ܲſ���
			 * arg3:BmobFile[]����������˿���ֱ��ʹ�õģ�ͼƬ��Bmob�������ϵĵ�ַ
			 */
			@Override
			public void onSuccess(boolean arg0, String[] arg1, String[] arg2,
					BmobFile[] arg3) {
				if(arg0==true){
					//���е�ͼƬ���ϴ��ɹ���
					StringBuilder sb = new StringBuilder();
					for(int i=0;i<arg3.length;i++){
						sb.append(arg3[i].getUrl()).append("&");
					}
					//��������ϴ��ɹ���ͼ���ַƴ�Ӷ��ɵ��ַ���
					String urls = sb.substring(0,sb.length()-1);
					npbProgressBar.setVisibility(View.INVISIBLE);
					postContent(urls);
				}
				
			}
			/**
			 * arg0:��ʾ��ǰ�ǵڼ����ļ����ϴ�
			 * arg1:�ǵ�ǰ����ļ��ϴ��Ľ���
			 * arg2:��ʾһ��Ҫ�ϴ����ٸ��ļ�
			 * arg3:�ϴ����������
			 */
			@Override
			public void onProgress(int arg0, int arg1, int arg2, int arg3) {
				npbProgressBar.setProgress(arg3); 
			}
		});
		
	}

	/**
	 * ��һ��������Blog���󱣴浽Bmob������
	 * @param urls
	 */
	private void postContent(String urls) {
		 Blog blog = new Blog();
		 blog.setUser(bmobUserManager.getCurrentUser(User.class));
		 blog.setImages(urls);
		 blog.setContent(etContent.getText().toString());
		 blog.setLove(0);
		 
		 blog.save(this, new SaveListener() {
			
			@Override
			public void onSuccess() {
				toast("�����ѷ���");
				isPosting = false;
				finish();
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				toastAndLog("����ʧ�ܣ�������", arg0+": "+arg1);
				isPosting = false;
			}
		});
	}

	/**
	 * ��ʼ����ͼ
	 */
	private void initView() {
		ivBlogImgs = new ArrayList<ImageView>();
		ivBlogImgs.add(ivBlogImg0);
		ivBlogImgs.add(ivBlogImg1);
		ivBlogImgs.add(ivBlogImg2);
		ivBlogImgs.add(ivBlogImg3);

		ivDelImgs = new ArrayList<ImageView>();
		ivDelImgs.add(ivDelImg0);
		ivDelImgs.add(ivDelImg1);
		ivDelImgs.add(ivDelImg2);
		ivDelImgs.add(ivDelImg3);

		for(int i=0;i<4;i++){
			ivBlogImgs.get(i).setVisibility(View.INVISIBLE);
			ivDelImgs.get(i).setVisibility(View.INVISIBLE);
		}

		tvPicNumber.setText("");

		npbProgressBar.setVisibility(View.INVISIBLE);

		ibPicture.setVisibility(View.INVISIBLE);
		ibCamera.setVisibility(View.INVISIBLE);
		ibLocation.setVisibility(View.INVISIBLE);
	}

	/**
	 * ������Ӻš���ť���л�����������ť�ɼ����ǲ��ɼ�
	 * @param v
	 */
	@OnClick(R.id.btn_postblog_plus)
	public void switchButtons(View v){
		//��������ڰ�ť�仯�Ķ����У��������Ӻš���ť��Ч
		if(isAniming){
			return;
		}

		if(isExpanded){
			closeButtons();
		}else{
			expandButtons();
		}
	}

	private void closeButtons() {
		// ͼƬ����λ�ð�ť���ɼ�
		isAniming = true;

		Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.click_button_anim);
		ibPlus.startAnimation(clickAnim);

		Animation closeAnim = AnimationUtils.loadAnimation(this, R.anim.close_button_anim);
		ibPicture.startAnimation(closeAnim);
		ibCamera.startAnimation(closeAnim);
		ibLocation.startAnimation(closeAnim);

		closeAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAniming = false;
				isExpanded = false;
				ibPicture.setVisibility(View.INVISIBLE);
				ibCamera.setVisibility(View.INVISIBLE);
				ibLocation.setVisibility(View.INVISIBLE);
			}
		});
	}

	private void expandButtons() {
		// ͼƬ����λ�ð�ť�ɼ�
		isAniming = true;

		Animation clickAnim = AnimationUtils.loadAnimation(this, R.anim.click_button_anim);
		ibPlus.startAnimation(clickAnim);

		Animation expandAnim = AnimationUtils.loadAnimation(this, R.anim.expand_button_anim);
		ibPicture.setVisibility(View.VISIBLE);
		ibCamera.setVisibility(View.VISIBLE);
		ibLocation.setVisibility(View.VISIBLE);

		ibPicture.startAnimation(expandAnim);
		ibCamera.startAnimation(expandAnim);
		ibLocation.startAnimation(expandAnim);

		expandAnim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAniming = false;
				isExpanded = true;
			}
		});

	}

	@OnClick(R.id.btn_postblog_addpicture)
	public void addPicture(View v){
		Intent intent = new Intent(Intent.ACTION_PICK);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		startActivityForResult(intent, 101);
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		if(arg1==RESULT_OK){
			if(arg0==101){
				Uri uri = arg2.getData();
				Cursor c = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
				c.moveToNext();
				String path = c.getString(0);
				c.close();
				showBlogImage(path);
			}
			//TODO 
		}
	}

	/**
	 * ��pathλ�õ�ͼƬ���ŵ���ʾblog��ͼ��ImageView����ʾ
	 * @param path Ҫ��ʾ��ͼƬ���豸�ϵĴ洢λ��
	 */
	private void showBlogImage(String path) {
		for(int i=0;i<ivBlogImgs.size();i++){
			if(ivBlogImgs.get(i).getVisibility()!=View.VISIBLE){
				Bitmap bitmap = BitmapFactory.decodeFile(path);
				ivBlogImgs.get(i).setVisibility(View.VISIBLE);
				ivBlogImgs.get(i).setImageBitmap(bitmap);
				ivBlogImgs.get(i).setTag(path);//��ͼƬ��·�����洢����ImageView��tag������
				ivDelImgs.get(i).setVisibility(View.VISIBLE);//��ImageView��Ӧ��С�����ʾ
				tvPicNumber.setText(i + 1 + " / 4");//�޸��Ѿ���ʾ��ͼƬ������
				return;
			}
		}
		toast("���ֻ������ĸ�ͼƬŶ^.^");
	}

	@OnClick({R.id.iv_postblog_delimg0,R.id.iv_postblog_delimg1,R.id.iv_postblog_delimg2,R.id.iv_postblog_delimg3})
	public void delBlogPic(View v){
		switch (v.getId()) {
		case R.id.iv_postblog_delimg0:
			delImage(0);
			break;
		case R.id.iv_postblog_delimg1:
			delImage(1);
			break;
		case R.id.iv_postblog_delimg2:
			delImage(2);
			break;
		case R.id.iv_postblog_delimg3:
			delImage(3);
			break;
		}
	}
	
	/**
	 * ����ɾ��������ͼ�ķ���
	 * @param index
	 */
	private void delImage(int index) {
		int count = 0;//�ж�������һ���ж���ͼƬ
		
		for(int i=0;i<ivBlogImgs.size();i++){
			if(ivBlogImgs.get(i).getVisibility()==View.VISIBLE){
				count += 1;
			}
		}
		
		if(index == count-1){
			//��������ǡ�������һ����ͼ��С���
			ivBlogImgs.get(index).setVisibility(View.INVISIBLE);
			ivDelImgs.get(index).setVisibility(View.INVISIBLE);
		}else{
			//�����С��治�����һ����ͼ�ĺ��
			for(int i=index;i<count;i++){
				if(i==count-1){//����Ѿ��������һ��ͼ
					ivBlogImgs.get(i).setVisibility(View.INVISIBLE);
					ivDelImgs.get(i).setVisibility(View.INVISIBLE);
				}else{
					//û�����һ��ͼ��ʱ��
					//��ǰ��ImageView��ʾ��һ��imageView����ʾͼƬ��·��
					String path = (String) ivBlogImgs.get(i+1).getTag();
					ivBlogImgs.get(i).setTag(path);
					ivBlogImgs.get(i).setImageBitmap(BitmapFactory.decodeFile(path));
				}
			}
		}
		
		if(count == 1){
			tvPicNumber.setText("");
		}else{
			tvPicNumber.setText(count - 1 +" / 4");
		}
		
	}

}
