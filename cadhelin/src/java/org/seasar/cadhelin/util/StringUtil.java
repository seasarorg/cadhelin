/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
	public static String join(String[] strs, String delim, int len) {
		StringBuilder buff = new StringBuilder();
		for(int i=0;i<len && i < strs.length;i++){
			if(i>0){
				buff.append(delim);
			}
			buff.append(strs[i]);
		}
		return buff.toString();
	}
}
