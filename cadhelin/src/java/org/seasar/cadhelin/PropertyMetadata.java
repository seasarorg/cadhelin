package org.seasar.cadhelin;

import java.beans.PropertyDescriptor;

public class PropertyMetadata {
	private Validator validator;
	private PropertyDescriptor pd;
	
	public PropertyMetadata(Validator validator, PropertyDescriptor pd) {
		this.validator = validator;
		this.pd = pd;
	}
	public boolean isReadWritable(){
		return pd.getReadMethod()!=null && 
			pd.getWriteMethod()!=null;
	}
	public String getName(){
		return pd.getName();
	}
	public String getShortDescription(){
		return pd.getShortDescription();
	}
	public Object getValue(){
		try {
			return pd.getReadMethod().invoke(validator, new Object[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
