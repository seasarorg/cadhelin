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
}
