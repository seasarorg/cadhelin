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
package org.seasar.cadhelin.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionFilter;
import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ActionMetadataFactory;
import org.seasar.cadhelin.ControllerMetadata;
import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.ConverterFactory;
import org.seasar.cadhelin.HttpMethod;
import org.seasar.cadhelin.RequestNamingConvention;
import org.seasar.cadhelin.annotation.Dispatch;
import org.seasar.cadhelin.annotation.ParamNames;
import org.seasar.cadhelin.annotation.ResultName;
import org.seasar.cadhelin.annotation.Role;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.cadhelin.util.StringUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

public class ActionMetadataFactoryImpl implements Disposable, ActionMetadataFactory{
	protected S2Container container;
	protected Map<ActionKey,List<ActionMetadata>> actions =
			new HashMap<ActionKey, List<ActionMetadata>>();
	protected Map<String,ControllerMetadata> controllers 
			= new HashMap<String,ControllerMetadata>();
	protected Map<Class,ControllerMetadata> classMap
			= new HashMap<Class,ControllerMetadata>();
	protected RequestNamingConvention requestNamingConvention;
	protected String[] getActionPrefix = new String[]{"show"};
	protected String[] postActionPrefix = new String[]{"do"};
	protected String urlEncoding;
	/**
	 * 
	 */
	protected String defaultRole;
	/**
	 * アプリケーションのデフォルトロールを設定します。
	 * コントローラークラスやアクションメソッドにRoleアノテーションが設定されていない場合は
	 * このロールが使われます。
	 * @param defaultRole
	 */
	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}
	protected ActionFilter[] filters = new ActionFilter[0];
	public void dispose() {
		actions.clear();
		controllers.clear();
		classMap.clear();
	}
	protected ConverterFactory factory;
	public ActionMetadataFactoryImpl(S2Container container) {
		this.container = container;
		factory = 
			(ConverterFactory) container.getComponent(ConverterFactory.class);
		requestNamingConvention = (RequestNamingConvention) 
			container.getComponent(RequestNamingConvention.class);
	}
	protected ControllerMetadata createControllerMetadata(String controllerName, ComponentDef componentDef){
		DisposableUtil.add(this);
		Object controller = componentDef.getComponent();
		ControllerMetadata metadata = new ControllerMetadata(controllerName ,componentDef);
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentDef.getComponentClass());
		Class<?> beanClass = beanDesc.getBeanClass();
		Role r = (Role) beanClass.getAnnotation(Role.class);
		String role = (r!=null && r.value().length() > 0)? r.value() : defaultRole;
		Method[] methods = beanClass.getDeclaredMethods();
		String controllerPath = requestNamingConvention.createControllerPath(controllerName);
		for(Method method : methods){
			if((method.getModifiers() & Modifier.PUBLIC) > 0){
				ActionMetadata actionMetadata = createActionMetadata(metadata, beanDesc,controllerPath,controllerName,controller, method, role);
				if(actionMetadata!=null){
					metadata.addActionMetadata(actionMetadata.getName(),actionMetadata);
					String actionPath = requestNamingConvention.getActionPath(actionMetadata);
					ActionKey actionKey = new ActionKey(actionPath,actionMetadata.getHttpMethod());
					List<ActionMetadata> as = actions.get(actionKey);
					if(as==null){
						as = new ArrayList<ActionMetadata>();
						actions.put(actionKey, as);
						as.add(actionMetadata);
					}
					as.add(actionMetadata);
				}
			}
		}
		return metadata;
	}
	public ActionMetadata getActionMetadata(HttpServletRequest request){
		String actionPath = requestNamingConvention.getActionPath(request);
		ActionKey actionKey = new ActionKey(actionPath,HttpMethod.valueOf(request.getMethod()));
		List<ActionMetadata> metadata = actions.get(actionKey);
		if(metadata==null || metadata.size()==0){
			setupActionMetadata(request);
			metadata = actions.get(actionKey);
			if(metadata==null || metadata.size()==0){
				return null;				
			}
		}
		ActionMetadata defaultAction = null;
		for (ActionMetadata metadatum : metadata) {
			if(metadatum.getDispatch()!=null){
				//dispatchアノテーションで指定されたパラメータが存在していればそのActionを返す
				if(request.getParameter(metadatum.getDispatch().key())!=null){
					return metadatum;
				}
			}else{
				defaultAction = metadatum;
			}
		}
		//デフォルトActionを返す
		return defaultAction;
		
	}
	private void setupActionMetadata(HttpServletRequest request){
		String controllerName = requestNamingConvention.getControllerName(request);
		String controllerKey = controllerName+"Controller";
		if(container.hasComponentDef(controllerKey)){
			ComponentDef componentDef = container.getComponentDef(controllerKey);
			createControllerMetadata(controllerName,componentDef);
		}
	}
	protected ActionMetadata createActionMetadata(
			ControllerMetadata controllerMetadata,
			BeanDesc beanDesc,
			String controllerPath,
			String controllerName,
			Object controller,
			Method method,
			String defaultRole){
		HttpMethod httpMethod = null;
		String actionName = null;
		//指定されたPrefix(一般的にはshow)で始まるMethodであればPrefixをのぞいてアクション名とする
		httpMethod = requestNamingConvention.getHttpMethod(method);
		actionName = requestNamingConvention.getActionName(method);
		//Dispatchアノテーションが指定されていればaction名を変更
		Dispatch dispatch = method.getAnnotation(Dispatch.class);
		if(dispatch!=null){
			actionName = dispatch.actionName();
		}
		if(actionName==null){
			return null;
		}
		actionName = StringUtil.toLowwerCaseInitial(actionName);
		String[] parameterNames = null;
		ParamNames param = method.getAnnotation(ParamNames.class);
		if(param!=null){
			parameterNames = param.value();
		}else{
			parameterNames = beanDesc.getMethodParameterNames(method);
		}
		Role r = method.getAnnotation(Role.class);
		String role = (r!=null && r.value().length() > 0)? r.value() : defaultRole;
		String resultName = actionName;
		ResultName rna = (ResultName) AnnotationUtil.getAnnotation(controller.getClass(),ResultName.class,method);
		if(rna!=null){
			resultName = rna.value();
		}
		Converter[] converters = factory.createConverters(method,parameterNames);
		return new ActionMetadata(
				httpMethod,
				urlEncoding,
				controllerPath,
				controllerName,
				actionName,
				resultName,
				dispatch,
				role,
				controller,
				method,
				requestNamingConvention,
				parameterNames,
				converters);
	}
	public ControllerMetadata getControllerMetadata(String controllerName) {
		ControllerMetadata controllerMetadata = controllers.get(controllerName);
		if(controllerMetadata==null){
			controllerMetadata = setupControllerMetadata(controllerName);
		}
		return controllerMetadata;
	}
	public ControllerMetadata setupControllerMetadata(String controllerName){
		ComponentDef componentDef = container.getComponentDef(controllerName+"Controller");
		return createControllerMetadata(controllerName,componentDef);		
	}
	public ActionMetadata getActionMetadata(String controllerName, String methodName) {
		ControllerMetadata controllerMetadata = getControllerMetadata(controllerName);
		return controllerMetadata.getAction(methodName);
	}
	public ActionMetadata getActionMetadata(String controllerName, String actionName, HttpMethod method) {
		ControllerMetadata controllerMetadata = getControllerMetadata(controllerName);
		return controllerMetadata.getAction(actionName,method.toString());
	}
	public ActionMetadata getActionMetadata(Method method) {
		Class<?> clazz = method.getDeclaringClass();
		while(clazz != null){
			ControllerMetadata controllerMetadata = classMap.get(clazz);
			if(controllerMetadata!=null){
				return controllerMetadata.getAction(method);
			}
			ComponentDef componentDef = container.getComponentDef(clazz);
			if(componentDef!=null){
				String controllerName = componentDef.getComponentName().replaceFirst("Controller$", "");
				return createControllerMetadata(controllerName,componentDef).getAction(method); 
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
}
class ActionKey {
	private String name;
	private HttpMethod method;
	public ActionKey(String name, HttpMethod method) {
		super();
		this.name = name;
		this.method = method;
	}
	public int hashCode() {
		return 
			2*name.hashCode() +
			method.hashCode();
	}
	public boolean equals(Object obj) {
		if (obj instanceof ActionKey) {
			ActionKey actionKey = (ActionKey) obj;
			return  name.equals(actionKey.name) && 
					method.equals(actionKey.method);
		}
		return false;
	}
}