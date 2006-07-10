package org.seasar.cadhelin;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class ValidatorMetadata {
	private BeanInfo validatorBeanInfo;
	private PropertyMetadata[] propertyMetadatas;
	public ValidatorMetadata(Validator validator){
		try {
			validatorBeanInfo = Introspector.getBeanInfo(validator.getClass());
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		List<PropertyMetadata> list = new ArrayList<PropertyMetadata>();
		for (PropertyDescriptor descriptor : validatorBeanInfo.getPropertyDescriptors()) {
			list.add(new PropertyMetadata(validator,descriptor));
		}
		propertyMetadatas = list.toArray(new PropertyMetadata[list.size()]);
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
