package org.seasar.cadhelin.impl;

import java.lang.reflect.Method;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerMetadata;

public abstract class InternalControllerContext extends ControllerContext {

	public abstract String getViewURL();
	
	public abstract void setControllerName(String controllerName);

	public abstract String getViewName();
	
	public abstract String getUrl(String controllerName, String actionName,
			Object[] arguments);

	public abstract String getUrl(Class clazz, Method method, Object[] arguments);
	
	public abstract void setFirstAction();

	public abstract boolean isRedirected();

	public abstract void setRedirected(boolean redirected);

	public abstract void setRedirect(String redirectUrl);

	public abstract ControllerMetadata getControllerMetadata(
			Class controllerClass);
	
	public abstract ActionMetadata getAction(String controllerName,
			String actionName, String method);


}
