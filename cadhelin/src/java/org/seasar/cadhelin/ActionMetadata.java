package org.seasar.cadhelin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.converter.ConverterFactory;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.cadhelin.util.RedirectSession;

public class ActionMetadata {
	private String redirectParameterName = "cadhelin_redirect_to";
	private String resultName = "ret";
	private String actionName;
	private Object controller;
	private Method method;
	private String[] parameterNames;
	private Converter[] converters;

	public ActionMetadata(String actionName,Object controller,
			Method method,String[] parameterNames,ConverterFactory factory) {
		this.actionName = actionName;
		this.controller = controller;
		this.method = method;
		this.parameterNames = parameterNames;
		ResultName rna = (ResultName) AnnotationUtil.getAnnotation(controller.getClass(),ResultName.class,method);
		if(rna!=null){
			resultName = rna.value();
		}
		converters = factory.createConverters(method,parameterNames);
	}
	protected void service(RequestInfo info,HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		if(widthError(info, request, response, message, values)){
			return;
		}
		try {
			Object ret = method.invoke(controller,argumants);
			request.getSession().setAttribute(resultName,ret);
			ControllerContext context = ControllerContext.getContext();
			if(context.isRedirected()){
				return;
			}
			String url = context.getViewURL(info,controller.getClass(),method.getName());
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
	private boolean widthError(RequestInfo info, HttpServletRequest request, HttpServletResponse response, Map message, Map m) throws IOException, ServletException {
		if(message.size()>0){
			String redirectUrl = request.getParameter(redirectParameterName);
			ControllerContext context = ControllerContext.getContext();
			if(redirectUrl!=null){
				RedirectSession.setAttribute(request.getSession(),m);
				RedirectSession.setAttribute(request.getSession(),MessageTool.MESSAGE_KEY,message);
				response.sendRedirect(redirectUrl);
			}else{
				String url = context.getViewURL(info,controller.getClass(),method.getName());
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
}
