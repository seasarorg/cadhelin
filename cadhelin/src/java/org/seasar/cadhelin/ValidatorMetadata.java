package org.seasar.cadhelin;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class ValidatorMetadata {
	private Class validatorClass;
	private BeanInfo validatorBeanInfo;
	private PropertyMetadata[] propertyMetadatas;
	public ValidatorMetadata(Validator validator){
		try {
			this.validatorClass = validator.getClass();
			validatorBeanInfo = Introspector.getBeanInfo(validatorClass);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		List<PropertyMetadata> list = new ArrayList<PropertyMetadata>();
		for (PropertyDescriptor descriptor : validatorBeanInfo.getPropertyDescriptors()) {
			list.add(new PropertyMetadata(validator,descriptor));
		}
		propertyMetadatas = list.toArray(new PropertyMetadata[list.size()]);
	}
	public Validator createValidator(){
		try {
			return (Validator) validatorClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public String getValidatorName(){
		return validatorBeanInfo.getBeanDescriptor().getName();
	}
	public String getShortDescription(){
		return validatorBeanInfo.getBeanDescriptor().getShortDescription();
	}
	public PropertyMetadata[] getPropertyMetadatas(){
		return propertyMetadatas;
	}
}
