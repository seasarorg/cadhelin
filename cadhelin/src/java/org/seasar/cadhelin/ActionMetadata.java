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
			String url = context.getViewURL();
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
			Map message, 
			Map<String,Object> m) throws IOException, ServletException {
		if(message.size()>0){
			String redirectUrl = request.getParameter(redirectParameterName);
			if(redirectUrl!=null){
				RedirectSession.setAttribute(request.getSession(),m);
				RedirectSession.setAttribute(request.getSession(),MessageTool.MESSAGE_KEY,message);
				response.sendRedirect(redirectUrl);
			}else{
				String url = context.getViewURL();
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
			for(int i=0;i<arguments.length;i++){
				if(i>0){
					buff.append("&");
				}
				buff.append(parameterNames[i]);
				buff.append("=");
				buff.append(arguments[i].toString());
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
