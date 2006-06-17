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
	private String viewUrlPattern  = "/WEB-INF/vm/${controllerName}/${actionName}.vm";
	private S2Container container;
	private ControllerMetadataFactory controllerMetadataFactory;
	public static final String CONTROLLER_CONTEXT_NAME = "org.seasar.cadhelin.controllercontext";
	@Override
	public void init(ServletConfig config) throws ServletException {
		String s = config.getInitParameter("urlPrefix");
		if(s!=null){
			urlPrefix = s;
		}
		container = SingletonS2ContainerFactory.getContainer();
		controllerMetadataFactory = new ControllerMetadataFactory(container);
		if(container.hasComponentDef("sessionManager")){
		}
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
		ControllerContext controllerContext = new ControllerContext(controllerMetadataFactory,request,response,urlPrefix,viewUrlPattern);
		ControllerContext.setContext(
				controllerContext);
		request.setAttribute(CONTROLLER_CONTEXT_NAME,controllerContext);
		RequestInfo info = new RequestInfo(request.getPathInfo());
		ControllerMetadata metadata =
			controllerMetadataFactory.getControllerMetadata(info.getControllerName());
		if(metadata!=null){
			metadata.service(controllerContext,request,response);			
		}else{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}
}
