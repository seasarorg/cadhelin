package org.seasar.cadhelin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.util.RedirectSession;

public class ControllerContext {
	private static ThreadLocal<ControllerContext> context = 
		new ThreadLocal<ControllerContext>();
	
	public static void setContext(ControllerContext ctx){
		context.set(ctx);
	}
	public static ControllerContext getContext(){
		return context.get();
	}
	private RequestInfo info;
	private boolean redirected = false;
	private boolean firstAction = true;
	private String viewUrlPattern  = "/WEB-INF/vm/${controllerName}/${actionName}.vm";
	private ControllerMetadataFactory controllerMetadataFactory;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String urlPrefix = "/do/";
	
	public ControllerContext(
			ControllerMetadataFactory controllerMetadataFactory,
			HttpServletRequest request,
			HttpServletResponse response,
			String urlPrefix,
			String viewUrlPattern) {
		this.controllerMetadataFactory = controllerMetadataFactory;
		this.request = request;
		this.response = response;
		this.urlPrefix = urlPrefix;
		this.viewUrlPattern = viewUrlPattern;
		this.info = 
			new RequestInfo(request.getPathInfo());
	}
	public String getActionName(){
		return info.getActionName();
	}
	public String getControllerName(){
		return info.getControllerName();
	}
	public String getUrl(String controllerName,String actionName,Object[] arguments){
		ControllerMetadata metadata = controllerMetadataFactory.getControllerMetadata(controllerName);
		return request.getContextPath() + urlPrefix  + metadata.convertToURL(actionName,arguments);
	}
	public String getUrl(Class clazz,Method method,Object[] arguments){
		ControllerMetadata metadata = controllerMetadataFactory.getControllerMetadata(clazz);
		return request.getContextPath() + urlPrefix  + metadata.convertToURL(method,arguments);
	}
	public String getViewURL(String actionName){
		return viewUrlPattern.
			replace("${controllerName}",info.getControllerName()).
			replace("${actionName}",actionName);
	}
	public void setFirstAction(){
		firstAction = false;
	}
	public boolean isRedirected(){
		return redirected;
	}
	public void setRedirected(boolean redirected) {
		this.redirected = redirected;
	}
	public boolean isFirstAction() {
		return firstAction;
	}
	@SuppressWarnings("unchecked")
	public void sendMessage(String key,Message message){
		Map<String, Message> attribute = (Map<String, Message>) RedirectSession.getAttribute(
						request.getSession(),MessageTool.MESSAGE_KEY);
		Map<String,Message> map = attribute;
		if(map==null){
			map = new HashMap<String,Message>();
			RedirectSession.setAttribute(request.getSession(),
					MessageTool.MESSAGE_KEY,map);
		}
		map.put(key,message);
	}
	@SuppressWarnings("unchecked")
	public void addMessage(String key,Message message){
		Map<String, Message> map = 
			(Map<String, Message>) request.getAttribute(MessageTool.MESSAGE_KEY);
		if(map==null){
			map = new HashMap<String, Message>();
			request.setAttribute(MessageTool.MESSAGE_KEY,map);
		}
		map.put(key,message);
	}
	@SuppressWarnings("unchecked")
	public void addMessage(Map<String,Message> messages){
		Map<String,Message> map = 
			(Map<String, Message>) request.getAttribute(MessageTool.MESSAGE_KEY);
		if(map==null){
			map = new HashMap<String,Message>();
			request.setAttribute(MessageTool.MESSAGE_KEY,map);
		}
		map.putAll(messages);
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public ActionMetadata getAction(String controllerName, String actionName) {
		ControllerMetadata metadata = 
			controllerMetadataFactory.getControllerMetadata(controllerName);
		return metadata.getAction(actionName);
	}
	public void setRedirect(String redirectUrl) {
		try {
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
