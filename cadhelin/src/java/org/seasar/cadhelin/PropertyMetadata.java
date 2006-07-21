package org.seasar.cadhelin;

import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

public class PropertyMetadata {
	private static Map<Class,Object> editors = new HashMap<Class,Object>();
	static{
		editors.put(String.class,String.class);
		editors.put(boolean.class,String.class);
		editors.put(Integer.class,String.class);
	}
	private Object bean;
	private PropertyDescriptor pd;
	private boolean editable;
	
	public PropertyMetadata(Object bean, PropertyDescriptor pd) {
		this.bean = bean;
		this.pd = pd;
		if(pd.getReadMethod()!=null && 			
				pd.getWriteMethod()!=null &&
				editors.containsKey(pd.getPropertyType())){
			this.editable = true;
		}
	}
	public boolean isEditable(){
		return editable;
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
			return pd.getReadMethod().invoke(bean, new Object[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public void setValue(Object value) {
		try {
			pd.getWriteMethod().invoke(bean,new Object[]{value});
		} catch (Exception e) {
		}			
	}
	public Class getPropertyType() {
		return pd.getPropertyType();
	}
}
