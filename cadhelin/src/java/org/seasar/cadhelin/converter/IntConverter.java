package org.seasar.cadhelin.converter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;

public class IntConverter extends AbstractConverter {
	private static final String ERROR_KEY_INTEGER_REQUIRED = "error.converter.integer.required";
	private static final String ERROR_KEY_INTEGER_FORMAT = "error.converter.integer.format";
	private boolean required = true;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	public IntConverter() {
		super(new Object[]{int.class,Integer.class});
	}
	public IntConverter(Object[] keys,String parameterName,Param validater){
		super(keys);
		this.parameterName = parameterName;
		if(validater!=null){
			required = validater.required();

		}
	}
	public Converter createInstance(String parameterName, Class targetClass, Param validater) {
		return new IntConverter(converterKeys,parameterName,validater);
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		String str = request.getParameter(parameterName);
		if(str==null || str.length() == 0){
			if(required){
				messages.put(parameterName,new Message(ERROR_KEY_INTEGER_REQUIRED+"."+parameterName,messageArguments));
			}
			return null;
		}
		try {
			Integer value = Integer.valueOf(str);
			validate(value,messages);
			return value;
		} catch (NumberFormatException e) {
			messages.put(parameterName,new Message(ERROR_KEY_INTEGER_FORMAT+"."+parameterName,messageArguments));
			return str;
		}
	};

}
