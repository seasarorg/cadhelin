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
import java.util.regex.Pattern;

import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validator;

public class RegexValidator implements Validator<String> {
	private String errorMessageKey = "error.string.regex";
	private Map<String,String> messageArguments = 
		new HashMap<String,String>();
	
	private Pattern pattern;
	public RegexValidator() {
	}
	public void setRegex(String regex) {
		pattern = Pattern.compile(regex);
	}

	public boolean validate(String name, String value,
			Map<String, Message> errors) {
		if (value instanceof String) {
			String string = (String) value;
			if(!pattern.matcher(string).matches()){
				errors.put(name,
						new Message(errorMessageKey + name,
								messageArguments));				
				return true;
			}
		}
		return false;
	}
	public void setMessageArguments(Map<String, String> messageArguments) {
		this.messageArguments = messageArguments;
	}

}
