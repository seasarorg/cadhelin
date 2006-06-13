package org.seasar.cadhelin.velocity;

import org.seasar.cadhelin.ControllerContext;

public class LinkMetadata {
	private ControllerContext context;
	private String controllerName;
	
	public LinkMetadata(ControllerContext context, String controllerName) {
		this.context = context;
		this.controllerName = controllerName;
	}
	public String getLink(String actionName){
		return context.getUrl(controllerName,actionName,new Object[]{});
	}
	public String getLink(String actionName,Object[] args){
		return context.getUrl(controllerName,actionName,args);
	}
}
