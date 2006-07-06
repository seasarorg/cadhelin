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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.MultipartRequestWrapper;
import org.seasar.cadhelin.Param;

public class FileItemConverter extends AbstractConverter {
	private static final Log LOG = LogFactory.getLog(FileItemConverter.class);
	private static final String ERROR_KEY_REQUIRED = "error.converter.fileitem.required";
	public FileItemConverter() {
		super(new Object[]{FileItem.class});
	}
	public FileItemConverter(Object[] keys,String parameterName,Param validater){
		super(keys);
		this.parameterName = parameterName;
		if(validater!=null){
			required = validater.required();

		}
	}
	public Converter createInstance(
			String parameterName, 
			Class targetClass, 
			Param validater) {
		return new FileItemConverter(converterKeys,parameterName,validater);
	}
	
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> message) {
		if (request instanceof MultipartRequestWrapper) {
			MultipartRequestWrapper wrapper = (MultipartRequestWrapper) request;
			FileItem fileItem = wrapper.getFileItem(parameterName);
			if((fileItem ==null)&& required){
				message.put(parameterName,new Message(ERROR_KEY_REQUIRED +"." + parameterName));
				return null;
			}
			validate(fileItem,message);
			return fileItem;			
		}
		LOG.warn("file iterm is requested but request is not multipart.");
		return null;
	};

}
