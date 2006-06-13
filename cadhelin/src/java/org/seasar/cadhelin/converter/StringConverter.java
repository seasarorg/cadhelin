package org.seasar.cadhelin.converter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validater;

public class StringConverter extends AbstractConverter {
	private static final String ERROR_KEY_REQUIRED = "error.integer.required"; 
	private boolean required = true;
	private boolean trim = false;
	public StringConverter() {
		super(new Object[]{String.class});
	}
	public Converter createInstance(
			String parameterName, 
			Class targetClass, 
			Validater validater) {
		StringConverter converter = null;
		try {
			converter = (StringConverter) clone();
			converter.parameterName = parameterName;
			if(validater!=null){
				converter.required = validater.required();
				for (String arg : validater.arg()) {
					if(arg.equals("trim")){
						converter.trim = true;
					}
				}				
			}
			return converter;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> message) {
		String str = request.getParameter(parameterName);
		if(str == null && required){
			message.put(parameterName,new Message(ERROR_KEY_REQUIRED +"." + parameterName));
			return null;
		}
		if(trim){
			return str.trim();
		}
		return str;
	};

}
