package org.seasar.cadhelin.validator;

import org.seasar.cadhelin.Validator;

public abstract class AbstractValidator implements Validator {
	protected Integer getInteger(String[] arguments,String key){
		for (String argument : arguments) {
			if(argument.startsWith(key)){
				String[] strs = argument.split("=");
				if(strs.length>1){
					return Integer.parseInt(strs[1]);
				}				
			}
		}
		return null;
	}
	protected String getString(String[] arguments,String key,String defaultStr){
		for (String argument : arguments) {
			if(argument.startsWith(key)){
				String[] strs = argument.split("=");
				if(strs.length>1){
					return strs[1];
				}				
			}
		}
		return defaultStr;
	}

}
