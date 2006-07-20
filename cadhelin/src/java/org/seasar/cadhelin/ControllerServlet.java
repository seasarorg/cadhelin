/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.cadhelin;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.impl.ControllerContextImpl;
import org.seasar.cadhelin.impl.InternalControllerContext;
import org.seasar.cadhelin.util.RedirectSession;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class ControllerServlet extends HttpServlet {
	private static Log LOG = LogFactory.getLog(ControllerServlet.class);
	private String urlPrefix = "/do/";
	private String viewUrlPattern  = "/${controllerName}/${actionName}.vm";
	private S2Container container;
	private ExceptionHandlerMetadata exceptionHandlerMetadata;
	private ControllerMetadataFactory controllerMetadataFactory;
	public static final String CONTROLLER_METADATA_NAME = "org.seasar.cadhelin.controllermetadata";
	public static final String CONTROLLER_CONTEXT_NAME = "org.seasar.cadhelin.controllercontext";
	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info("start cadhelin servlert" + config.getServletName());
		String s = config.getInitParameter("urlPrefix");
		if(s!=null){
			urlPrefix = s;
		}
		container = SingletonS2ContainerFactory.getContainer();
		controllerMetadataFactory = new ControllerMetadataFactory(container);
		config.getServletContext().setAttribute(CONTROLLER_METADATA_NAME,controllerMetadataFactory);
		ComponentDef componentDef = container.getComponentDef(ExceptionHandler.class);
		if(componentDef!=null){
			exceptionHandlerMetadata = new ExceptionHandlerMetadata(componentDef);
		}
	}
	public String convertToURL(String contextPath,Class clazz,Method method,Object[] arguments){
		ControllerMetadata metadata = controllerMetadataFactory.getControllerMetadata(clazz);
		String url = contextPath + urlPrefix + metadata.convertToURL(method,arguments);
		return url;
	}
	protected HttpServletRequest createHttpRequest(HttpServletRequest request) throws FileUploadException{
		ServletRequestContext context = new ServletRequestContext(request);
		if(FileUploadBase.isMultipartContent(context)){
			MultipartRequestWrapper wrapper = new MultipartRequestWrapper(request);
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			List list = upload.parseRequest(context);
			wrapper.setFileItems(list);
			return wrapper;
		}else{
			return request;			
		}
	}
	@Override
	protected void service(HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		RedirectSession.move(request.getSession());
		try{
			request = createHttpRequest(request);
			InternalControllerContext controllerContext = 
				new ControllerContextImpl(container,controllerMetadataFactory,request,response,urlPrefix,viewUrlPattern);
			ControllerContext.setContext(
					controllerContext);
			request.setAttribute(CONTROLLER_CONTEXT_NAME,controllerContext);
			RequestInfo info = new RequestInfo(request.getPathInfo());
			ControllerMetadata metadata =
				controllerMetadataFactory.getControllerMetadata(info.getControllerName());
			if(metadata!=null){
				try {
					metadata.service(controllerContext,request,response);
				} catch (Throwable e) {
					if(exceptionHandlerMetadata!=null){
						exceptionHandlerMetadata.service(e,controllerContext,request,response);						
					}
				}				
			}else{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);				
			}
		} catch (FileUploadException e) {
			LOG.error("",e);
			throw new RuntimeException(e);
		}finally{
			if (request instanceof MultipartRequestWrapper) {
				MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
				wrapper.closeFileItems();
			}
		}
	}
}
