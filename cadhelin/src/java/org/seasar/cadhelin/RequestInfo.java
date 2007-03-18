package org.seasar.cadhelin;

import org.seasar.cadhelin.util.StringUtil;

public class RequestInfo {
	private String controllerName;
	private String actionName;
	private String extention;
	private String[] params;
	
	public RequestInfo(String controllerName, String actionName, String extention,String[] params) {
		this.controllerName = controllerName;
		this.actionName = actionName;
		this.extention = extention;
		this.params = params;
	}
	public String getControllerName() {
		return controllerName;
	}
	public String getActionName() {
		return actionName;
	}
	public String getExtention() {
		return extention;
	}
	public String[] getParams() {
		return params;
	}
	public String toString() {
		return 
			"controllerName="+controllerName+
			",actionName="+actionName+
			",extention="+extention +
			(params!=null?",param=["+StringUtil.join(params, ",")+"]":"");
	}
	
}
