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
package org.seasar.cadhelin;

import java.util.Map;
import java.util.ResourceBundle;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.util.ResourceBundleUtil;
import org.seasar.cadhelin.velocity.InsertAsIs;

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
    public boolean hasMessage(String key){
    	Map msgs = (Map) request.getAttribute(MESSAGE_KEY);
    	if(msgs==null){
    		return false;
    	}
    	Message msg = (Message) msgs.get(key);
    	return msg != null;
    }
    
    private String getMessageString(String key){
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
    public InsertAsIs getMessage(String key){
    	String message = getMessageString(key);
    	return (message!=null)?new InsertAsIs(message):null;
    }
    @SuppressWarnings("unchecked")
	public InsertAsIs getErrors(){
    	StringBuffer buff = new StringBuffer();
    	Map<String,Message> msgs = (Map) request.getAttribute(MESSAGE_KEY);
    	if(msgs == null || msgs.size()==0){
    		return null;
    	}
    	String header = ResourceBundleUtil.getString(bundle,"errors.header","");
    	String hooter = ResourceBundleUtil.getString(bundle,"errors.footer","");
    	for (String key : msgs.keySet()) {
			buff.append(getErrorString(key));
		}
    	return new InsertAsIs(header + buff.toString() +hooter);
    }
    private String getErrorString(String key){
    	String message = getMessageString(key);
    	if(message==null){
    		return  null;
    	}
    	String header = ResourceBundleUtil.getString(bundle,"error.header","");
    	String hooter = ResourceBundleUtil.getString(bundle,"error.footer","");
    	return header+message+hooter;
    }
    public InsertAsIs getError(String key){
    	String message = getErrorString(key);
    	return (message!=null)?new InsertAsIs(message):null;
    }
}
