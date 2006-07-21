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
import org.seasar.cadhelin.impl.InternalControllerContext;
import org.seasar.cadhelin.util.RedirectSession;
import org.seasar.framework.aop.interceptors.AbstractInterceptor;

public class ControllerInterceptor extends AbstractInterceptor {

	public Object invoke(MethodInvocation method) throws Throwable {
		InternalControllerContext context = 
			(InternalControllerContext) ControllerContext.getContext();
		//もしリクエスト中で2回目に呼び出されたActionメソッドで
		if(!context.isFirstAction()){
			ControllerMetadata controllerMetadata = context.getControllerMetadata(method.getMethod().getDeclaringClass());
			ActionMetadata actionMetadata =
				controllerMetadata.getAction(method.getMethod());
			//かつGETのActionメソッドならリダイレクトする
			if(actionMetadata.getHttpMethod().isGet()){
				String url = context.getUrlByMethodName(
						controllerMetadata.getName(),
						method.getMethod().getName(),
						method.getArguments());
				HttpServletRequest request = context.getRequest();
				RedirectSession.setAttribute(request.getSession(),
						MessageTool.MESSAGE_KEY,request.getAttribute(MessageTool.MESSAGE_KEY));				
				context.getResponse().sendRedirect(url);
				context.setRedirected(true);
				return null;
			}
		}
		//そうでなければ普通に実行
		context.setFirstAction();
		return method.proceed();
	}

}
