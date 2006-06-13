package org.seasar.cadhelin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.converter.ConverterFactory;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.container.ComponentDef;

public class ControllerMetadata {
	private String name;
	private Object controller;
	private BeanDesc beanDesc;
	private Map<String,ActionMetadata> actions = new HashMap<String,ActionMetadata>();
	public ControllerMetadata(String name,ComponentDef componentDef,ConverterFactory factory){
		this.name = name;
		this.controller = componentDef.getComponent();
		beanDesc = BeanDescFactory.getBeanDesc(componentDef.getComponentClass());
		Method[] methods = beanDesc.getBeanClass().getDeclaredMethods();
		for(Method method : methods){
			Action action = (Action)
				AnnotationUtil.getAnnotation(beanDesc.getBeanClass(),Action.class,method);
			String actionName = method.getName();
			if(action !=null &&
					(method.getModifiers() & Modifier.PUBLIC) > 0){
				actions.put(actionName,
						new ActionMetadata(method.getName(),controller,method,action.value(),factory));				
			}
		}
	}
	protected void service(RequestInfo info,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String actionName = info.getActionName();
		ActionMetadata metadata = null;
		if(actionName!=null && actionName.length() > 0){
			metadata = actions.get(actionName);
		}else{
			metadata = actions.get("index");
		}
		if(metadata!=null){
			metadata.service(info,request,response);			
		}
	}
	public String convertToURL(Method method, Object[] arguments) {
		ActionMetadata metadata = actions.get(method.getName());
		return name+"/"+metadata.convertToURL(arguments);
	}
	public String convertToURL(String actionName, Object[] arguments) {
		ActionMetadata metadata = actions.get(actionName);
		return name+"/"+metadata.convertToURL(arguments);
	}
}
