package org.seasar.cadhelin.converter;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.util.StringUtil;

public class IntConverter extends AbstractConverter {
	private String errorMessageKey;
	private String defaultValue;
	private boolean required = true;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	public IntConverter() {
		super(new Object[]{int.class,Integer.class});
	}
	public IntConverter(Object[] keys,String parameterName,Param param){
		super(keys);
		this.parameterName = parameterName;
		this.errorMessageKey = "error.integer." + parameterName;
		if(param != null){
			this.defaultValue = param.defaultVal();
			required = param.required();
		}
		
	}
	public Converter createInstance(String parameterName, Class targetClass, Param validater) {
		return new IntConverter(converterKeys,parameterName,validater);
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		String str = request.getParameter(parameterName);
		if(StringUtil.isNullOrEmpty(str)){
			str = defaultValue;
		}
		if(StringUtil.isNullOrEmpty(str)){
			if(required){
				messages.put(parameterName,
						new Message(errorMessageKey+".required",
								messageArguments));
			}
			return null;
		}
		try {
			Integer value = Integer.valueOf(str);
			validate(value,messages);
			return value;
		} catch (NumberFormatException e) {
			messages.put(parameterName,
					new Message(errorMessageKey+".format",messageArguments));
			return str;
		}
	};

}
