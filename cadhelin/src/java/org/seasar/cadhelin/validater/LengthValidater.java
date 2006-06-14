package org.seasar.cadhelin.validater;

import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validate;
import org.seasar.cadhelin.Validater;

public class LengthValidater extends AbstractValidater {
	private String errorMessageKey;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	private Integer minLength;
	private Integer maxLength;
	public LengthValidater(String errorMessageKey,String[] arguments){
		errorMessageKey = (errorMessageKey!=null)? 
				errorMessageKey :
				"error.integer.outofrange" ;
		minLength = getInteger(arguments,"min");
		if(minLength!=null){
			messageArguments.put("min",minLength.toString());						
		}
		maxLength = getInteger(arguments,"max");
		if(maxLength!=null){
			messageArguments.put("max",maxLength.toString());						
		}
	}
	public Validater createValidater(Validate validate) {
		return new LengthValidater(errorMessageKey,validate.args());
	}
	public String getValidaterKey() {
		return "length";
	}

	public boolean validate(String name, Object value,
			Map<String, Message> errors) {
		if (value instanceof String) {
			String string = (String) value;
			if(minLength!=null && minLength.intValue() > string.length()){
				errors.put(name,new Message(errorMessageKey + "." + name));
				return true;
			}
			if(maxLength!=null && maxLength.intValue() < string.length()){
				errors.put(name,new Message(errorMessageKey + "." + name));
				return true;
			}
		}
		return false;
	}

}
