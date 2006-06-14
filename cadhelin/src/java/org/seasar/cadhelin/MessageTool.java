package org.seasar.cadhelin;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.util.ResourceBundleUtil;
import org.seasar.framework.util.ResourceUtil;

public class MessageTool {
	public static String MESSAGE_KEY = "org.seasar.cadhelin.messages";
    private ResourceBundle bundle;
	private HttpServletRequest request;
    public MessageTool(String prefix){
    	bundle = MessageResources.getResourceBundle(prefix);    	
    }
    public void setRequest(HttpServletRequest request){
    	this.request = request;
    }
    public String getMessage(String key){
    	Map msgs = (Map) request.getAttribute(MESSAGE_KEY);
    	if(msgs==null){
    		return null;
    	}
    	Message msg = (Message) msgs.get(key);
    	if(msg==null){
    		return null;
    	}
    	String message = ResourceBundleUtil.getString(bundle,msg.getKey(),msg.getKey());
    	if(msg.getArguments()==null){
    		return message;
    	}
    	for (Entry<String,String> e : msg.getArguments().entrySet()) {
    		String k = "${" + e.getKey() +"}";
    		message = message.replace(k,e.getValue().toString());
    	}
    	return message;
    }
    public String getErrors(){
    	StringBuffer buff = new StringBuffer();
    	Map<String,Message> msgs = (Map<String,Message>) request.getAttribute(MESSAGE_KEY);
    	if(msgs.size()==0){
    		return null;
    	}
    	String header = ResourceBundleUtil.getString(bundle,"errors.header","");
    	String hooter = ResourceBundleUtil.getString(bundle,"errors.hooter","");
    	for (String key : msgs.keySet()) {
			buff.append(getError(key));
		}
    	return header + buff.toString() +hooter;
    }
    public String getError(String key){
    	String message = getMessage(key);
    	if(message==null){
    		return  null;
    	}
    	String header = ResourceBundleUtil.getString(bundle,"error.header","");
    	String hooter = ResourceBundleUtil.getString(bundle,"error.hooter","");
    	return header+message+hooter;
    }
}
