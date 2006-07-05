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
	public static String ERROR_KEY = "org.seasar.cadhelin.errors";
	private String messageKey;
	private String messagePrefix = "";
	private String messageSuffix = "";
	private String messagesPrefix = "";
	private String messagesSuffix = "";
    private ResourceBundle bundle;
	private HttpServletRequest request;
    public MessageTool(String prefix,String messageKey){
    	this(prefix,messageKey,null,null);
    }
    public MessageTool(String prefix,String messageKey,String messageAppendKey,String messagesAppendKey){
    	bundle = MessageResources.getResourceBundle(prefix);
    	this.messageKey = messageKey;
    	if(messageAppendKey!=null){
    		this.messagePrefix = ResourceBundleUtil.getString(bundle,messageAppendKey + ".header","");
    		this.messageSuffix = ResourceBundleUtil.getString(bundle,messageAppendKey + ".footer","");
    	}
    	if(messagesAppendKey!=null){
    		this.messagesPrefix = ResourceBundleUtil.getString(bundle,messagesAppendKey + ".header","");
    		this.messagesSuffix = ResourceBundleUtil.getString(bundle,messagesAppendKey + ".footer","");
    	}
    }
    public void setRequest(HttpServletRequest request){
    	this.request = request;
    }
    public boolean hasMessage(String key){
    	Map msgs = (Map) request.getAttribute(messageKey);
    	if(msgs==null){
    		return false;
    	}
    	Message msg = (Message) msgs.get(key);
    	return msg != null;    	
    }
    private String getString(String key){
    	Map msgs = (Map) request.getAttribute(messageKey);
    	if(msgs==null){
    		return null;
    	}
    	Message msg = (Message) msgs.get(key);
    	if(msg==null){
    		return null;
    	}
    	String message = ResourceBundleUtil.getStringRecursive(bundle, msg.getKey(),msg.getKey());
    	if(msg.getArguments()==null){
    		return message;
    	}
    	for (Entry<String,Object> e : msg.getArguments().entrySet()) {
    		String k = "${" + e.getKey() +"}";
    		message = message.replace(k,e.getValue().toString());
    	}
    	return message;
    }
    public InsertAsIs getMessage(String key){
    	String message = getString(key);
    	return (message!=null)?new InsertAsIs(message):null;
    }
    @SuppressWarnings("unchecked")
	public InsertAsIs getMessages(){
    	StringBuffer buff = new StringBuffer();
    	Map<String,Message> msgs = (Map) request.getAttribute(ERROR_KEY);
    	if(msgs == null || msgs.size()==0){
    		return null;
    	}
    	for (String key : msgs.keySet()) {
			buff.append(getMessageString(key));
		}
    	return new InsertAsIs(messagesPrefix + buff.toString() +messagesSuffix);
    }
    private String getMessageString(String key){
    	String message = getString(key);
    	if(message==null){
    		return  null;
    	}
    	return messagePrefix+message+messageSuffix;
    }
    public InsertAsIs getError(String key){
    	String message = getMessageString(key);
    	return (message!=null)?new InsertAsIs(message):null;
    }
}
