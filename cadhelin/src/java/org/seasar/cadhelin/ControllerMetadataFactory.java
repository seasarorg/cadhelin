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

import java.beans.XMLDecoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.annotation.Default;
import org.seasar.cadhelin.annotation.Dispatch;
import org.seasar.cadhelin.annotation.ResultName;
import org.seasar.cadhelin.annotation.Role;
import org.seasar.cadhelin.annotation.Url;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.cadhelin.util.StringUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;
import org.seasar.framework.util.InputStreamUtil;

public class ControllerMetadataFactory implements Disposable{
	protected S2Container container;
	protected Map<String,List<ActionMetadata>> actions =
			new HashMap<String, List<ActionMetadata>>();
	protected Map<String,ControllerMetadata> controllers 
			= new HashMap<String,ControllerMetadata>();
	protected Map<Class,ControllerMetadata> classMap
			= new HashMap<Class,ControllerMetadata>();
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
	public ControllerMetadataFactory(S2Container container,String urlEncoding) {
		this.container = container;
		this.urlEncoding = urlEncoding;
		factory = 
			(ConverterFactory) container.getComponent(ConverterFactory.class);
	}
	protected void setupControllers(S2Container container){
		int size = container.getComponentDefSize();
		for(int i=0;i<size;i++){
			ComponentDef componentDef = container.getComponentDef(i);
			String componentName = componentDef.getComponentName();
			if(componentName==null){
				componentName = componentDef.getComponentClass().getSimpleName();
			}
			if(componentName.endsWith("Controller")){
				componentName = componentName.replace("Controller","");
				ControllerMetadata controllerMetadata = 
					createControllerMetadata(componentName, componentDef);
				controllers.put(
						componentName,
						controllerMetadata);
				classMap.put(componentDef.getComponentClass(),controllerMetadata);
			}
		}		
	}
	protected Map<String,Converter[]> loadConverterSettings(Class controllerClass){
		ProtectionDomain protectionDomain = controllerClass.getProtectionDomain();
		CodeSource codeSource = protectionDomain.getCodeSource();
		URL location = codeSource.getLocation();
		if(location==null){
			return null;
		}
		String path = location.getPath();
		if(!path.endsWith(".class")){
			return null;
		}
		File file = new File(path.replace(".class", ".converters"));
		if(!file.exists()){
			return null;
		}
		FileInputStream is = null;
		Map<String,Converter[]> map = null;
		try{
			is = new FileInputStream(file);
			XMLDecoder decoder = new XMLDecoder(is);
			map = (Map<String, Converter[]>) decoder.readObject();
			decoder.close();
		}catch (IOException e) {
		}finally{
			InputStreamUtil.close(is);
		}
		return map;
	}
	protected ControllerMetadata createControllerMetadata(
			String name,
			ComponentDef componentDef){
		DisposableUtil.add(this);
		Object controller = componentDef.getComponent();
		Url url = (Url) componentDef.getComponentClass().getAnnotation(Url.class);
		String urlPattern = null;
		if(url!=null){
			urlPattern = url.value();
		}else{
			urlPattern = "/"+name;
		}
		ControllerMetadata metadata = new ControllerMetadata(name,urlPattern,componentDef);
		Map<String, Converter[]> converterMap = loadConverterSettings(componentDef.getComponentClass());
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentDef.getComponentClass());
		Class<?> beanClass = beanDesc.getBeanClass();
		Role r = (Role) beanClass.getAnnotation(Role.class);
		String role = (r!=null && r.value().length() > 0)? r.value() : defaultRole;
		Method[] methods = beanClass.getDeclaredMethods();
		for(Method method : methods){
			if((method.getModifiers() & Modifier.PUBLIC) > 0){
				ActionMetadata actionMetadata = 
					createActionMetadata(metadata, beanDesc,name,controller, method, role,converterMap);
				if(actionMetadata!=null){
					metadata.addActionMetadata(actionMetadata.getName(),actionMetadata);					
					Default def = method.getAnnotation(Default.class);
					if(def!=null){
						metadata.setDefaultActionName(actionMetadata.getName());
						String up = metadata.getUrlPattern()+"/";
						List<ActionMetadata> as = actions.get(up);
						if(as==null){
							as = new ArrayList<ActionMetadata>();
							actions.put(up, as);
						}
						as.add(actionMetadata);
					}
					String up = actionMetadata.getUrlPattern();
					List<ActionMetadata> as = actions.get(up);
					if(as==null){
						as = new ArrayList<ActionMetadata>();
						actions.put(up, as);
					}
					as.add(actionMetadata);
				}
			}
		}
		return metadata;
	}
	public ActionMetadata getActionMetadata(HttpServletRequest request){
		List<ActionMetadata> metadata = actions.get(request.getPathInfo());
		if(metadata==null || metadata.size()==0){
			setupActionMetadata(request);
			metadata = actions.get(request.getPathInfo());
			if(metadata==null || metadata.size()==0){
				return null;				
			}
		}
		for (ActionMetadata metadatum : metadata) {
			if(metadatum.getHttpMethod().name().equals(request.getMethod()) && 
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
			if(metadatum.getHttpMethod().name().equals(request.getMethod())&&
					metadatum.getDispatch()==null){
				return metadatum;
			}
		}
		return null;
		
	}
	private void setupActionMetadata(HttpServletRequest request) {
		String pathInfo = request.getPathInfo();
		String controllerName = pathInfo.split("/")[1];
		ComponentDef componentDef = container.getComponentDef(controllerName+"Controller");
		createControllerMetadata(controllerName, componentDef);
	}
	protected ActionMetadata createActionMetadata(
			ControllerMetadata controllerMetadata,
			BeanDesc beanDesc,
			String controllerName,
			Object controller,
			Method method,
			String defaultRole,
			Map<String, Converter[]> converterMap){
		HttpMethod httpMethod = null;
		String actionName = null;
		//指定されたPrefix(一般的にはshow)で始まるMethodであればPrefixをのぞいてアクション名とする
		for (String prefix : getActionPrefix) {
			if(method.getName().startsWith(prefix)){
				httpMethod = HttpMethod.GET;
				actionName = method.getName().substring(prefix.length());
			}
		}
		for (String prefix : postActionPrefix) {
			if(method.getName().startsWith(prefix)){
				httpMethod = HttpMethod.POST;
				actionName = method.getName().substring(prefix.length());
			}
		}
		//Dispatchアノテーションが指定されていればaction名を変更
		Dispatch dispatch = method.getAnnotation(Dispatch.class);
		if(dispatch!=null){
			actionName = dispatch.actionName();
		}
		if(actionName==null){
			return null;
		}
		actionName = StringUtil.toLowwerCaseInitial(actionName);
		String[] parameterNames = beanDesc.getMethodParameterNames(method);
		Role r = method.getAnnotation(Role.class);
		String role = (r!=null && r.value().length() > 0)? r.value() : defaultRole;
		String resultName = actionName;
		ResultName rna = (ResultName) AnnotationUtil.getAnnotation(controller.getClass(),ResultName.class,method);
		if(rna!=null){
			resultName = rna.value();
		}
		Converter[] converters = null;
		if(converterMap!=null && converterMap.containsKey(method.getName())){
			converters = converterMap.get(method.getName());
		}else{
			converters = factory.createConverters(method,parameterNames);
		}
		Url url = method.getAnnotation(Url.class);
		String urlPattern = null;
		if(url!=null){
			urlPattern = url.value();
		}else{
			urlPattern = actionName;
		}
		return new ActionMetadata(controllerMetadata,urlPattern, httpMethod,urlEncoding,controllerName,actionName,resultName,dispatch,role,controller,method,parameterNames,converters);
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
		return createControllerMetadata(controllerName, componentDef);		
	}
	public Collection<ControllerMetadata> getControllerMetadata(){
		return controllers.values();
	}
	public ControllerMetadata getControllerMetadata(Class clazz) {
		while(clazz != null){
			ControllerMetadata controllerMetadata = classMap.get(clazz);
			if(controllerMetadata!=null){
				return controllerMetadata;
			}
			if(clazz.getSimpleName().endsWith("ControllerImpl")){
				String controllerName = 
					StringUtil.toLowwerCaseInitial(clazz.getSimpleName().replace("ControllerImpl", ""));
				ControllerMetadata metadata = setupControllerMetadata(controllerName);
				return metadata;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
}
