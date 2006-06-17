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

import javax.servlet.http.HttpServletRequest;

import org.aopalliance.intercept.MethodInvocation;
import org.seasar.cadhelin.util.RedirectSession;
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
			HttpServletRequest request = controllerContext.getRequest();
			Class class1 = method.getThis().getClass();
			String url = controllerContext.getUrl(class1.getSuperclass(),
					method.getMethod(),method.getArguments());
			RedirectSession.setAttribute(request.getSession(),
					MessageTool.MESSAGE_KEY,request.getAttribute(MessageTool.MESSAGE_KEY));			
			context.getResponse().sendRedirect(url);
			context.setRedirected(true);
		}
		return null;
	}

}
