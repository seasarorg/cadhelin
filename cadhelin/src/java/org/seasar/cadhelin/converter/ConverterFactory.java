package org.seasar.cadhelin.converter;

import java.lang.reflect.Method;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Param;

public interface ConverterFactory {
	public Converter[] createConverters(Method method,String[] parameterNames);
	
	public Converter findConverter(String propertyName, Class propertyType, Param param);

	public void setUpValidater(Converter converter, Param param);

}