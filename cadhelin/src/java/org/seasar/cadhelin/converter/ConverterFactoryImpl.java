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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.Validator;
import org.seasar.cadhelin.annotation.Validate;

public class ConverterFactoryImpl implements ConverterFactory {
	private Map<Object,Converter> converters 
		= new HashMap<Object,Converter>();
	private Map<String,Validator> validaters 
		= new HashMap<String,Validator>();
	public void addConverters(Converter converter){
		Object[] converterKey = converter.getConverterKey();
		for (Object key : converterKey) {
			this.converters.put(key,converter);
		}
	}
	public void addConverters(Object[] converters){
		for (Object c : converters) {
			Converter converter = (Converter) c; 
			Object[] converterKey = converter.getConverterKey();
			for (Object key : converterKey) {
				this.converters.put(key,converter);				
			}
		}		
	}
	public void setValidators(Object[] validaters){
		for (Object c : validaters) {
			Validator validater = (Validator) c; 
			this.validaters.put(validater.getValidaterKey(),validater);
		}		
	}
	public Converter getConverter(
			String parameterName,
			String validaterName,
			Class targetClass,
			Param validater){
		Converter converter = converters.get(validaterName);
		if(converter==null){
			return null;
		}
		return converter.createInstance(parameterName,targetClass,validater);
	}
	public Converter getConverter(
			String parameterName,
			Class targetClass,
			Param validater){
		Converter converter = searchConverter(targetClass);
		if(converter==null){
			return null;
		}
		return converter.createInstance(parameterName,targetClass,validater);
	}
	public Converter findConverter(String parameterName,
			Class targetClass,
			Param param){
		Converter converter = null;
		if(param!=null && param.name().length()>0){
			converter = getConverter(parameterName,param.name(),targetClass,param);				
		}else{
			converter = getConverter(parameterName,parameterName,targetClass,param);								
		}
		if(converter!=null){
			return converter;
		}
		return getConverter(parameterName,targetClass,param);
	}
	public Converter[] createConverters(Method method,String[] parameterNames){
		Class<?>[] parameterTypes = method.getParameterTypes();
		Converter[] converters = new Converter[parameterTypes.length];
		Annotation[][] annot = method.getParameterAnnotations();
		for(int i=0;i<parameterTypes.length;i++){
			Param param = findParam(annot[i]);
			Converter converter = findConverter(parameterNames[i],parameterTypes[i],param);
			setUpValidater(converter,param);
			converters[i] = converter;
		}
		return converters;
	}
	public void setUpValidater(Converter converter,Param param){
		if(param == null){
			return;
		}
		for (Validate validate : param.validate()) {
			Validator validater = validaters.get(validate.name());
			if(validater==null){
				continue;
			}
			validater = validater.createValidater(validate);
			if(validater!=null){
				converter.addValidater(validater);
			}
		}
	}
	private Param findParam(Annotation[] annotations) {
		for (Annotation annotation : annotations) {
			if (annotation instanceof Param) {
				return (Param) annotation;
			}
		}
		return null;
	}
	private Converter searchConverter(Class targetClass){
		if(targetClass==null){
			return null;
		}
		Converter converter = converters.get(targetClass);
		if(converter==null){
			return searchConverter(targetClass.getSuperclass());
		}
		return converter;
	}
}