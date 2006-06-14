package org.seasar.cadhelin.validater;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validate;
import org.seasar.cadhelin.Validater;

public class RegexValidater extends AbstractValidater {
	private String errorMessageKey;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	
	private Pattern pattern;
	
	public RegexValidater(String errorMessageKey,String[] arguments){
		errorMessageKey = (errorMessageKey!=null)? 
				errorMessageKey :
				"error.string.regex" ;
		String regex = getString(arguments,"regex",null);
		if(regex!=null){
			pattern = Pattern.compile(regex);
		}
	}
	public Validater createValidater(Validate validate) {
		return new RegexValidater(errorMessageKey,validate.args());
	}
	public String getValidaterKey() {
		return "regex";
	}

	public boolean validate(String name, Object value,
			Map<String, Message> errors) {
		if (value instanceof String) {
			String string = (String) value;
			if(!pattern.matcher(string).matches()){
				
				return true;
			}
		}
		return false;
	}

}
