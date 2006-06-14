package org.seasar.cadhelin.converter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.Validate;
import org.seasar.cadhelin.Validater;

public class ConverterFactoryImpl implements ConverterFactory {
	private Map<Object,Converter> converters 
		= new HashMap<Object,Converter>();
	private Map<String,Validater> validaters 
		= new HashMap<String,Validater>();
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
	public void setValidaters(Object[] validaters){
		for (Object c : validaters) {
			Validater validater = (Validater) c; 
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
	protected Converter findConverter(String parameterName,
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
		}
		return converters;
	}
	protected void setUpValidater(Converter converter,Param param){
		for (Validate validate : param.arg()) {
			Validater validater = validaters.get(validate.name());
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