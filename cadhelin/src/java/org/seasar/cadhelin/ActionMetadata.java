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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.annotation.Dispatch;
import org.seasar.cadhelin.annotation.Render;
import org.seasar.cadhelin.converter.RedirectConverter;
import org.seasar.cadhelin.impl.InternalControllerContext;
import org.seasar.cadhelin.util.RedirectSession;

public class ActionMetadata {
	private HttpMethod httpMethod;
	private String redirectParameterName = "cadhelin_redirect_to";
	private String resultName;
	private String actionName;
	private String controllerName;
	private Object controller;
	private String role;
	private Dispatch dispatch;
	private Method method;
	private String[] parameterNames;
	private Converter[] converters;
	private String render = null;
	private boolean reternMap = false;
	private String urlEncoding;

	public ActionMetadata(
			HttpMethod httpMethod,
			String urlEncoding,
			String controllerName,
			String actionName,
			String resultName,
			Dispatch dispatch, String role,
			Object controller,
			Method method,
			String[] parameterNames,
			Converter[] converters) {
		this.httpMethod = httpMethod;
		this.urlEncoding = urlEncoding;
		this.controllerName = controllerName;
		this.actionName = actionName;
		this.resultName = resultName;
		this.dispatch = dispatch;
		this.role = role;
		this.controller = controller;
		this.method = method;
		this.parameterNames = parameterNames;
		this.converters = converters;
		this.render = actionName;
		Render render2 = method.getAnnotation(Render.class);
		if(render2!=null && render2.value().length()>0){
			render = render2.value();
		}
		reternMap = Map.class.isAssignableFrom(method.getReturnType());
	}
	public Dispatch getDispatch() {
		return dispatch;
	}
	public ConverterMetadata[] getConverterMetadata(){
		ConverterMetadata[] converterMetadata = 
			new ConverterMetadata[converters.length];
		for (int i=0;i<converterMetadata.length;i++) {
			converterMetadata[i] = new ConverterMetadata(converters[i]);
		}
		return converterMetadata;
	}
	public Converter[] getConverters() {
		return converters;
	}
	public Converter getConverter(String parameterName){
		for (Converter converter : converters) {
			if(converter.getParameterName().equals(parameterName)){
				return converter;
			}else{
				Converter c = getConverter(parameterName,converter);
				if(c!=null){
					return c;
				}
			}
		}
		return null;
	}
	private Converter getConverter(String parameterName,Converter converter){
		Converter[] convertors = converter.getChildConvertors();
		for (Converter child : convertors) {
			if(child.getParameterName().equals(parameterName)){
				return child;
			}
		}
		return null;
	}
	public Object[] convertToParameter(HttpServletRequest request,Map<String,Message> error){
		Object[] argumants = new Object[converters.length];
		int i = 0;
		for (Converter con : converters) {
			Object arg = con.convert(request,error);
			argumants[i] = arg;
			request.setAttribute(parameterNames[i],arg);
			i++;
		}
		return argumants;
	}
	public void service(
			InternalControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) throws Throwable {
		if(!request.getMethod().equals(httpMethod.name())){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
		context.setViewName(render);
		request.setAttribute("this",controller);
		request.setAttribute("actionName",actionName);
		Map<String,Message> error = new HashMap<String,Message>();
		//convert
		Object[] argumants = convertToParameter(request,error);
		Map<String,Object> values = new HashMap<String,Object>();
		for(int i=0;i<argumants.length;i++){
			values.put(parameterNames[i],argumants[i]);
		}
		if(error.size()>0){
			widthError(context,request,response,error,values);
			return;
		}
		Object ret = null;
		try {
			ret = method.invoke(controller,argumants);
		} catch (InvocationTargetException e) {
			throw e.getTargetException();
		}
		if(context.isRedirected()){
			RedirectSession.setAttribute(request.getSession(), MessageTool.MESSAGE_KEY, request.getAttribute(MessageTool.MESSAGE_KEY));			
			RedirectSession.setAttribute(request.getSession(), MessageTool.ERROR_KEY, request.getAttribute(MessageTool.ERROR_KEY));
			return;
		}
		if(context.getErrorCount()>0){
			widthError(context,request,response,error,values);				
			return;
		}
		request.setAttribute(resultName,ret);
		if(reternMap && ret != null){
			Map retMap = (Map) ret;
			for (Entry entry : (Collection<Entry>)retMap.entrySet()) {
				request.setAttribute(entry.getKey().toString(),entry.getValue());
			}
		}
		String url = context.getViewURL();
		String redirectUrl = request.getRequestURI();
		if(request.getQueryString()!=null){
			redirectUrl += "?" + request.getQueryString();
		}
		request.setAttribute(redirectParameterName,redirectUrl);
		RequestDispatcher dispatcher = request.getRequestDispatcher(url);
		RedirectSession.copyToRequest(request);
		dispatcher.forward(request,response);
	}
	private void widthError(
			InternalControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response, 
			Map<String,Message> error, 
			Map<String,Object> m) throws IOException, ServletException {
		String redirectUrl = request.getParameter(redirectParameterName);
		if(redirectUrl == null && httpMethod.isPost()){
			//もしリダイレクト先がなくPOSTメソッドならGETにリダイレクトする
			ActionMetadata action = context.getAction(this.controllerName,this.actionName,"GET");
			Object[] arguments = action.convertToParameter(request,new HashMap<String,Message>());
			redirectUrl = action.convertToURL(arguments);
		}
		if(redirectUrl!=null){
			RedirectSession.setAttribute(request.getSession(),m);
			context.addError(error);
			context.setRedirect(redirectUrl);
		}else{
			//リダイレクト先がなければそのままレンダリングを行う
			String url = context.getViewURL();
			context.addMessage(error);
			request.setAttribute(redirectParameterName,request.getRequestURI()+"/"+request.getQueryString());
			RequestDispatcher dispatcher = request.getRequestDispatcher(url);
			dispatcher.forward(request,response);
		}
	}
	public String convertToURL(Object[] arguments,HttpServletRequest request) {
		StringBuffer buff = new StringBuffer();
		buff.append(actionName);
		if(arguments.length>0){
			boolean first = true;
			for(int i=0;i<arguments.length;i++){
				if (converters[i] instanceof RedirectConverter) {
					RedirectSession.setAttribute(request.getSession(),parameterNames[i],arguments[i]);
				}else if(arguments[i]!=null){
					if (arguments[i] instanceof Object[]) {
						Object[] array = (Object[]) arguments[i];
						for (Object object : array) {
							if(first){
								buff.append("?");
							}else{
								buff.append("&");
							}
							buff.append(parameterNames[i]);
							buff.append("=");
							buff.append(encodeURL(object.toString()));
							first = false;
							
						}
					}else{
						if(first){
							buff.append("?");
						}else{
							buff.append("&");
						}
						buff.append(parameterNames[i]);
						buff.append("=");
						buff.append(encodeURL(arguments[i].toString()));
						first = false;						
					}
				}
			}
			
		}
		return buff.toString();
	}
	public String convertToURL(Object[] arguments) {
		StringBuffer buff = new StringBuffer();
		buff.append(actionName);
		if(arguments.length>0){
			boolean first = true;
			for(int i=0;i<arguments.length;i++){
				if(arguments[i]!=null){
					if (arguments[i] instanceof Object[]) {
						Object[] array = (Object[]) arguments[i];
						for (Object object : array) {
							if(first){
								buff.append("?");
							}else{
								buff.append("&");
							}
							buff.append(parameterNames[i]);
							buff.append("=");
							buff.append(encodeURL(object.toString()));
							first = false;
							
						}
					}else{
						if(first){
							buff.append("?");
						}else{
							buff.append("&");
						}
						buff.append(parameterNames[i]);
						buff.append("=");
						buff.append(encodeURL(arguments[i].toString()));
						first = false;						
					}
				}
			}
			
		}
		return buff.toString();
	}
    public String encodeURL(String url){
    	try {
			return URLEncoder.encode(url,urlEncoding);
		} catch (UnsupportedEncodingException e) {
			return url;
		}
    }

	public String getRole() {
		return role;
	}
	public String getName() {
		return actionName;
	}

	public String toString() {
		StringBuilder buff = new StringBuilder();
		buff.append(httpMethod);
		buff.append(" ");
		buff.append(actionName);
		buff.append(" {");
		for (String name : parameterNames) {
			buff.append(name);
			buff.append(", ");
		}
		return buff.toString();
	}
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	public Method getMethod() {
		return method;
	}
	public String getMethodName() {
		return method.getName();
	}
	public boolean isGetAndNoParam(){
		return httpMethod.isGet() && converters.length == 0;
	}
}
