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
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.util.RedirectSession;

public class ActionMetadata {
	private HttpMethod httpMethod;
	private String redirectParameterName = "cadhelin_redirect_to";
	private String resultName;
	private String actionName;
	private Object controller;
	private String role;
	private Method method;
	private String[] parameterNames;
	private Converter[] converters;

	public ActionMetadata(
			HttpMethod httpMethod,
			String actionName,
			String resultName,
			String role,
			Object controller,
			Method method,
			String[] parameterNames,
			Converter[] converters) {
		this.httpMethod = httpMethod;
		this.actionName = actionName;
		this.resultName = resultName;
		this.role = role;
		this.controller = controller;
		this.method = method;
		this.parameterNames = parameterNames;
		this.converters = converters;
	}
	protected void service(
			ControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		if(!request.getMethod().equals(httpMethod.name())){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
		Object[] argumants = new Object[converters.length];
		int i = 0;
		Map<String,Message> message = new HashMap<String,Message>();
		//convert
		Map<String,Object> values = new HashMap<String,Object>();
		for (Converter con : converters) {
			Object arg = con.convert(request,message);
			argumants[i] = arg;
			request.setAttribute(parameterNames[i],arg);
			values.put(parameterNames[i],arg);
			i++;
		}
		if(widthError(context, request, response, message, values)){
			return;
		}
		try {
			Object ret = method.invoke(controller,argumants);
			request.setAttribute(resultName,ret);
			if(context.isRedirected()){
				return;
			}
			String url = context.getViewURL(actionName);
			String redirectUrl = request.getRequestURI();
			if(request.getQueryString()!=null){
				redirectUrl += "?" + request.getQueryString();
			}
			request.setAttribute(redirectParameterName,redirectUrl);
			RequestDispatcher dispatcher = request.getRequestDispatcher(url);
			RedirectSession.copyToRequest(request);
			dispatcher.forward(request,response);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private boolean widthError(
			ControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response, 
			Map<String,Message> message, 
			Map<String,Object> m) throws IOException, ServletException {
		if(message.size()>0){
			String redirectUrl = request.getParameter(redirectParameterName);
			if(redirectUrl!=null){
				RedirectSession.setAttribute(request.getSession(),m);
				RedirectSession.setAttribute(request.getSession(),MessageTool.MESSAGE_KEY,message);
				response.sendRedirect(redirectUrl);
			}else{
				String url = context.getViewURL(actionName);
				context.addMessage(message);
				request.setAttribute(redirectParameterName,request.getRequestURI()+"/"+request.getQueryString());
				RequestDispatcher dispatcher = request.getRequestDispatcher(url);
				dispatcher.forward(request,response);				
			}
			return true;
		}
		return false;
	}
	public String convertToURL(Object[] arguments) {
		StringBuffer buff = new StringBuffer();
		buff.append(actionName);
		if(arguments.length>0){
			buff.append("?");
			boolean first = true;
			for(int i=0;i<arguments.length;i++){
				if(arguments[i]!=null){
					if(!first){
						buff.append("&");
					}
					buff.append(parameterNames[i]);
					buff.append("=");
					buff.append(arguments[i].toString());
					first = false;
				}
			}
			
		}
		return buff.toString();
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
	public Method getMethod() {
		return method;
	}
}