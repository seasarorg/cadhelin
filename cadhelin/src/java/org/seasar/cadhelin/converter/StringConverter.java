package org.seasar.cadhelin.converter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class StringConverter extends AbstractConverter {
	private static final String ERROR_KEY_REQUIRED = "error.converter.string.required"; 
	private boolean required = true;
	private boolean trim = false;
	public StringConverter() {
		super(new Object[]{String.class});
	}
	public StringConverter(Object[] keys,String parameterName,Param validater){
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
		return new StringConverter(converterKeys,parameterName,validater);
	}
	
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> message) {
		String str = request.getParameter(parameterName);
		if((str == null ) && required){
			message.put(parameterName,new Message(ERROR_KEY_REQUIRED +"." + parameterName));
			return null;
		}
		if(trim){
			return str.trim();
		}
		return str;
	};

}
