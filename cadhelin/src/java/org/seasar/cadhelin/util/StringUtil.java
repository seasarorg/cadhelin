package org.seasar.cadhelin.util;

public class StringUtil {
	public static String convertToString(Object value){
		StringBuffer buff = new StringBuffer();
		if (value instanceof Object[]) {
			Object[] val = (Object[]) value;
			buff.append("[");
			for (int i = 0; i < val.length; i++) {
				if(i>0){buff.append(",");}
				buff.append(convertToString(val[i]));
			}
			buff.append("]");
		}else{
			buff.append(value);
		}
		return buff.toString();
	}
	public static boolean isNullOrEmpty(String str){
		return str == null || str.length() == 0;
	}
	public static String toLowwerCaseInitial(String str){
		if(str.length()==0){
			return str;
		}
		return Character.toLowerCase(str.charAt(0)) + str.substring(1);
	}
}
