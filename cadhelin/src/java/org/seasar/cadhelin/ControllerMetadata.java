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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.framework.container.ComponentDef;

public class ControllerMetadata {
	Log log = LogFactory.getLog(this.getClass());
	private Class controllerClass;
	private String name;
	private Map<String,ActionMetadata[]> actions = 
		new HashMap<String,ActionMetadata[]>();
	private Map<String,ActionMetadata> actionByMethodName = 
		new HashMap<String,ActionMetadata>();
	private String defaultActionName = null;
	public ControllerMetadata(
			String name,
			ComponentDef componentDef){
		this.controllerClass = componentDef.getComponentClass();
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public Collection<ActionMetadata[]> getActionMetadata(){
		return actions.values();
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
//			if(method.equals(metadata.getHttpMethod())){
//				count++;
//			}
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
