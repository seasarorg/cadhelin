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
import org.seasar.cadhelin.ValidatorMetadata;

public abstract class AbstractConverter implements Converter {
	protected Map<String,Object> messageArguments = new HashMap<String,Object>();
	protected List<Validator> validaters = 
		new ArrayList<Validator>();
	protected boolean required = false;
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	protected String parameterName;
	public String getParameterName() {
		return parameterName;
	}
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}
	protected Class parameterType;
	public Class getParameterType() {
		return parameterType;
	}
	public void setParameterType(Class parameterType) {
		this.parameterType = parameterType;
	}
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
	public void setValidators(Validator[] vals){
		validaters.clear();
		for (Validator validator : vals) {
			validaters.add(validator);
		}
	}
	public Validator[] getValidators() {
		return validaters.toArray(new Validator[validaters.size()]);
	}
	public ValidatorMetadata[] getValidatorMetadata() {
		ValidatorMetadata[] meta = new ValidatorMetadata[validaters.size()];
		int i=0;
		for (Validator validator : validaters) {
			meta[i++] = new ValidatorMetadata(validator);
		}
		return meta;
	}
	public Converter[] getChildConvertors() {
		return new Converter[0];
	}
}
