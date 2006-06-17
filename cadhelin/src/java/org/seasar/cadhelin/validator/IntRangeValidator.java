/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.cadhelin.validator;

import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validator;
import org.seasar.cadhelin.annotation.Validate;

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
