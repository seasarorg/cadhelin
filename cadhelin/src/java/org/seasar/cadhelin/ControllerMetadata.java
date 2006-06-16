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
import org.apache.log4j.Category;
import org.seasar.framework.container.ComponentDef;

public class ControllerMetadata {
	Log log = LogFactory.getLog(this.getClass());
	private String name;
	private Map<String,ActionMetadata> actions = 
		new HashMap<String,ActionMetadata>();
	private Map<Method,ActionMetadata> actionByMethods = 
		new HashMap<Method,ActionMetadata>();
	private ActionFilter[] filters = new ActionFilter[0];
	
	@SuppressWarnings("unchecked")
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
		ActionMetadata metadata = actionByMethods.get(method);
		if(metadata!=null){
			return name+"/"+metadata.convertToURL(arguments);			
		}
		log.warn("cannot find ActionMetadata by " + method);
		return null;
	}
	public String convertToURL(String actionName, Object[] arguments) {
		ActionMetadata metadata = actions.get(actionName);
		if(metadata!=null){
			return name+"/"+metadata.convertToURL(arguments);			
		}
		log.warn("cannot find ActionMetadata by " + actionName);
		return null;
	}
	public ActionMetadata getAction(String actionName) {
		return actions.get(actionName);
	}
	public void addActionMetadata(String name, ActionMetadata metadata) {
		actions.put(name, metadata);
		actionByMethods.put(metadata.getMethod(), metadata);
	}
}
