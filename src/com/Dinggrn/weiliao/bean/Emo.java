package com.Dinggrn.weiliao.bean;
/**
 * ʵ���࣬������������
 * ÿһ���������һ��Emo����
 * @author pjy
 *
 */
public class Emo {
	//R.drawable.ue057 ���� 0x7fXXXXXX ��̫�׵�
	//��Ϊ��������ʽ�жϵ�ʱ�򣬲�̫��������
	//��Щ���û������е����֣���Щ�����˱���id
	//�����ue057��id����ĳЩ�����£�Ҳ�᲻���ж�
	//��Щ�������е���Ϣ����Щ�Ǳ���id
	String id;//[emo]ue057

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Emo(String id) {
		super();
		this.id = id;
	}

	public Emo() {
		super();
	}
	
}
