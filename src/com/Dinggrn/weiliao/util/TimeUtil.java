package com.Dinggrn.weiliao.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
	/**
	 * �������������תΪ�����Ե�ʱ��ֵ
	 * ���������Ϣ����ʱ���뵱ǰʱ������һ�����ڣ���ֱ����ʾhh:mm������15:35
	 * ���������Ϣ����ʱ�������죬����ʾ������ hh:mm������������ 15:35
	 * ���������Ϣ����ʱ����ǰ�죬����ʾ��ǰ�� hh:mm��������ǰ�� 15:35
	 * ���������Ϣ������ʱ����磬����ʾ��yyy/MM/dd HH:mm�� ����2016/4/15 15:35
	 * @param seconds �����ʱ�����ע���ʱ�����ֵΪ�룬���Ǻ���
	 * @return
	 */
	public static String getTime(long seconds){

		StringBuilder sb = new StringBuilder();
		long now = System.currentTimeMillis();//13λ��������ֵ
		int span = (int) (now/1000/24/3600-seconds/24/3600);
		//span = (int)(now-seconds*1000)/1000/24/3600
		
		SimpleDateFormat spf = new SimpleDateFormat("HH:mm");
		
		switch (span) {
		case 0:
			sb.append(spf.format(new Date(seconds*1000)));
			break;
		case 1:
			sb.append("���� ").append(spf.format(new Date(seconds*1000)));
			break;
		case 2:
			sb.append("ǰ�� ").append(spf.format(new Date(seconds*1000)));
			break;
		default:
			SimpleDateFormat spf2 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
			sb.append(spf2.format(new Date(seconds*1000)));
			break;
		}

		return sb.toString();
	}

}
