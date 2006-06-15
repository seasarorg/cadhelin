package org.seasar.cadhelin.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validator;

public abstract class AbstractConverter implements Converter {
	protected List<Validator> validaters = 
		new ArrayList<Validator>();
	protected String parameterName;
	protected Object[] converterKeys;
	public AbstractConverter(Object[] converterKeys){
		this.converterKeys = converterKeys;
	}
	public Object[] getConverterKey() {
		return converterKeys;
	}
	public void validate(Object value,Map<String,Message> messages){
		for (Validator validater : validaters) {
			validater.validate(parameterName,value,messages);
		}
	}
	public void addValidater(Validator validater){
		validaters.add(validater);
	}
}
