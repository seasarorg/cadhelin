package org.seasar.cadhelin.validator;

import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validate;
import org.seasar.cadhelin.Validator;

public class IntRangeValidator extends AbstractValidator{
	private String errorMessageKey;
	private Integer minValue;
	private Integer maxValue;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	public IntRangeValidator(){
	}
	public IntRangeValidator(String errorMessageKey,String[] arguments){
		this.errorMessageKey = (errorMessageKey!=null)? 
				errorMessageKey :
				"error.integer.outofrange" ;
		minValue = getInteger(arguments,"min");
		if(minValue!=null){
			messageArguments.put("min",minValue.toString());						
		}
		maxValue = getInteger(arguments,"max");
		if(maxValue!=null){
			messageArguments.put("max",maxValue.toString());						
		}
	}
	public Validator createValidater(Validate validate) {
		return new IntRangeValidator(errorMessageKey,validate.args());
	}
	public String getValidaterKey() {
		return "intRange";
	}
	public boolean validate(String name, Object value, Map<String, Message> errors) {
		if (value instanceof Number) {
			Number number = (Number) value;
			if(minValue!=null && minValue.intValue() > number.intValue()){
				errors.put(name,new Message(errorMessageKey + "." + name,messageArguments));
				return true;
			}
			if(maxValue!=null && maxValue.intValue() < number.intValue()){
				errors.put(name,new Message(errorMessageKey + "." + name,messageArguments));
				return true;
			}
		}
		return false;
	}
}
