package org.seasar.cadhelin;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

public interface RequestNamingConvention {
	
	public RequestInfo[] getRequestInfo(String actionPath);
	
	public String getActionPath(HttpServletRequest request);
	
	/**
	 * HttpServletRequest‚©‚çŒÄ‚Ño‚³‚ê‚éController–¼‚ğì¬‚·‚é
	 * @param request
	 * @return
	 */
	public String getControllerName(HttpServletRequest request);


	public HttpMethod getHttpMethod(Method method);

	public String getActionName(Method method);

	public String getUrlPrefix();

	public String getDefaultUrlSuffix();

	public String getActionPath(ActionMetadata actionMetadata);

}
