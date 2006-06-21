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

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class IntArrayConverter extends AbstractConverter {
	private static final String ERROR_KEY_REQUIRED = "error.converter.intarray.required"; 
	private boolean required = true;
	public IntArrayConverter() {
		super(new Object[]{int[].class,Integer[].class});
	}
	public IntArrayConverter(Object[] keys,String parameterName,Param validater){
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
		return new IntArrayConverter(converterKeys,parameterName,validater);
	}
	
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> message) {
		String[] strs = request.getParameterValues(parameterName);
		if((strs == null || strs.length == 0) && required){
			message.put(parameterName,new Message(ERROR_KEY_REQUIRED +"." + parameterName));
			return null;
		}
		Integer[] ints = new Integer[strs.length];
		int i=0;
		for (String str : strs) {
			try {
				ints[i] = Integer.valueOf(str);
			} catch (NumberFormatException e) {
			}
			i++;
		}
		validate(ints,message);
		return ints;
	};

}
