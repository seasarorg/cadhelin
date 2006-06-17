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

public class IntRangeValidator implements Validator<Integer>{
	private String errorMessageKey = "error.integer.outofrange.";
	private Integer min;
	private Integer max;
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	public void setMessageArguments(Map<String, String> messageArguments) {
		this.messageArguments = messageArguments;
	}
	public IntRangeValidator(){
	}
	public void setMin(Integer min) {
		this.min = min;
	}
	public void setMax(Integer max) {
		this.max = max;
	}

	public boolean validate(String name,Integer value, Map<String, Message> errors) {
		if (value instanceof Number) {
			Number number = (Number) value;
			if(min!=null && min.intValue() > number.intValue()){
				errors.put(name,
						new Message(
								errorMessageKey + name,
								messageArguments));
				return true;
			}
			if(max != null && max.intValue() < number.intValue()){
				errors.put(name,
						new Message(
								errorMessageKey + name,
								messageArguments));
				return true;
			}
		}
		return false;
	}
}
