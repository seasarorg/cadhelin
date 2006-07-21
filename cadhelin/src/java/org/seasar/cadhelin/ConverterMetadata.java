package org.seasar.cadhelin;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.List;

public class ConverterMetadata {
	private Converter converter;
	private Class converterClass;
	private BeanInfo converterBeanInfo;
	private PropertyMetadata[] propertyMetadatas;
	public ConverterMetadata(Converter converter){
		try {
			this.converter = converter;
			this.converterClass = converter.getClass();
			converterBeanInfo = Introspector.getBeanInfo(converterClass);
		} catch (IntrospectionException e) {
			e.printStackTrace();
		}
		List<PropertyMetadata> list = new ArrayList<PropertyMetadata>();
		for (PropertyDescriptor descriptor : converterBeanInfo.getPropertyDescriptors()) {
			list.add(new PropertyMetadata(converter,descriptor));
		}
		propertyMetadatas = list.toArray(new PropertyMetadata[list.size()]);
	}
	public Converter createConverter(){
		try {
			return (Converter) converterClass.getConstructor(new Class[]{}).newInstance(new Object[]{});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	public String getName(){
		return converterBeanInfo.getBeanDescriptor().getName();
	}
	public String getShortDescription(){
		return converterBeanInfo.getBeanDescriptor().getShortDescription();
	}
	public ConverterMetadata[] getChildConverterMetadata(){
		Converter[] childConvertors = converter.getChildConvertors();
		ConverterMetadata[] metadatas = new ConverterMetadata[childConvertors.length];
		for(int i=0;i<childConvertors.length;i++){
			metadatas[i] = new ConverterMetadata(childConvertors[i]);
		}
		return metadatas;
	}
	public Object get(String propertyName){
		for (PropertyMetadata propertyMetadata : propertyMetadatas) {
			if(propertyMetadata.getName().equals(propertyName)){
				return propertyMetadata.getValue();
			}
		}
		return null;
	}
	public PropertyMetadata[] getPropertyMetadatas(){
		return propertyMetadatas;
	}
	public ValidatorMetadata[] getValidatorMetadata() {
		Validator[] validators = converter.getValidators();
		ValidatorMetadata[] meta = new ValidatorMetadata[validators.length];
		int i=0;
		for (Validator validator : validators) {
			meta[i++] = new ValidatorMetadata(validator);
		}
		return meta;
	}
	public String getParameterName() {
		return converter.getParameterName();
	}

}
