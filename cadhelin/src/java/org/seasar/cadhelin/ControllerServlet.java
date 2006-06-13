package org.seasar.cadhelin;

import java.io.IOException;
import java.lang.reflect.Method;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.util.RedirectSession;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class ControllerServlet extends HttpServlet {
	private String urlPrefix = "/do/";
	private S2Container container;
	private ControllerMetadataFactory controllerMetadataFactory;
	@Override
	public void init(ServletConfig config) throws ServletException {
		String s = config.getInitParameter("urlPrefix");
		if(s!=null){
			urlPrefix = s;
		}
		container = SingletonS2ContainerFactory.getContainer();
		controllerMetadataFactory = new ControllerMetadataFactory(container);
	}
	public String convertToURL(String contextPath,Class clazz,Method method,Object[] arguments){
		ControllerMetadata metadata = controllerMetadataFactory.getControllerMetadata(clazz);
		String url = contextPath + urlPrefix + metadata.convertToURL(method,arguments);
		return url;
	}
	@Override
	protected void service(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		RedirectSession.move(request.getSession());
		ControllerContext.setContext(new ControllerContext(this,request,response));
		RequestInfo info = new RequestInfo(request.getPathInfo());
		ControllerMetadata metadata =
			controllerMetadataFactory.getControllerMetadata(info.getControllerName());
		metadata.service(info,request,response);
	}
	public String convertToViewURL(RequestInfo info,Class clazz, String method) {
		return "/WEB-INF/vm/"+ info.getControllerName()+"/"+method+".vm";
	}
}
