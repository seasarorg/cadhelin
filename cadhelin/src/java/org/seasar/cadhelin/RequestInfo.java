package org.seasar.cadhelin;

public class RequestInfo {
	String[] path;
	public RequestInfo(String pathInfo){
		path = pathInfo.split("/");
	}
	public String getControllerName(){
		return path[1];
	}
	public String getActionName(){
		return (path.length>2)?path[2]:null;
	}
}
