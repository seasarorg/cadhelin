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
package org.seasar.cadhelin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Converter extends Cloneable{
	public void setDefaultValue(String defaultValue);
	public Map<String,Object> getMessageArguments();
	public void addValidater(Validator validater);
	public boolean isRequired();
	public void setRequired(boolean required);

	public String getParameterName();
	public void setParameterName(String parameterName);
	
	public Class getParameterType();
	public void setParameterType(Class parameterType);
	
	/**
	 * ConverterÇÃKey
	 * @return
	 */
	public Object[] getConverterKey();
	
	/**
	 * @param name		ÉpÉâÉÅÅ[É^ñº
	 * @param request
	 * @param message
	 * @return
	 */
	public Object convert(
			HttpServletRequest request,
			Map<String,Message> message);
	
	public Validator[] getValidators();
	public Converter[] getChildConvertors();
	public void removeValidator(int i);
}
