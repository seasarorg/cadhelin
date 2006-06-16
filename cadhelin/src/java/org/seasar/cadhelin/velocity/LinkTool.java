package org.seasar.cadhelin.velocity;

import java.util.AbstractMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.AuthorizationManager;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerServlet;

public class LinkTool extends AbstractMap{
	private AuthorizationManager authorizationManager;
	private HttpServletRequest request;
	
	public LinkTool(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getLink(String controllerName,String actionName,Object[] arguments){
		ControllerContext context =
			(ControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
		return context.getUrl(controllerName,actionName,arguments);
	}
	public boolean authorized(String controllerName,String actionName){
		ControllerContext context =
			(ControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
		ActionMetadata action = context.getAction(controllerName,actionName);
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
		ControllerContext context =
			(ControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
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
	
}
