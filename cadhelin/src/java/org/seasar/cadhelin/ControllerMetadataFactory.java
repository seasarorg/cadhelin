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
import java.beans.XMLEncoder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.annotation.Default;
import org.seasar.cadhelin.annotation.Dispatch;
import org.seasar.cadhelin.annotation.ResultName;
import org.seasar.cadhelin.annotation.Role;
import org.seasar.cadhelin.impl.ConverterFactoryImpl;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.cadhelin.util.StringUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.util.InputStreamUtil;

public class ControllerMetadataFactory {
	protected Map<String,ControllerMetadata> controllers 
			= new HashMap<String,ControllerMetadata>();
	protected Map<Class,ControllerMetadata> classMap
			= new HashMap<Class,ControllerMetadata>();
	protected String[] getActionPrefix = new String[]{"show"};
	protected String[] postActionPrefix = new String[]{"do"};
	
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
	
	protected ConverterFactory factory;
	public ControllerMetadataFactory(S2Container container) {
		factory = 
			(ConverterFactory) container.getComponent(ConverterFactory.class);
		Object[] f = container.findComponents(ActionFilter.class);
		this.filters = new ActionFilter[f.length];
		System.arraycopy(f,0,filters,0,filters.length);
		setupControllers(container);
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
		String path = controllerClass.getProtectionDomain().getCodeSource().getLocation().getPath();
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
		Object controller = componentDef.getComponent();
		ControllerMetadata metadata = new ControllerMetadata(name,componentDef,filters);
		Map<String, Converter[]> converterMap = loadConverterSettings(componentDef.getComponentClass());
		BeanDesc beanDesc = BeanDescFactory.getBeanDesc(componentDef.getComponentClass());
		Class<?> beanClass = beanDesc.getBeanClass();
		Role r = (Role) beanClass.getAnnotation(Role.class);
		String role = (r!=null && r.value().length() > 0)? r.value() : defaultRole;
		Method[] methods = beanClass.getDeclaredMethods();
		for(Method method : methods){
			if((method.getModifiers() & Modifier.PUBLIC) > 0){
				ActionMetadata actionMetadata = 
					createActionMetadata(beanDesc,name,controller, method, role,converterMap);
				if(actionMetadata!=null){
					metadata.addActionMetadata(actionMetadata.getName(),actionMetadata);					
					Default def = method.getAnnotation(Default.class);
					if(def!=null){
						metadata.setDefaultActionName(actionMetadata.getName());
					}
				}
			}
		}
		return metadata;
	}
	protected ActionMetadata createActionMetadata(
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
		return new ActionMetadata(httpMethod,controllerName,actionName,resultName,dispatch,role,controller,method,parameterNames,converters);
	}
	public ControllerMetadata getControllerMetadata(String controllerName) {
		return controllers.get(controllerName);
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
			clazz = clazz.getSuperclass();
		}
		return null;
	}
}
