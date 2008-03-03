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
package org.seasar.cadhelin.converter;

import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.MultipartRequestWrapper;

public class ParameterConverter extends AbstractConverter {
	public ParameterConverter() {
		super(new Object[]{Map.class});
	}

	@SuppressWarnings("unchecked")
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		HashMap<String, Object[]> parameters = new HashMap<String,Object[]>(request.getParameterMap());
		Enumeration parameterNames = request.getParameterNames();
		while(parameterNames.hasMoreElements()){
			String name = (String) parameterNames.nextElement();
			parameters.put(name, request.getParameterValues(name));
		}
		if (request instanceof MultipartRequestWrapper) {
			MultipartRequestWrapper multiPartRequest 
				= (MultipartRequestWrapper) request;
			Collection<FileItem> fileItems = multiPartRequest.getFileItems();
			for (FileItem item : fileItems) {
				if(!item.isFormField()){
					parameters.put(item.getFieldName(), new FileItem[]{item});
				}
			}
		}
		return parameters;
	};

}
