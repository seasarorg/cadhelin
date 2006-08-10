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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.impl.InternalControllerContext;

public class LinkMetadata {
	private static Log log = LogFactory.getLog(LinkMetadata.class);
	private InternalControllerContext context;
	private String controllerName;
	
	public LinkMetadata(InternalControllerContext context, String controllerName) {
		this.context = context;
		this.controllerName = controllerName;
	}
	public String getLink(String methodName){
		return context.getUrlByMethodName(controllerName,methodName,new Object[]{});
	}
	public String getLink(String methodName,Object[] args){
		String url = context.getUrlByMethodName(controllerName,methodName,args);
		if(url!=null){
			return url;
		}
		ActionMetadata action = context.getAction(controllerName,methodName,"GET");
		url = context.getUrlByMethodName(controllerName,action.getMethodName(),args);
		if(url!=null){
			log.warn("depricated : please specify not actionName("+action.getName()+
					") but methodName("+action.getMethodName()+")");
		}
		return url;
	}
}
