package com.Dinggrn.weiliao.bean;

import cn.bmob.v3.BmobObject;

public class Comment extends BmobObject{
	
	User user;//�����۵�����
	String username;//���������ߵ��û���
	String content;//�����۵�����
	String blogId;//�������������һ��blog������
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getBlogId() {
		return blogId;
	}
	public void setBlogId(String blogId) {
		this.blogId = blogId;
	}
	
	
}
