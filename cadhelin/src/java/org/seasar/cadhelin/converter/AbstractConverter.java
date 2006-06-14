package org.seasar.cadhelin.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Validater;

public abstract class AbstractConverter implements Converter {
	protected List<Validater> validaters = 
		new ArrayList<Validater>();
	protected String parameterName;
	protected Object[] converterKeys;
	public AbstractConverter(Object[] converterKeys){
		this.converterKeys = converterKeys;
	}
	public Object[] getConverterKey() {
		return converterKeys;
	}
	public void validate(Object value,Map<String,Message> messages){
		for (Validater validater : validaters) {
			validater.validate(parameterName,value,messages);
		}
	}
	public void addValidater(Validater validater){
		validaters.add(validater);
	}
}
