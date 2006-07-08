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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.Validator;
import org.seasar.cadhelin.annotation.Validate;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;
import org.seasar.framework.mock.servlet.MockHttpServletRequest;
import org.seasar.framework.mock.servlet.MockHttpServletRequestImpl;
import org.seasar.framework.mock.servlet.MockServletContext;
import org.seasar.framework.mock.servlet.MockServletContextImpl;
import org.seasar.framework.util.ConstructorUtil;

public class ConverterFactoryImpl implements ConverterFactory {
	private static final Log LOG = LogFactory.getLog(ConverterFactoryImpl.class);
	private Map<Object,Converter> converters 
		= new HashMap<Object,Converter>();
	public void addConverter(Converter converter){
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
			converter.getMessageArguments().put("name",parameterNames[i]);
			setUpValidater(converter,param);
			converters[i] = converter;
		}
		return converters;
	}
	public void setUpValidater(Converter converter,Param param){
		if(param == null){
			return;
		}
		Map<String, Object> messageArguments = converter.getMessageArguments();
		for (Validate validate : param.validate()) {
			//create validator from Validate annotation
			Class validaterClass = validate.value();
			BeanDesc beanDesc = BeanDescFactory.getBeanDesc(validaterClass);
			Constructor c = beanDesc.getConstructor(new Class[0]);
			Validator<?> validator = (Validator) ConstructorUtil.newInstance(c,new Object[0]);
			validator.setMessageArguments(messageArguments);
			for (String arg : validate.args()) {
				String[] ss = arg.split("=");
				if(ss.length!=2){
					LOG.warn("validator argument for "+validaterClass.getSimpleName() +
							" must be key=value formet("+ arg+")");					
				}
				String key = ss[0];
				String value = ss[1];
				if(!beanDesc.hasPropertyDesc(key)){
					LOG.warn("validate="+validaterClass.getSimpleName() +
							" has no property " + key);
					continue;
				}
				PropertyDesc pd = beanDesc.getPropertyDesc(key);
				if(!pd.hasWriteMethod()){
					LOG.warn("validate="+validaterClass.getSimpleName() +
							" has write property " + key);
					continue;					
				}
				Class pt = pd.getPropertyType();
				if(pt.equals(int.class) || pt.equals(Integer.class)){
					pd.setValue(validator, Integer.valueOf(value));
				}else if(pt.equals(String.class)){
					pd.setValue(validator, value);
				}else if(pt.equals(boolean.class) || pt.equals(Boolean.class)){
					pd.setValue(validator, Boolean.valueOf(value));
				}
				messageArguments.put(key,value);
			}
			if(validator!=null){
				converter.addValidater(validator);
			}
		}
	}
	public MockHttpServletRequest createRequest(Validate validate) {
		MockServletContext context = new MockServletContextImpl("t");
		MockHttpServletRequest request = new MockHttpServletRequestImpl(context,"/t");
		String[] args = validate.args();
		for (String arg : args) {
			String[] ss = arg.split("=");
			request.addParameter(ss[0], ss[1]);
		}
		return request;
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