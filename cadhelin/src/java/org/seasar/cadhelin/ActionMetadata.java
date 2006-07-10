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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.annotation.Render;
import org.seasar.cadhelin.util.RedirectSession;

public class ActionMetadata {
	private static final Log LOG = LogFactory.getLog(ActionMetadata.class);
	private HttpMethod httpMethod;
	private String redirectParameterName = "cadhelin_redirect_to";
	private String resultName;
	private String actionName;
	private String controllerName;
	private Object controller;
	private String role;
	private Method method;
	private String[] parameterNames;
	private Converter[] converters;
	private String render = null;
	private boolean reternMap = false;

	public ActionMetadata(
			HttpMethod httpMethod,
			String controllerName,
			String actionName,
			String resultName,
			String role,
			Object controller,
			Method method,
			String[] parameterNames,
			Converter[] converters) {
		this.httpMethod = httpMethod;
		this.controllerName = controllerName;
		this.actionName = actionName;
		this.resultName = resultName;
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
	public Converter[] getConverters(){
		return converters;
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
	protected void service(
			ControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		if(!request.getMethod().equals(httpMethod.name())){
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;			
		}
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
		try {
			Object ret = null;
			try {
				ret = method.invoke(controller,argumants);
			} catch (InvocationTargetException e) {
				LOG.error("exception",e);
				RedirectSession.setAttribute(
						request.getSession(),"exception",e.getTargetException());
				widthError(context,request,response,error,values);
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
			if(context.isRedirected()){
				return;
			}
			if(context.getViewName()!=null){
				render = context.getViewName();
			}
			String url = context.getViewURL(render);
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
	private void widthError(
			ControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response, 
			Map<String,Message> error, 
			Map<String,Object> m) throws IOException, ServletException {
		String redirectUrl = request.getParameter(redirectParameterName);
		if(redirectUrl == null && httpMethod.isPost()){
			//�������_�C���N�g�悪�Ȃ�POST���\�b�h�Ȃ�GET�Ƀ��_�C���N�g����
			ActionMetadata action = context.getAction(this.controllerName,this.actionName,"GET");
			Object[] arguments = action.convertToParameter(request,new HashMap<String,Message>());
			redirectUrl = action.convertToURL(arguments);
		}
		if(redirectUrl!=null){
			RedirectSession.setAttribute(request.getSession(),m);
			context.addError(error);
			context.setRedirect(redirectUrl);
		}else{
			//���_�C���N�g�悪�Ȃ���΂��̂܂܃����_�����O���s��
			String url = context.getViewURL(actionName);
			context.addMessage(error);
			request.setAttribute(redirectParameterName,request.getRequestURI()+"/"+request.getQueryString());
			RequestDispatcher dispatcher = request.getRequestDispatcher(url);
			dispatcher.forward(request,response);
		}
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
							buff.append(object.toString());
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
						buff.append(arguments[i].toString());
						first = false;						
					}
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
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}
	public Method getMethod() {
		return method;
	}
}
