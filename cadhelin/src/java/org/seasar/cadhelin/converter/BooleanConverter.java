package org.seasar.cadhelin.converter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class BooleanConverter extends AbstractConverter {
	public BooleanConverter() {
		super(new Object[]{boolean.class,Boolean.class});
	}
	public BooleanConverter(Object[] keys,String parameterName,Param validater){
		super(keys);
		this.parameterName = parameterName;
	}
	public Converter createInstance(String parameterName, Class targetClass, Param validater) {
		return new BooleanConverter(converterKeys,parameterName,validater);
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		Boolean value = Boolean.FALSE;
		String str = request.getParameter(parameterName);
		
		if(str!=null && str.length() > 0){
			value = Boolean.valueOf(str);
		}
		validate(value,messages);
		return value;
	};

}
