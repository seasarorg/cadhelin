package org.seasar.cadhelin.converter;

import org.seasar.cadhelin.Converter;

public abstract class AbstractConverter implements Converter {
	protected String parameterName;
	protected Object[] converterKeys;
	public AbstractConverter(Object[] converterKeys){
		this.converterKeys = converterKeys;
	}
	public Object[] getConverterKey() {
		return converterKeys;
	}

}
