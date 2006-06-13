package org.seasar.cadhelin;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

public class ControllerInterceptor extends AbstractInterceptor {

	public Object invoke(MethodInvocation method) throws Throwable {
		ControllerContext context = ControllerContext.getContext();
		ControllerContext controllerContext = context;
		if(controllerContext.isFirstAction()){
			controllerContext.setFirstAction();
			return method.proceed();
		}else{
			//redirect
			Class class1 = method.getThis().getClass();
			String url = controllerContext.getUrl(class1.getSuperclass(),method.getMethod(),method.getArguments());
			context.getResponse().sendRedirect(url);
			context.setRedirected(true);
		}
		return null;
	}

}
