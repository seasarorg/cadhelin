package org.seasar.cadhelin.impl;

import java.lang.reflect.Method;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.HttpMethod;

public abstract class InternalControllerContext extends ControllerContext {

	public abstract String getViewURL();
	
	public abstract void setControllerName(String controllerName);

	public abstract String getViewName();
	
	public abstract String getUrlByMethodName(String controllerName, String methodName,
			Object[] arguments);
	
	public abstract void setFirstAction();

	public abstract boolean isRedirected();

	public abstract void setRedirected(boolean redirected);

	public abstract void setRedirect(String redirectUrl);
	
	public abstract ActionMetadata getActionMetadata(Method method);
	
	public abstract ActionMetadata getAction(String controllerName,
			String actionName, HttpMethod method);


}
