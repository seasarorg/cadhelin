/*
 * Copyright 2002-2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.seasar.cadhelin.velocity;

import org.seasar.cadhelin.ControllerContext;

public class LinkMetadata {
	private ControllerContext context;
	private String controllerName;
	
	public LinkMetadata(ControllerContext context, String controllerName) {
		this.context = context;
		this.controllerName = controllerName;
	}
	public String getLink(String actionName){
		return context.getUrl(controllerName,actionName,new Object[]{});
	}
	public String getLink(String actionName,Object[] args){
		return context.getUrl(controllerName,actionName,args);
	}
}
