package com.Dinggrn.weiliao.bean;

import cn.bmob.v3.BmobObject;

public class Blog extends BmobObject {
	
	User user;//blog������
	String images;//��ƪblog��ͼ�ĵ�ַ
	String content;//��ƪblog���ı�����
	int love;//��ƪblog��õġ��ޡ�������
	
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getImages() {
		return images;
	}
	public void setImages(String images) {
		this.images = images;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getLove() {
		return love;
	}
	public void setLove(int love) {
		this.love = love;
	}
	
}
