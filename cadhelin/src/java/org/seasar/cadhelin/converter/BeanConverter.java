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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

public class BeanConverter extends AbstractConverter {
	private ConverterFactory converterFactory;
	private BeanDesc beanDesc;
	private Converter[] converters;
	private PropertyDesc[] propertyDescs;
	private Constructor constructor;
	public BeanConverter(){
		super(new Object[]{Object.class});
	}
	public BeanConverter(
			ConverterFactory converterFactory,
			String parameterName,
			Class targetClass,
			Param validater){
		super(new Object[]{Object.class});
		this.converterFactory = converterFactory;
		beanDesc = BeanDescFactory.getBeanDesc(targetClass);
		constructor = beanDesc.getSuitableConstructor(new Class[0]);
		int size = beanDesc.getPropertyDescSize();
		ArrayList<Converter> cl = new ArrayList<Converter>();
		ArrayList<PropertyDesc> pl = new ArrayList<PropertyDesc>();
		converters = new Converter[size];
		for(int i=0;i<size;i++){
			PropertyDesc pd = beanDesc.getPropertyDesc(i);
			if(!pd.hasWriteMethod()){
				continue;
			}
			//Validater.nameプロパティが指定されていればその値でConverterを検索。されてなければproperty名Converterを検索
			Param param = AnnotationUtil.getPropertyAnnotation(pd,Param.class);
			Converter converter = this.converterFactory.findConverter(pd.getPropertyName(),pd.getPropertyType(),param);
			if(converter!=null){
				this.converterFactory.setUpValidater(converter,param);
				pl.add(pd);
				cl.add(converter);
			}
		}
		converters = cl.toArray(new Converter[cl.size()]);
		propertyDescs = pl.toArray(new PropertyDesc[pl.size()]);
	}
	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}
	public Converter createInstance(
			String parameterName, 
			Class targetClass, 
			Param validater) {
		return new BeanConverter(converterFactory,parameterName,targetClass,validater);
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		try {
			Object bean = constructor.newInstance(new Object[0]);
			int errorSize = 0;
			for(int i=0;i<converters.length;i++){
				Object value = 
					converters[i].convert(request,messages);
				if(messages.size()> errorSize){
				}else{
					propertyDescs[i].setValue(bean,value);							
				}
				errorSize = messages.size();
			}
			HashMap<String,String> value = new HashMap<String,String>();
			if(messages.size()>0){
				for(int i=0;i<propertyDescs.length;i++){
					PropertyDesc pd = propertyDescs[i];
					value.put(pd.getPropertyName(),request.getParameter(pd.getPropertyName()));
					
				}
				return value;
			}
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	};

}
