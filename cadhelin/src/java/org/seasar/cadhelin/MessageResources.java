package org.seasar.cadhelin;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.seasar.framework.util.ResourceBundleUtil;

public class MessageResources {
	private static MessageResources instance = new MessageResources();
	public static ResourceBundle getResourceBundle(String key){
		return instance.getResourceBundles(key);
	}
    private Map<String,ResourceBundle> bundleMap =
    	new HashMap<String,ResourceBundle>();
    private MessageResources() {
	}
	public ResourceBundle getResourceBundles(String key) {
		if(bundleMap.containsKey(key)){
			return bundleMap.get(key);
		}
		ResourceBundle resourceBundle = ResourceBundleUtil.getBundle(key,Locale.getDefault());
		bundleMap.put(key,resourceBundle);
		return resourceBundle;
    }

}
