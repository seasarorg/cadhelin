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
package org.seasar.cadhelin.converter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validator;

public abstract class AbstractConverter implements Converter {
	protected Map<String,Object> messageArguments = new HashMap<String,Object>();
	protected List<Validator> validaters = 
		new ArrayList<Validator>();
	protected boolean required = false;

	protected String parameterName;
	protected Object[] converterKeys;
	public AbstractConverter(Object[] converterKeys){
		this.converterKeys = converterKeys;
	}
	public Map<String, Object> getMessageArguments() {
		return messageArguments;
	}
	public Object[] getConverterKey() {
		return converterKeys;
	}
	@SuppressWarnings("unchecked")
	public void validate(Object value,Map<String,Message> messages){
		for (Validator validater : validaters) {
			validater.validate(parameterName,value,messages);
		}
	}
	public void addValidater(Validator validater){
		validaters.add(validater);
	}
}
