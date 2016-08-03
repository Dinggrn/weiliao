package com.Dinggrn.weiliao.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;

import com.Dinggrn.weiliao.R;
import com.Dinggrn.weiliao.bean.User;
import com.nostra13.universalimageloader.core.ImageLoader;


public class FriendAdapter extends MyBaseAdapter<User> implements SectionIndexer{
	
	public FriendAdapter(Context context, List<User> friends) {
		super(context,friends);
	}
	
	@Override
	public View getItemView(int position, View convertView, ViewGroup parent) {
		
		ViewHolder vh;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.item_friend_layout, parent,false);
			vh = new ViewHolder(convertView);
			convertView.setTag(vh);
		}else{
			vh = (ViewHolder) convertView.getTag();
		}
		
		User user = getItem(position);
		//��ʾͷ��
		if(TextUtils.isEmpty(user.getAvatar())){
			vh.ivAvatar.setImageResource(R.drawable.ic_launcher);
		}else{
			ImageLoader.getInstance().displayImage(user.getAvatar(), vh.ivAvatar);
		}
		//��ʾ�û���
		vh.tvUsername.setText(user.getUsername());
		
		//�����ķ�����ĸ�Ƿ���ʾ
		if(position==getPositionForSection(
				     getSectionForPosition(
						position))){
			//��ʾ
			vh.tvSortLetter.setVisibility(View.VISIBLE);
			vh.tvSortLetter.setText(user.getSortLetter());
		}else{
			//����ʾ
			vh.tvSortLetter.setVisibility(View.GONE);
		}
		
		return convertView;
	}

	
	public class ViewHolder{
		
		@Bind(R.id.tv_item_friend_index)
		TextView tvSortLetter;
		@Bind(R.id.iv_item_friend_avatar)
		ImageView ivAvatar;
		@Bind(R.id.tv_item_frined_usernmae)
		TextView tvUsername;
		
		public ViewHolder(View convertView) {
			
			ButterKnife.bind(this,convertView);
		
		}
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPositionForSection(int section) {
		for(int i=0;i<getCount();i++){
			if(section==getSectionForPosition(i)){
				return i;
			}
		}
		return -1;
	}
    
	@Override
	public int getSectionForPosition(int position) {
		//positionλ�õ�User��sortLetter��������Ӧ��char
		return getItem(position).getSortLetter();
	}






}
