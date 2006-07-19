package org.seasar.cadhelin;

import java.beans.PropertyDescriptor;

import org.seasar.cadhelin.util.StringUtil;

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
	public void setValue(String[] string) {
		try {
			if(string==null || string.length<1 || StringUtil.isNullOrEmpty(string[0])){
				pd.getWriteMethod().invoke(validator,new Object[]{null});
			}else{
				pd.getWriteMethod().invoke(validator,new Object[]{Integer.valueOf(string[0])});			
			}
		} catch (Exception e) {
		}
	}
}
