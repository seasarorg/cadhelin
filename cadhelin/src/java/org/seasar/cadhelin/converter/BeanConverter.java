package org.seasar.cadhelin.converter;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.util.AnnotationUtil;
import org.seasar.framework.beans.BeanDesc;
import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.beans.factory.BeanDescFactory;

public class BeanConverter extends AbstractConverter {
	private ConverterFactory converterFactory;
	private BeanDesc beanDesc;
	private Converter[] converters;
	private PropertyDesc[] propertyDescs;
	private Constructor constructor;
	public BeanConverter(){
		super(new Object[]{Object.class});
	}
	public BeanConverter(
			ConverterFactory converterFactory,
			String parameterName,
			Class targetClass,
			Param validater){
		super(new Object[]{Object.class});
		this.converterFactory = converterFactory;
		beanDesc = BeanDescFactory.getBeanDesc(targetClass);
		constructor = beanDesc.getSuitableConstructor(new Class[0]);
		int size = beanDesc.getPropertyDescSize();
		ArrayList<Converter> cl = new ArrayList<Converter>();
		ArrayList<PropertyDesc> pl = new ArrayList<PropertyDesc>();
		converters = new Converter[size];
		for(int i=0;i<size;i++){
			PropertyDesc pd = beanDesc.getPropertyDesc(i);
			if(!pd.hasWriteMethod()){
				continue;
			}
			Converter child = null;
			//Validater.nameプロパティが指定されていればその値でConverterを検索。されてなければproperty名Converterを検索
			Param param = AnnotationUtil.getPropertyAnnotation(pd,Param.class);
			Converter converter = this.converterFactory.findConverter(pd.getPropertyName(),pd.getPropertyType(),param);
			if(child!=null){
				this.converterFactory.setUpValidater(converter,param);
				pl.add(pd);
				cl.add(child);
			}
		}
		converters = cl.toArray(new Converter[cl.size()]);
		propertyDescs = pl.toArray(new PropertyDesc[pl.size()]);
	}
	public void setConverterFactory(ConverterFactory converterFactory) {
		this.converterFactory = converterFactory;
	}
	public Converter createInstance(
			String parameterName, 
			Class targetClass, 
			Param validater) {
		return new BeanConverter(converterFactory,parameterName,targetClass,validater);
	}
	public Object convert(
			HttpServletRequest request, 
			Map<String,Message> messages) {
		try {
			Object bean = constructor.newInstance(new Object[0]);
			int errorSize = 0;
			for(int i=0;i<converters.length;i++){
				Object value = 
					converters[i].convert(request,messages);
				if(messages.size()> errorSize){
				}else{
					propertyDescs[i].setValue(bean,value);							
				}
				errorSize = messages.size();
			}
			HashMap<String,String> value = new HashMap<String,String>();
			if(messages.size()>0){
				for(int i=0;i<propertyDescs.length;i++){
					PropertyDesc pd = propertyDescs[i];
					value.put(pd.getPropertyName(),request.getParameter(pd.getPropertyName()));
					
				}
				return value;
			}
			return bean;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	};

}
