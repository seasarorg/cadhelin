package org.seasar.cadhelin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ControllerContext {
	private static ThreadLocal<ControllerContext> context = 
		new ThreadLocal<ControllerContext>();
	
	public static void setContext(ControllerContext ctx){
		context.set(ctx);
	}
	public static ControllerContext getContext(){
		return context.get();
	}
	private boolean redirected = false;
	private boolean firstAction = true;
	private ControllerServlet servlet;
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public ControllerContext(ControllerServlet servlet,HttpServletRequest request, HttpServletResponse response) {
		this.servlet = servlet;
		this.request = request;
		this.response = response;
	}
	public String getUrl(Class clazz,Method method,Object[] arguments){
		return servlet.convertToURL(request.getContextPath(),clazz,method,arguments);
	}
	public String getViewURL(RequestInfo info,Class clazz,String method){
		return servlet.convertToViewURL(info,clazz,method);
	}
	public void setFirstAction(){
		firstAction = false;
	}
	public boolean isRedirected(){
		System.out.println("redirected = " + redirected);
		return redirected;
	}
	public void setRedirected(boolean redirected) {
		System.out.println("setRedirected = " + this.redirected+" to " + redirected);
		this.redirected = redirected;
	}
	public boolean isFirstAction() {
		return firstAction;
	}
	@SuppressWarnings("unchecked")
	public void addMessage(String key,Message message){
		Map map = (Map) request.getAttribute(MessageTool.MESSAGE_KEY);
		if(map==null){
			map = new HashMap();
		}
		map.put(key,message);
	}
	@SuppressWarnings("unchecked")
	public void addMessage(Map messages){
		Map map = (Map) request.getAttribute(MessageTool.MESSAGE_KEY);
		if(map==null){
			map = new HashMap();
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
}
