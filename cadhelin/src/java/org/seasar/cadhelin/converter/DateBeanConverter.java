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
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class DateBeanConverter extends AbstractConverter {
	private static final String YEAR_SUFFIX = "_year";
	private static final String MONTH_SUFFIX = "_month";
	private static final String DATE_SUFFIX = "_date";
	private static final String ERROR_KEY_DATE_REQUIRED = "error.converter.date.required";
	private static final String ERROR_KEY_DATE_FORMAT = "error.converter.date.format";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	public DateBeanConverter() {
		super(new Object[]{"dateBean",});
	}
	public Converter createInstance(String parameterName, Class targetClass, Param validater) {
		DateBeanConverter converter = null;
		try {
			converter = (DateBeanConverter) clone();
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
		String year = request.getParameter(parameterName+YEAR_SUFFIX);
		String month = request.getParameter(parameterName+MONTH_SUFFIX);
		String date = request.getParameter(parameterName + DATE_SUFFIX);
		if(	year==null 		 ||	month==null 	 	|| date==null ||
			year.length()==0 ||	month.length()==0	|| date.length()==0){
			if(required){
				messages.put(parameterName,new Message(ERROR_KEY_DATE_REQUIRED+"."+parameterName,messageArguments));
			}
			return null;
		}
		try {
			Date d = sdf.parse(year+"-"+month+"-"+date);
			validate(d,messages);
			return d;
		} catch (ParseException e1) {
			messages.put(parameterName,new Message(ERROR_KEY_DATE_FORMAT+"."+parameterName,messageArguments));
			HashMap<String,String> map = new HashMap<String,String>();
			map.put("year",year);
			map.put("month",month);
			map.put("date",date);
			return map;
		}
	};

}
