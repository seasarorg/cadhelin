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

public class LengthValidator implements Validator<String> {
	private String errorMessageKey = "error.string.outoflength.";
	private Map<String,Object> messageArguments = 
		new HashMap<String,Object>();
	
	private Integer min;
	private Integer max;
	
	public Integer getMin() {
		return min;
	}
	public Integer getMax() {
		return max;
	}

	public boolean validate(String name, String value,
			Map<String, Message> errors) {
		if (value instanceof String) {
			String string = (String) value;
			if(min != null && min.intValue() > string.length()){
				errors.put(name,new Message(errorMessageKey + name,messageArguments));
				return true;
			}
			if(max!=null && max.intValue() < string.length()){
				errors.put(name,new Message(errorMessageKey + name,messageArguments));
				return true;
			}
		}
		return false;
	}
	public void setMessageArguments(Map<String, Object> messageArguments) {
		this.messageArguments = messageArguments;
	}

}
