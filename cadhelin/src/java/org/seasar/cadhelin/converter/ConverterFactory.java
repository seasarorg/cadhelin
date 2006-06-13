package org.seasar.cadhelin.converter;

import java.lang.reflect.Method;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Validater;

public interface ConverterFactory {
	public Converter[] createConverters(Method method,String[] parameterNames);
	
	public Converter getConverter(
			String parameterName,
			String validaterName,
			Class targetClass,
			Validater validater);
	
	public Converter getConverter(
			String parameterName,
			Class targetClass,
			Validater validater);

}