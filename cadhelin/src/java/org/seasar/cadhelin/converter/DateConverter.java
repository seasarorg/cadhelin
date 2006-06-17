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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class DateConverter extends AbstractConverter {
	private static final String ERROR_KEY_DATE_REQUIRED = "error.converter.date.required";
	private static final String ERROR_KEY_DATE_FORMAT = "error.converter.date.format";
	private boolean required = true;
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public DateConverter() {
		super(new Object[]{"date",Date.class});
	}
	public Converter createInstance(String parameterName, Class targetClass, Param validater) {
		DateConverter converter = null;
		try {
			converter = (DateConverter) clone();
			converter.parameterName = parameterName;
			if(validater!=null){
				converter.required = validater.required();				
			}
			return converter;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		String str = request.getParameter(parameterName);
		if(str==null || str.length()==0){
			if(required){
				messages.put(parameterName,new Message(ERROR_KEY_DATE_REQUIRED+"."+parameterName));
			}
			return null;
		}
		try {
			Date date = sdf.parse(str);
			validate(date,messages);
			return date;
		} catch (ParseException e1) {
			messages.put(parameterName,new Message(ERROR_KEY_DATE_FORMAT+"."+parameterName));
			return str;
		}
	};

}
