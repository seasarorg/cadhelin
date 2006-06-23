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

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.util.StringUtil;
import org.seasar.framework.container.ComponentDef;

public class ControllerMetadata {
	Log log = LogFactory.getLog(this.getClass());
	private String name;
	private Map<String,ActionMetadata[]> actions = 
		new HashMap<String,ActionMetadata[]>();
	private Map<Method,ActionMetadata> actionByMethods = 
		new HashMap<Method,ActionMetadata>();
	private ActionFilter[] filters = new ActionFilter[0];
	private String defaultActionName = null;
	public ControllerMetadata(
			String name,
			ComponentDef componentDef,
			ActionFilter[] filters){
		this.name = name;
		this.filters = filters;
	}
	protected void service(
			ControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		String actionName = context.getActionName();
		ActionMetadata[] metadata = null;
		if(!StringUtil.isNullOrEmpty(actionName)){
			metadata = actions.get(actionName);
		}else{
			metadata = actions.get(defaultActionName);
		}
		ActionMetadata metadatum = getActionMetadata(metadata,request.getMethod());
		if(metadatum==null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);			
		}
		FilterContextImpl filter = new FilterContextImpl(filters,context,metadatum);
		filter.doFilter(request,response);
	}
	private ActionMetadata getActionMetadata(ActionMetadata[] metadata,String method){
		if(metadata==null || metadata.length==0){
			return null;
		}
		for (ActionMetadata metadatum : metadata) {
			if(metadatum.getHttpMethod().name().equals(method)){
				return metadatum;
			}
		}
		return null;
	}
	public String convertToURL(Method method, Object[] arguments) {
		ActionMetadata metadata = actionByMethods.get(method);
		if(metadata!=null){
			return name+"/"+metadata.convertToURL(arguments);			
		}
		log.warn("cannot find ActionMetadata by " + method);
		return null;
	}
	public String convertToURL(String actionName, Object[] arguments) {
		ActionMetadata metadata = 
			getActionMetadata(actions.get(actionName),"GET");
		if(metadata!=null){
			return name+"/"+metadata.convertToURL(arguments);			
		}
		log.warn("cannot find ActionMetadata by " + actionName);
		return null;
	}
	public ActionMetadata getAction(String actionName,String method) {
		return getActionMetadata(actions.get(actionName),method);
	}
	public void addActionMetadata(String name, ActionMetadata metadata) {
		ActionMetadata[] metadatas = actions.get(name);
		if(metadatas==null){
			actions.put(name,new ActionMetadata[]{metadata});
		}else{
			ActionMetadata[] am = new ActionMetadata[metadatas.length+1];
			System.arraycopy(metadatas,0,am,0,metadatas.length);
			am[metadatas.length] = metadata;
			actions.put(name,am);
		}
		actionByMethods.put(metadata.getMethod(), metadata);
	}
	public void setDefaultActionName(String defaultActionName) {
		this.defaultActionName = defaultActionName;
	}
}
