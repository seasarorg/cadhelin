package org.seasar.cadhelin.converter;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validater;

public class IntConverter extends AbstractConverter {
	private static final String ERROR_KEY_INTEGER_REQUIRED = "error.converter.integer.required";
	private static final String ERROR_KEY_INTEGER_FORMAT = "error.converter.integer.format";
	private static final String ERROR_KEY_INTEGER_OUTOF_RANGE = "error.converter.integer.outofrang";
	private boolean required = true;
	private Integer min;
	private Integer max;
	public IntConverter() {
		super(new Object[]{int.class,Integer.class});
	}
	public Converter createInstance(String parameterName, Class targetClass, Validater validater) {
		IntConverter converter = null;
		try {
			converter = (IntConverter) clone();
			converter.parameterName = parameterName;
			if(validater!=null){
				converter.required = validater.required();				
			}
			return converter;
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		String str = request.getParameter(parameterName);
		if(str==null){
			if(required){
				messages.put(parameterName,new Message(ERROR_KEY_INTEGER_REQUIRED+"."+parameterName));
			}
			return null;
		}
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			messages.put(parameterName,new Message(ERROR_KEY_INTEGER_FORMAT+"."+parameterName));
			return str;
		}
	};

}
