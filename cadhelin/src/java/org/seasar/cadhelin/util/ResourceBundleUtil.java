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

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleUtil {
	public static String getString(ResourceBundle bundle,String key,String defaultStr){
		try {
			return bundle.getString(key);
		} catch (NullPointerException e) {
			return defaultStr;
		} catch (MissingResourceException e) {
			return defaultStr;
		}
	}
	public static String getStringRecursive(ResourceBundle bundle,String key,String defaultStr){
		String[] strs = key.split("\\.");
		for(int i=strs.length;0<i;i--){
			String k = StringUtil.join(strs,".",i);
			try {
				return bundle.getString(k);
			} catch (NullPointerException e) {
			} catch (MissingResourceException e) {
			}
		}
		return defaultStr;
	}
}