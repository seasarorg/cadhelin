package org.seasar.cadhelin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FilterContextImpl implements FilterContext {
	private int filterIndex = 0;
	private ActionFilter[] filters;
	private ControllerContext controllerContext;
	private ActionMetadata actionMetadata;
	
	public FilterContextImpl(
			ActionFilter[] filters,
			ControllerContext controllerContext,
			ActionMetadata actionMetadata) {
		this.filters = filters;
		this.controllerContext = controllerContext;
		this.actionMetadata = actionMetadata;
	}

	public void doFilter(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(filterIndex < filters.length){
			ActionFilter filter = filters[filterIndex++];
			filter.doFilter(this,request,response);
		}else{
			actionMetadata.service(controllerContext,request,response);
		}
	}

	public ActionMetadata getActionMetadata() {
		return actionMetadata;
	}

	public void sendRedirect(String redirectUrl) throws IOException {
		controllerContext.getResponse().sendRedirect(redirectUrl);
	}

}
