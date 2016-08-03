package com.Dinggrn.weiliao.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
/**
 * ƴ��������
 * @author pjy
 *
 */
public class PinYinUtil {
	/**
	 * ��ָ�����ַ���ת��Ϊȫ��д�ĺ���ƴ����ʽ
	 * "����"--->"LAOWANG"
	 * "��a��"--->"WANGALAO"
	 * "abc" ---> "ABC"
	 * "a$b$c"--->"A#B#C"
	 * ������ȡ��һ�ֶ���
	 * @param string
	 * @return
	 */
	public static String getPinYin(String string){
		try {
			HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
			format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
			format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
			StringBuilder sb = new StringBuilder();
			for(int i=0;i<string.length();i++){
				String str = string.substring(i,i+1);
				if(str.matches("[\u4e00-\u9fff]")){
					char c = str.charAt(0);
					String[] pinyins = PinyinHelper.toHanyuPinyinStringArray(c, format);
					//����ж����ֵ������ֻȡ��һ�ֶ���
					sb.append(pinyins[0]);
				}else if(str.matches("[a-zA-Z]")){
					sb.append(str.toUpperCase());
				}else{
					sb.append("#");
				}
			}
			return sb.toString();
		} catch (BadHanyuPinyinOutputFormatCombination e) {
			throw new RuntimeException("����ȷ��ƴ����ʽ");
		}
	}
	
	/**
	 * ����ָ������תΪ����ƴ����ʽ�������ĸ
	 * @param string
	 * @return
	 */
	public static String getSortLetter(String string){
		return getPinYin(string).substring(0,1);
	}
}
