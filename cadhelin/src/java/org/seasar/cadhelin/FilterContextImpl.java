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
