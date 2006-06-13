package org.seasar.cadhelin.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Validater;

public class ConverterFactoryImpl implements ConverterFactory {
	private Map<Object,Converter> converters 
		= new HashMap<Object,Converter>();
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
			Validater validater){
		Converter converter = converters.get(validaterName);
		if(converter==null){
			return null;
		}
		return converter.createInstance(parameterName,targetClass,validater);
	}
	public Converter getConverter(
			String parameterName,
			Class targetClass,
			Validater validater){
		Converter converter = searchConverter(targetClass);
		if(converter==null){
			return null;
		}
		return converter.createInstance(parameterName,targetClass,validater);
	}
	public Converter[] createConverters(Method method,String[] parameterNames){
		Class<?>[] parameterTypes = method.getParameterTypes();
		Converter[] converters = new Converter[parameterTypes.length];
		Annotation[][] annot = method.getParameterAnnotations();
		for(int i=0;i<parameterTypes.length;i++){
			Converter converter = null;
			Validater validater = findValidater(annot[i]);
			if(validater!=null && validater.name().length()>0){
				converter = getConverter(parameterNames[i],validater.name(),parameterTypes[i],validater);				
			}else{
				converter = getConverter(parameterNames[i],parameterNames[i],parameterTypes[i],validater);								
			}
			if(converter!=null){
				converters[i] = converter;
				continue;
			}
			converters[i] = getConverter(parameterNames[i],parameterTypes[i],validater);
		}
		return converters;
	}
	private Validater findValidater(Annotation[] annotations) {
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