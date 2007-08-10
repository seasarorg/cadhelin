package org.seasar.cadhelin.impl;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.HttpMethod;
import org.seasar.cadhelin.RequestInfo;
import org.seasar.cadhelin.RequestNamingConvention;
import org.seasar.cadhelin.util.StringUtil;

public class RequestNamingConventionImpl implements RequestNamingConvention {
	private String[] getMethodPrefix = new String[]{"show"};
	private String[] postMethodPrefix = new String[]{"do"};
	private String defaultControllerName = "default";
	private String pathSepareter = "/";
	private String urlPrefix = "/do/";
	private String defaultUrlSuffix = "";
	public void setGetMethodPrefix(String[] getMethodPrefix) {
		this.getMethodPrefix = getMethodPrefix;
	}
	public void setPostMethodPrefix(String[] postMethodPrefix) {
		this.postMethodPrefix = postMethodPrefix;
	}
	public void setDefaultUrlSuffix(String defaultUrlSuffix) {
		this.defaultUrlSuffix = defaultUrlSuffix;
	}
	public String getUrlPrefix() {
		return urlPrefix;
	}
	public String getDefaultUrlSuffix() {
		return defaultUrlSuffix;
	}
	public RequestInfo[] getRequestInfo(String actionPath) {
		actionPath = actionPath.substring(urlPrefix.length());
		String extention = "";
		if(0 < actionPath.lastIndexOf('.')){
			extention = actionPath.substring(actionPath.lastIndexOf('.')+1);
			actionPath = actionPath.substring(0, actionPath.lastIndexOf('.'));
		}
		;
		String[] paths = StringUtil.split(actionPath, '/');
		if(paths.length==1){
			return new RequestInfo[]{new RequestInfo("",paths[0],extention,null)};
		}else if(paths.length==2){
			return new RequestInfo[]{
					new RequestInfo(paths[0],paths[1],extention,null),
			};
		}else if(paths.length == 3){
			String[] params = new String[paths.length-2];
			System.arraycopy(paths, 2, params, 0, params.length);
			return new RequestInfo[]{
					new RequestInfo(paths[0]+'_'+paths[1],paths[2],extention,null),
					new RequestInfo(paths[0],paths[1],extention,params),
			};			
		}else{
			String[] params1 = new String[paths.length-3];
			System.arraycopy(paths, 3, params1, 0, params1.length);
			String[] params2 = new String[paths.length-2];
			System.arraycopy(paths, 2, params2, 0, params2.length);
			return new RequestInfo[]{
					new RequestInfo(paths[0]+'_'+paths[1],paths[2],extention,params1),
					new RequestInfo(paths[0],paths[1],extention,params2),
			};
		}
	}
	public String getActionPath(HttpServletRequest request) {
		return request.getServletPath() + 
				((request.getPathInfo()==null)?"":request.getPathInfo());
	}
	public String getControllerName(HttpServletRequest request) {
		String actionPath = getActionPath(request).substring(urlPrefix.length());
		String[] paths = actionPath.split(pathSepareter);
		if(paths.length==1){
			return defaultControllerName;
		}else if (paths.length==2){
			if(paths[0].equals(defaultControllerName)){
				return null;
			}
			return paths[0];
		}else if(paths.length == 3){
			return paths[0] + "_" +paths[1];
		}
		return null;
	}
	public String getActionPath(ActionMetadata actionMetadata) {
		return actionMetadata.getActionPath();
	}
	public String createControllerPath(String controllerName) {
		if(defaultControllerName.equals(controllerName)){
			return "";
		}
		return controllerName+"/";
	}
	public String getActionName(Method method) {
		for (String prefix : getMethodPrefix) {
			if(method.getName().startsWith(prefix)){
				return method.getName().substring(prefix.length());
			}
		}
		for (String prefix : postMethodPrefix) {
			if(method.getName().startsWith(prefix)){
				return method.getName().substring(prefix.length());
			}
		}
		return null;
	}
	
	public HttpMethod getHttpMethod(Method method) {
		for (String prefix : getMethodPrefix) {
			if(method.getName().startsWith(prefix)){
				return HttpMethod.GET;
			}
		}
		for (String prefix : postMethodPrefix) {
			if(method.getName().startsWith(prefix)){
				return HttpMethod.POST;
			}
		}
		return null;
	}
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}
}
