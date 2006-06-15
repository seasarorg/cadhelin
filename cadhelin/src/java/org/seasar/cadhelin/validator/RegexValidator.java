package org.seasar.cadhelin.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validate;
import org.seasar.cadhelin.Validator;

public class RegexValidator extends AbstractValidator {
	private String errorMessageKey;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	
	private Pattern pattern;
	public RegexValidator() {
	}
	public RegexValidator(String errorMessageKey,String[] arguments){
		this.errorMessageKey = (errorMessageKey!=null)? 
				errorMessageKey :
				"error.string.regex" ;
		String regex = getString(arguments,"regex",null);
		if(regex!=null){
			pattern = Pattern.compile(regex);
		}
	}
	public Validator createValidater(Validate validate) {
		return new RegexValidator(errorMessageKey,validate.args());
	}
	public String getValidaterKey() {
		return "regex";
	}

	public boolean validate(String name, Object value,
			Map<String, Message> errors) {
		if (value instanceof String) {
			String string = (String) value;
			if(!pattern.matcher(string).matches()){
				errors.put(name,
						new Message(errorMessageKey + "." + name,
								messageArguments));				
				return true;
			}
		}
		return false;
	}

}
