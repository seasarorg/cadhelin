package org.seasar.cadhelin;

import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

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
    	System.out.println("message");
    	Map msgs = (Map) request.getAttribute(MESSAGE_KEY);
    	if(msgs==null){
    		return null;
    	}
    	Message msg = (Message) msgs.get(key);
    	if(msg==null){
    		return null;
    	}
		try {
			return bundle.getString(msg.getKey());
		} catch (MissingResourceException e) {
    		return msg.getKey();
		}
    }
}
