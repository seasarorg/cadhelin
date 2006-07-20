package org.seasar.cadhelin;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.impl.InternalControllerContext;
import org.seasar.cadhelin.util.RedirectSession;
import org.seasar.framework.container.ComponentDef;

public class ExceptionHandlerMetadata {
	private String handlerName;
	private Map<Class,Method> handlerMap = 
		new HashMap<Class,Method>();
	private Class handlerClass;
	private Object hander;
	public ExceptionHandlerMetadata(ComponentDef componentDef){
		hander = componentDef.getComponent();
		handlerClass = componentDef.getConcreteClass();
		Method[] methods = handlerClass.getMethods();
		for (Method method : methods) {
			//メソッド名がhandleで始まり
			if(!method.getName().startsWith("handle")){
				continue;
			}
			//Throwableの子クラスのみを引数にとれば
			Class<?>[] types = method.getParameterTypes();
			if(!(types.length==1 && Throwable.class.isAssignableFrom(types[0]))){
				continue;				
			}
			//ハンドラーメソッドとして登録
			handlerMap.put(types[0],method);
		}
		handlerName = componentDef.getComponentName();
	}
	public void service(
			Throwable throwable,
			InternalControllerContext context,
			HttpServletRequest request, 
			HttpServletResponse response) {
		Class<?> clazz = throwable.getClass();
		while(!handlerMap.containsKey(clazz)){
			clazz = clazz.getSuperclass();
		}
		Method method = handlerMap.get(clazz);
		Object object = null;
		try {
			object = method.invoke(hander,new Object[]{throwable});
		} catch (Exception e) {
			e.printStackTrace();
		}
		String retName = method.getName().replace("handle","");
		request.setAttribute(retName,object);
		context.setControllerName(handlerName);
		context.setViewName(retName);
		String viewURL = context.getViewURL();
		RequestDispatcher dispatcher = request.getRequestDispatcher(viewURL);
		RedirectSession.copyToRequest(request);
		try {
			dispatcher.forward(request,response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
