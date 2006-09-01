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

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.util.StringUtil;

public class BooleanConverter extends AbstractConverter {
	private String errorMessageKey = "error.boolean";
	public BooleanConverter() {
		super(new Object[]{boolean.class,Boolean.class});
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		Boolean value = Boolean.FALSE;
		String str = request.getParameter(parameterName);

		if(StringUtil.isNullOrEmpty(str)){
			str = defaultValue;
		}
		
		if(StringUtil.isNullOrEmpty(str)){
			if(required){
				messages.put(parameterName,
						new Message(errorMessageKey+".required",
								messageArguments));
			}
			if(parameterType.isPrimitive()){
				return Boolean.FALSE;
			}
			return null;
		}
		value = Boolean.parseBoolean(str);
		validate(value,messages);
		return value;
	};

}
