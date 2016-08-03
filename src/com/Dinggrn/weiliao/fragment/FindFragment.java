package com.Dinggrn.weiliao.fragment;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bmob.im.util.BmobNetUtil;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.adapter.BlogAdapter;
import com.Dinggrn.weiliao.bean.Blog;
import com.Dinggrn.weiliao.bean.Comment;
import com.Dinggrn.weiliao.bean.User;
import com.Dinggrn.weiliao.constant.Constant.Position;
import com.Dinggrn.weiliao.listener.OnPhotoViewListener;
import com.Dinggrn.weiliao.listener.OnSaveCommentListener;
import com.Dinggrn.weiliao.ui.BaseActivity;
import com.Dinggrn.weiliao.ui.PostBlogActivity;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class FindFragment extends BaseFragment implements OnSaveCommentListener,OnPhotoViewListener{
	@Bind(R.id.headerview)
	View headerView;
	
	@Bind(R.id.plv_find_fragment_blogs)
	PullToRefreshListView pulltorefreshListView;
	
	ListView listView;//pulltorefreshListView�ڲ���listView
	
	//����Դ
	List<Blog> blogs;
	//������
	BlogAdapter adapter;
	
	@Bind(R.id.ll_find_fragment_commentcontainer)
	LinearLayout commentContainer;//����ʢ���������ۺͷ��Ͱ�ť��LinearLayout
	
	@Bind(R.id.et_find_fragment_comment)
	EditText etComment;
	
	@Bind(R.id.btn_find_fragment_send)
	Button btnSendComment;
	
	int pos;//������������¼�û��������Listview����һ��blog
	
	
	@Bind(R.id.rl_find_fragment_photoviewcontainer)
	RelativeLayout photoviewContainer;
	
	@Bind(R.id.pv_find_fragment_photoview)
	PhotoView photoView;
	
	@Bind(R.id.iv_find_fragment_closephotoview)
	ImageView ivClose;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		View view  = inflater.inflate(R.layout.fragment_find, container,false);
		ButterKnife.bind(this, view);
		initHeaderView();
		initListView();
		return view;
	}


	private void initHeaderView() {
		setHeaderTitle(headerView, "����Ȧ", Position.CENTER);
		setHeaderImage(headerView, R.drawable.ic_new_blog, Position.RIGHT, new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//������������͡���ť��������ת
				jump(PostBlogActivity.class, false);
			}
		});
	}

	private void initListView() {
		listView = pulltorefreshListView.getRefreshableView();	
		blogs = new ArrayList<Blog>();
		adapter = new BlogAdapter(getActivity(), blogs,this,this);
		listView.setAdapter(adapter);
		
		pulltorefreshListView.setMode(Mode.PULL_FROM_START);
		pulltorefreshListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				refresh();
			}
			
		});
	}
	
	@Override
	public void onResume() {
		super.onResume();
		refresh();
	}

	protected void refresh() {
		//ȥ������������ѯ���е�blog��Ȼ��ŵ�ListView����ʾ
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.include("user");//Ҫ��Bmobȥ_User���в�ѯblog���ߵ���ϸ��Ϣ
		query.order("-createdAt");//���ݴ���ʱ����е�����
		query.findObjects(getActivity(), new FindListener<Blog>() {
			
			@Override
			public void onSuccess(List<Blog> arg0) {
				pulltorefreshListView.onRefreshComplete();
				adapter.addAll(arg0, true);
			}
			
			@Override
			public void onError(int arg0, String arg1) {
				pulltorefreshListView.onRefreshComplete();
				//TODO Log
			}
		});
		
	}


	@Override
	public void saveComment(int position) {
		if(commentContainer.getVisibility()==View.VISIBLE){
			commentContainer.setVisibility(View.GONE);
			this.pos = -1;
		}else{
			commentContainer.setVisibility(View.VISIBLE);
			this.pos = position;
		}
		
	}
	
	@OnClick(R.id.btn_find_fragment_send)
	public void sendComment(View v){
		//����һ�����۶��󣬱��浽Bmob������
		if(TextUtils.isEmpty(etComment.getText().toString())){
			return;
		}
		if(!BmobNetUtil.isNetworkAvailable(getActivity())){
			((BaseActivity)getActivity()).toast("��ǰ���粻����");
			return;
		}
		Comment comment = new Comment();
		comment.setUser(bmobUserManager.getCurrentUser(User.class));
		comment.setUsername(bmobUserManager.getCurrentUserName());
		comment.setContent(etComment.getText().toString());
		comment.setBlogId(adapter.getItem(pos).getObjectId());
		
		comment.save(getActivity(), new SaveListener() {
			
			@Override
			public void onSuccess() {
				refresh();
				listView.setSelection(pos);
				etComment.setText("");
				commentContainer.setVisibility(View.GONE);
			}
			
			@Override
			public void onFailure(int arg0, String arg1) {
				
				((BaseActivity)getActivity()).toast("����ʧ�ܣ�������");
			}
		});
		
	}


	@Override
	public void onPhotoView(String url) {
		if(photoviewContainer.getVisibility()!=View.VISIBLE){
			photoviewContainer.setVisibility(View.VISIBLE);
			ImageLoader.getInstance().displayImage(url, photoView, new ImageLoadingListener() {
				ProgressDialog pd = ProgressDialog.show(getActivity(), "", "ͼ�������...");
				@Override
				public void onLoadingStarted(String imageUri, View view) {
					photoView.setImageResource(R.drawable.ic_loading);
					pd.show();
				}
				
				@Override
				public void onLoadingFailed(String imageUri, View view,
						FailReason failReason) {
					//TODO
				}
				
				@Override
				public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
					pd.dismiss();
					photoView.setImageBitmap(loadedImage);
				}
				
				@Override
				public void onLoadingCancelled(String imageUri, View view) {
					// TODO Auto-generated method stub
					
				}
			});
		
		
		
		
		
		
		}
		
	}
	
	@OnClick(R.id.iv_find_fragment_closephotoview)
	public void closePhotoView(View v){
		photoviewContainer.setVisibility(View.INVISIBLE);
	}
	
}
