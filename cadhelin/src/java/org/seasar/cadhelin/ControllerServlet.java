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
import org.seasar.cadhelin.impl.ActionMetadataFactoryImpl;
import org.seasar.cadhelin.impl.ControllerContextImpl;
import org.seasar.cadhelin.impl.FilterContextImpl;
import org.seasar.cadhelin.impl.InternalControllerContext;
import org.seasar.cadhelin.util.RedirectSession;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class ControllerServlet extends HttpServlet {
	private static Log LOG = LogFactory.getLog(ControllerServlet.class);
	private String viewUrlPattern  = "/${controllerName}/${actionName}.vm";
	private S2Container container;
	private String encoding = "UTF-8";
	private ExceptionHandlerMetadata exceptionHandlerMetadata;
	private ActionMetadataFactory actionMetadataFactory;
	private ActionFilter[] filters;
	public static final String CONTROLLER_METADATA_NAME = "org.seasar.cadhelin.controllermetadata";
	public static final String CONTROLLER_CONTEXT_NAME = "org.seasar.cadhelin.controllercontext";
	@Override
	public void init(ServletConfig config) throws ServletException {
		LOG.info("start cadhelin servlert" + config.getServletName());
		String s = config.getInitParameter("encoding");
		if(s!=null){
			encoding = s;
		}
		s = config.getInitParameter("viewUrlPattern");
		if(s!=null){
			viewUrlPattern = s;
		}
		container = SingletonS2ContainerFactory.getContainer();
		actionMetadataFactory = new ActionMetadataFactoryImpl(container);
		config.getServletContext().setAttribute(CONTROLLER_METADATA_NAME,actionMetadataFactory);
		if(container.hasComponentDef(ExceptionHandler.class)){
			exceptionHandlerMetadata = new ExceptionHandlerMetadata(container.getComponentDef(ExceptionHandler.class));			
		}
		Object[] f = container.findComponents(ActionFilter.class);
		this.filters = new ActionFilter[f.length];
		System.arraycopy(f,0,filters,0,filters.length);

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
		request.setCharacterEncoding(encoding);
		RedirectSession.move(request.getSession());
		try{
			request = createHttpRequest(request);
			ActionMetadata metadata =
				actionMetadataFactory.getActionMetadata(request);
			if(metadata!=null){
				InternalControllerContext controllerContext = 
					new ControllerContextImpl(container,actionMetadataFactory,request,response,viewUrlPattern,
							metadata.getControllerName(),metadata.getName());
				ControllerContext.setContext(
						controllerContext);
				request.setAttribute(CONTROLLER_CONTEXT_NAME,controllerContext);
				try {
					FilterContextImpl filter = new FilterContextImpl(filters,controllerContext,metadata);
					filter.doFilter(request,response);
				} catch (Throwable e) {
					if(exceptionHandlerMetadata!=null){
						exceptionHandlerMetadata.service(e,controllerContext,request,response);						
					}
				}				
			}else{
				response.sendError(HttpServletResponse.SC_NOT_FOUND,request.getRequestURI());				
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
