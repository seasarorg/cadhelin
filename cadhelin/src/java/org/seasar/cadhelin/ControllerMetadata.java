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
	private String role;
	private String name;
	private Object controller;
	private BeanDesc beanDesc;
	private Map<String,ActionMetadata> actions = 
		new HashMap<String,ActionMetadata>();
	private ActionFilter[] filters = new ActionFilter[0];
	
	@SuppressWarnings("unchecked")
	public ControllerMetadata(
			String name,
			String defaultRole,
			ComponentDef componentDef,
			ConverterFactory factory,
			ActionFilter[] filters){
		this.name = name;
		this.controller = componentDef.getComponent();
		this.filters = filters;
		beanDesc = BeanDescFactory.getBeanDesc(componentDef.getComponentClass());
		Class beanClass = beanDesc.getBeanClass();
		Role r = (Role) beanClass.getAnnotation(Role.class);
		role = (r!=null && r.value().length() > 0)? r.value() : defaultRole;
		Method[] methods = beanClass.getDeclaredMethods();
		for(Method method : methods){
			Action action = (Action)
				AnnotationUtil.getAnnotation(beanClass,Action.class,method);
			String actionName = method.getName();
			if(action !=null &&
					(method.getModifiers() & Modifier.PUBLIC) > 0){
				actions.put(actionName,
						new ActionMetadata(method.getName(),role,controller,method,action.value(),factory));				
			}
		}
	}
	protected void service(
			ControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		String actionName = context.getActionName();
		ActionMetadata metadata = null;
		if(actionName!=null && actionName.length() > 0){
			metadata = actions.get(actionName);
		}else{
			metadata = actions.get("index");
		}
		if(metadata!=null){
			FilterContextImpl filter = new FilterContextImpl(filters,context,metadata);
			filter.doFilter(request,response);
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
	public ActionMetadata getAction(String actionName) {
		return actions.get(actionName);
	}
}
