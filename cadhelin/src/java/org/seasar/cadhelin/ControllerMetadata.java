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

import java.beans.XMLEncoder;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.annotation.Dispatch;
import org.seasar.cadhelin.impl.FilterContextImpl;
import org.seasar.cadhelin.impl.InternalControllerContext;
import org.seasar.cadhelin.util.StringUtil;
import org.seasar.framework.container.ComponentDef;

public class ControllerMetadata {
	Log log = LogFactory.getLog(this.getClass());
	private Class controllerClass;
	private String name;
	private Map<String,ActionMetadata[]> actions = 
		new HashMap<String,ActionMetadata[]>();
	private Map<String,ActionMetadata> actionByMethodName = 
		new HashMap<String,ActionMetadata>();
	private ActionFilter[] filters = new ActionFilter[0];
	private String defaultActionName = null;
	public ControllerMetadata(
			String name,
			ComponentDef componentDef,
			ActionFilter[] filters){
		this.controllerClass = componentDef.getComponentClass();
		this.name = name;
		this.filters = filters;
	}
	protected void service(
			InternalControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) throws Throwable {
		String actionName = context.getActionName();
		ActionMetadata[] metadata = null;
		if(!StringUtil.isNullOrEmpty(actionName)){
			metadata = actions.get(actionName);
		}else{
			metadata = actions.get(defaultActionName);
		}
		ActionMetadata metadatum = getActionMetadata(request,metadata,request.getMethod());
		if(metadatum==null){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}else{
			FilterContextImpl filter = new FilterContextImpl(filters,context,metadatum);
			filter.doFilter(request,response);			
		}
	}
	public String getName() {
		return name;
	}
	private ActionMetadata getActionMetadata(HttpServletRequest request,ActionMetadata[] metadata,String method){
		if(metadata==null || metadata.length==0){
			return null;
		}
		for (ActionMetadata metadatum : metadata) {
			if(metadatum.getHttpMethod().name().equals(method) && 
					metadatum.getDispatch()!=null){
				Dispatch dispatch = metadatum.getDispatch();
				String key = dispatch.key();
				String parameter = request.getParameter(key);
				if(parameter!=null){
					return metadatum;					
				}
			}
		}
		for (ActionMetadata metadatum : metadata) {
			if(metadatum.getHttpMethod().name().equals(method)&&
					metadatum.getDispatch()==null){
				return metadatum;
			}
		}
		return null;
	}
	public Collection<ActionMetadata[]> getActionMetadata(){
		return actions.values();
	}
	public String convertToURL(String methodName, Object[] arguments) {
		ActionMetadata metadata = 
			actionByMethodName.get(methodName);
		if(metadata!=null){
			return name+"/"+metadata.convertToURL(arguments);			
		}
		log.warn("cannot find ActionMetadata by " + methodName);
		return null;
	}
	public String convertToURL(String methodName, Object[] arguments, HttpServletRequest request) {
		ActionMetadata metadata = 
			actionByMethodName.get(methodName);
		if(metadata!=null){
			return name+"/"+metadata.convertToURL(arguments,request);			
		}
		log.warn("cannot find ActionMetadata by " + methodName);
		return null;
	}

	public int getPostActionCount(){
		return getActionCount(HttpMethod.POST);
	}
	public int getGetActionCount(){
		return getActionCount(HttpMethod.GET);
	}
	private int getActionCount(HttpMethod method){
		int count = 0;
		Collection<ActionMetadata> actions = actionByMethodName.values();
		for (ActionMetadata metadata : actions) {
			if(method.equals(metadata.getHttpMethod())){
				count++;
			}
		}
		return count;
	}
	public ActionMetadata getAction(String methodName) {
		return actionByMethodName.get(methodName);
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
		actionByMethodName.put(metadata.getMethod().getName(), metadata);
	}
	public void setDefaultActionName(String defaultActionName) {
		this.defaultActionName = defaultActionName;
	}
	public Class getControllerClass() {
		return controllerClass;
	}
	public void saveConverterSettings(OutputStream os){
		XMLEncoder encoder = new XMLEncoder(os);
		encoder.writeObject(getConverters());
		encoder.close();
	}
	public Map<String,Converter[]> getConverters(){
		Map<String,Converter[]> map = 
			new HashMap<String,Converter[]>();
		Collection<ActionMetadata[]> am = actions.values();
		for (ActionMetadata[] metadatas : am) {
			for (ActionMetadata metadata : metadatas) {
				map.put(metadata.getMethod().getName(), metadata.getConverters());
			}
		}
		return map;
	}
	public Map<String,ConverterMetadata[]> getConverterMetadata(){
		Map<String,ConverterMetadata[]> map = 
			new HashMap<String,ConverterMetadata[]>();
		Collection<ActionMetadata[]> am = actions.values();
		for (ActionMetadata[] metadatas : am) {
			for (ActionMetadata metadata : metadatas) {
				map.put(metadata.getMethod().getName(), metadata.getConverterMetadata());
			}
		}
		return map;
	}
	public ActionMetadata getAction(Method method) {
		return actionByMethodName.get(method.getName());
	}
	public ActionMetadata getAction(String actionName, String httpMethodName) {
		ActionMetadata[] metadata = actions.get(actionName);
		if(metadata==null || metadata.length==0){
			return null;
		}
		for (ActionMetadata metadatum : metadata) {
			if(metadatum.getHttpMethod().name().equals(httpMethodName)&&
					metadatum.getDispatch()==null){
				return metadatum;
			}
		}
		return null;
	}
}
