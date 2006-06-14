package org.seasar.cadhelin.velocity;

import java.util.AbstractMap;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerServlet;

public class LinkTool extends AbstractMap{
	private HttpServletRequest request;
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public String getLink(String controllerName,String actionName,Object[] arguments){
		ControllerContext context =
			(ControllerContext) request.getAttribute(ControllerServlet.CONTROLLER_CONTEXT_NAME);
		return context.getUrl(controllerName,actionName,arguments);
	}
	@Override
	public Set entrySet() {
		return null;
	}
	@Override
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
