/*
 * Copyright 2002-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.seasar.cadhelin.velocity;

import java.util.AbstractMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.AuthorizationManager;
import org.seasar.cadhelin.ControllerServlet;
import org.seasar.cadhelin.HttpMethod;
import org.seasar.cadhelin.impl.InternalControllerContext;

public class LinkTool extends AbstractMap{
	private AuthorizationManager authorizationManager;
	private HttpServletRequest request;
	
	public LinkTool(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public InsertAsIs getLink(String controllerName,String methodName,Object[] arguments){
		if(arguments==null){
			arguments = new Object[0];
		}
		InternalControllerContext context =
			(InternalControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
		return new InsertAsIs(context.getUrlByMethodName(controllerName,methodName,arguments));
	}
	
	public boolean authorized(String controllerName,String methodName){
		InternalControllerContext context =
			(InternalControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
		ActionMetadata action = context.getAction(controllerName,methodName,HttpMethod.GET);
		if(authorizationManager == null || action==null){
			return false;
		}
		return authorizationManager.authorized(
				request.getSession().getAttribute("sessionManager"),action.getRole());
	}
	
	public Set entrySet() {
		return null;
	}
	
	public Object get(Object key) {
		InternalControllerContext context =
			(InternalControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
		return new LinkMetadata(context,(String)key);
	}
    public String setRelative(String uri)
    {
        if (uri.startsWith("/"))
        {
            return request.getContextPath() + uri;
        }
        else
        {
            return request.getContextPath() + '/' + uri;
        }        
    }
	public String getContextPath(){
		return request.getContextPath();
	}
}
