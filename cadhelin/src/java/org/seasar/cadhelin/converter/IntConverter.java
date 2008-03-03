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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.annotation.OnError;
import org.seasar.cadhelin.util.StringUtil;

public class IntConverter extends AbstractConverter {
	private String errorMessageKey = "error.integer";
	private Map<String,Object> messageArguments = 
		new HashMap<String,Object>();
	public IntConverter() {
		super(new Object[]{int.class,Integer.class});
	}

	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		String str = request.getParameter(parameterName);
		if(StringUtil.isNullOrEmpty(str)){
			str = defaultValue;
		}
		if(StringUtil.isNullOrEmpty(str)){
			if(required){
				messages.put(parameterName,
						new Message(errorMessageKey+".required." + parameterName,
								messageArguments));
			}
			if(parameterType.isPrimitive()){
				return new Integer(0);
			}
			return null;
		}
		try {
			Integer value = Integer.valueOf(str);
			validate(value,messages);
			return value;
		} catch (NumberFormatException e) {
			if(onError == OnError.IGNORE){
				if(!StringUtil.isNullOrEmpty(defaultValue)){
					return Integer.valueOf(defaultValue);
				}
				return new Integer(0);
			}
			messages.put(parameterName,
					new Message(errorMessageKey+".format." + parameterName,messageArguments));
			return str;
		}
	};

}
