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
package org.seasar.cadhelin.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.ActionFilter;
import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.AuthorizationManager;
import org.seasar.cadhelin.FilterContext;

public class AuthorizationFilter implements ActionFilter {
	private String redirectUrl;
	private AuthorizationManager authorizationManager;
	
	public AuthorizationFilter(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public void doFilter(
			FilterContext context, 
			HttpServletRequest request, 
			HttpServletResponse response) throws Throwable {
		Object sessionManager = request.getSession().getAttribute("sessionManager");
		ActionMetadata actionMetadata = context.getActionMetadata();
		boolean authorized = authorizationManager.authorized(sessionManager,actionMetadata.getRole());
		if(authorized){
			context.doFilter(request,response);
		}else{
			context.sendRedirect(redirectUrl);
		}
	}

}
