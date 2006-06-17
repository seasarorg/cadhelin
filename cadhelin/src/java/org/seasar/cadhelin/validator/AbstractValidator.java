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
