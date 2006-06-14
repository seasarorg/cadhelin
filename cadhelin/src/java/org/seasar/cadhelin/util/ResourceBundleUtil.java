package org.seasar.cadhelin.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleUtil {
	public static String getString(ResourceBundle bundle,String key,String defaultStr){
		try {
			return bundle.getString(key);
		} catch (MissingResourceException e) {
			return defaultStr;
		}
	}
}
