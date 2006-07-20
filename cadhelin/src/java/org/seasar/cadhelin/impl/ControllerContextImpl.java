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

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerMetadata;
import org.seasar.cadhelin.ControllerMetadataFactory;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.MessageTool;
import org.seasar.cadhelin.RequestInfo;
import org.seasar.cadhelin.util.RedirectSession;
import org.seasar.framework.container.S2Container;

public class ControllerContextImpl implements ControllerContext {
	private static ThreadLocal<ControllerContextImpl> context = 
		new ThreadLocal<ControllerContextImpl>();
	
	public static void setContext(ControllerContextImpl ctx){
		context.set(ctx);
	}
	public static ControllerContext getContext(){
		return context.get();
	}
	private String viewName;
	private S2Container container;
	private RequestInfo info;
	private boolean redirected = false;
	private boolean firstAction = true;
	private String viewUrlPattern  = "/WEB-INF/vm/${controllerName}/${actionName}.vm";
	private ControllerMetadataFactory controllerMetadataFactory;
	private HttpServletRequest request;
	private HttpServletResponse response;
	private String urlPrefix = "/do/";
	
	public ControllerContextImpl(
			S2Container container,
			ControllerMetadataFactory controllerMetadataFactory,
			HttpServletRequest request,
			HttpServletResponse response,
			String urlPrefix,
			String viewUrlPattern) {
		this.container = container;
		this.controllerMetadataFactory = controllerMetadataFactory;
		this.request = request;
		this.response = response;
		this.urlPrefix = urlPrefix;
		this.viewUrlPattern = viewUrlPattern;
		this.info = 
			new RequestInfo(request.getPathInfo());
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#setViewName(java.lang.String)
	 */
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getViewName()
	 */
	public String getViewName() {
		return viewName;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getActionName()
	 */
	public String getActionName(){
		return info.getActionName();
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getControllerName()
	 */
	public String getControllerName(){
		return info.getControllerName();
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getUrl(java.lang.String, java.lang.String, java.lang.Object[])
	 */
	public String getUrl(String controllerName,String actionName,Object[] arguments){
		ControllerMetadata metadata = controllerMetadataFactory.getControllerMetadata(controllerName);
		return request.getContextPath() + urlPrefix  + metadata.convertToURL(actionName,arguments);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getUrl(java.lang.Class, java.lang.reflect.Method, java.lang.Object[])
	 */
	public String getUrl(Class clazz,Method method,Object[] arguments){
		ControllerMetadata metadata = controllerMetadataFactory.getControllerMetadata(clazz);
		return request.getContextPath() + urlPrefix  + metadata.convertToURL(method,arguments);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getViewURL(java.lang.String)
	 */
	public String getViewURL(String actionName){
		return viewUrlPattern.
			replace("${controllerName}",info.getControllerName()).
			replace("${actionName}",actionName);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getCookie(java.lang.String)
	 */
	public String getCookie(String key){
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equals(key)){
				try {
					return URLDecoder.decode(cookies[i].getValue(),"UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#setCookie(java.lang.String, java.lang.String)
	 */
	public void setCookie(String key,String value){
		try {
			response.addCookie(new Cookie(key,(value!=null)?URLEncoder.encode(value,"UTF-8"):null));
		} catch (UnsupportedEncodingException e) {
		}
	}
	
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#setFirstAction()
	 */
	public void setFirstAction(){
		firstAction = false;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#isRedirected()
	 */
	public boolean isRedirected(){
		return redirected;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#setRedirected(boolean)
	 */
	public void setRedirected(boolean redirected) {
		this.redirected = redirected;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#setResponseHeader(java.lang.String, java.lang.String)
	 */
	public void setResponseHeader(String key,String value){
		response.setHeader(key,value);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#isFirstAction()
	 */
	public boolean isFirstAction() {
		return firstAction;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#sendMessage(java.lang.String, org.seasar.cadhelin.Message)
	 */
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
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#addMessage(java.lang.String, org.seasar.cadhelin.Message)
	 */
	@SuppressWarnings("unchecked")
	public void addMessage(String key,Message message){
		Map<String,Message> map = 
			(Map<String,Message>) request.getAttribute(MessageTool.MESSAGE_KEY);
		if(map==null){
			map = new HashMap<String, Message>();
			request.setAttribute(MessageTool.MESSAGE_KEY,map);
		}
		map.put(key,message);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#addMessage(java.util.Map)
	 */
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
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getErrorCount()
	 */
	@SuppressWarnings("unchecked")
	public int getErrorCount(){
		Map<String,Message> map = 
			(Map<String,Message>) request.getAttribute(MessageTool.ERROR_KEY);
		return (map==null)?0:map.size();
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#addError(java.lang.String, org.seasar.cadhelin.Message)
	 */
	@SuppressWarnings("unchecked")
	public void addError(String key,Message message){
		Map<String,Message> map = 
			(Map<String,Message>) request.getAttribute(MessageTool.ERROR_KEY);
		if(map==null){
			map = new HashMap<String, Message>();
			request.setAttribute(MessageTool.ERROR_KEY,map);
		}
		map.put(key,message);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#addError(java.util.Map)
	 */
	@SuppressWarnings("unchecked")
	public void addError(Map<String,Message> messages){
		Map<String,Message> map = 
			(Map<String, Message>) request.getAttribute(MessageTool.ERROR_KEY);
		if(map==null){
			map = new HashMap<String,Message>();
			request.setAttribute(MessageTool.ERROR_KEY,map);
		}
		map.putAll(messages);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getRequest()
	 */
	public HttpServletRequest getRequest() {
		return request;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getResponse()
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getAction(java.lang.String, java.lang.String, java.lang.String)
	 */
	public ActionMetadata getAction(String controllerName, String actionName,String method) {
		ControllerMetadata metadata = 
			controllerMetadataFactory.getControllerMetadata(controllerName);
		return metadata.getAction(actionName,method);
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#setRedirect(java.lang.String)
	 */
	public void setRedirect(String redirectUrl) {
		try {
			RedirectSession.setAttribute(request.getSession(),
					MessageTool.ERROR_KEY,
					request.getAttribute(MessageTool.ERROR_KEY));
			response.sendRedirect(redirectUrl);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getSessionManager()
	 */
	public Object getSessionManager(){
		if(container.hasComponentDef("sessionManager")){
			return container.getComponent("sessionManager");
		}
		return null;
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#createWriter(java.lang.String)
	 */
	public Writer createWriter(String contentType) throws IOException{
		redirected = true;
		response.setContentType( contentType );
		return response.getWriter();
		
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#createWriter(java.lang.String, java.lang.String)
	 */
	public Writer createWriter(String fileName,String contentType) throws IOException{
		redirected = true;
		response.setContentType( contentType );
		response.setHeader( "Content-disposition", "attachment; filename=\"" + fileName + '\"' );
		return response.getWriter();
		
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#createOutputStream(java.lang.String)
	 */
	public OutputStream createOutputStream(String contentType) throws IOException{
		redirected = true;
		response.setContentType( contentType );
		return response.getOutputStream();
		
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#createOutputStream(java.lang.String, java.lang.String)
	 */
	public OutputStream createOutputStream(String fileName,String contentType) throws IOException{
		redirected = true;
		response.setContentType( contentType );
		response.setHeader( "Content-disposition", "attachment; filename=\"" + fileName + '\"' );
		return response.getOutputStream();
		
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#invalidateSession()
	 */
	public void invalidateSession(){
		request.getSession().invalidate();
	}
	/* (non-Javadoc)
	 * @see org.seasar.cadhelin.ControllerContext#getControllerMetadata(java.lang.Class)
	 */
	public ControllerMetadata getControllerMetadata(Class controllerClass) {
		return controllerMetadataFactory.getControllerMetadata(controllerClass);
	}
}
