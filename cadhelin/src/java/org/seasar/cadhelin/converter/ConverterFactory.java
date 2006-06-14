package org.seasar.cadhelin.converter;

import java.lang.reflect.Method;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Param;

public interface ConverterFactory {
	public Converter[] createConverters(Method method,String[] parameterNames);
	
	public Converter getConverter(
			String parameterName,
			String validaterName,
			Class targetClass,
			Param validater);
	
	public Converter getConverter(
			String parameterName,
			Class targetClass,
			Param validater);

}