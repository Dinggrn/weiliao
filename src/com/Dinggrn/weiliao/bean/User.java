package com.Dinggrn.weiliao.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class User extends BmobChatUser{
	String pyname;//username�Ĵ�дƴ����ʽ
	char sortLetter;//pyname������ĸ�Ĵ�д
	BmobGeoPoint point;//�û����һ�ε�¼ʱ������λ��
	Boolean gender;//�Ա� true ��  false Ů
	
	public String getPyname() {
		return pyname;
	}
	public void setPyname(String pyname) {
		this.pyname = pyname;
	}
	public char getSortLetter() {
		return sortLetter;
	}
	public void setSortLetter(char sortLetter) {
		this.sortLetter = sortLetter;
	}
	public BmobGeoPoint getPoint() {
		return point;
	}
	public void setPoint(BmobGeoPoint point) {
		this.point = point;
	}
	public Boolean getGender() {
		return gender;
	}
	public void setGender(Boolean gender) {
		this.gender = gender;
	}
	
	
	
}
