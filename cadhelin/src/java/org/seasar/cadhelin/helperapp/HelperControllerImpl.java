package org.seasar.cadhelin.helperapp;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerMetadata;
import org.seasar.cadhelin.ControllerMetadataFactory;
import org.seasar.cadhelin.ControllerServlet;
import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.PropertyMetadata;
import org.seasar.cadhelin.Validator;
import org.seasar.cadhelin.ValidatorMetadata;
import org.seasar.cadhelin.annotation.Default;

public class HelperControllerImpl {
	@Default
	private ControllerMetadataFactory getControllerMetadataFactory(){
		HttpServletRequest request = ControllerContext.getContext().getRequest();
		ServletContext context = request.getSession().getServletContext();
		return (ControllerMetadataFactory) context.getAttribute(ControllerServlet.CONTROLLER_METADATA_NAME);		
	}
	public Collection<ControllerMetadata> showControllers(){
		return getControllerMetadataFactory().getControllerMetadata();
	}
	public ControllerMetadata showController(String controllerName){
		return getControllerMetadataFactory().getControllerMetadata(controllerName);
	}
	public Converter showParameterConvertor(String controllerName,String methodName,String actionName,int paramNum){
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(actionName,methodName);
		
		Converter converter = action.getConverters()[paramNum-1];
		return converter;
	}
	public void showConvertor(String controllerName,String methodName,String actionName,int paramNum){
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(actionName,methodName);
		
		Converter converter = action.getConverters()[paramNum-1];
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		XMLEncoder encoder = new XMLEncoder(os);
		encoder.writeObject(converter);
		encoder.close();
		String string = new String(os.toByteArray());
		System.err.print(string);
		showParameterConvertor(controllerName,methodName,actionName,paramNum);
	}
	public void doParameterConvertor(String controllerName,String methodName,String actionName,int paramNum){
		
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(actionName,methodName);
		Converter[] converters = action.getConverters();
		Converter converter = converters[paramNum-1];
		String prefix = converter.getParameterName()+"_";
		Map<String,String[]> request = getString(prefix);
		if(request.containsKey("required")){
			converter.setRequired(true);
		}else{
			converter.setRequired(false);			
		}
		for (ValidatorMetadata validator : converter.getValidatorMetadata()) {
			String vp = prefix + validator.getValidatorName() + "_";
			request = getString(vp);
			PropertyMetadata[] propertyMetadatas = validator.getPropertyMetadatas();
			for (PropertyMetadata pm : propertyMetadatas) {
				if(pm.isReadWritable()){
					pm.setValue(request.get(pm.getName()));					
				}
			}
		}
		for (Converter child : converter.getChildConvertors()) {
			request = getString(child.getParameterName()+"_");
			if(request.containsKey("required")){
				child.setRequired(true);
			}else{
				child.setRequired(false);			
			}
			
		}
		showParameterConvertor(controllerName,methodName,actionName,paramNum);
	}
	public Map<String,String[]> getString(String prefix){
		Map<String,String[]> result = new HashMap<String,String[]>();
		HttpServletRequest request = ControllerContext.getContext().getRequest();
		Collection<Entry<String,String[]>> map = 
			request.getParameterMap().entrySet();
		for(Entry<String,String[]> entry : map){
			if(entry.getKey().startsWith(prefix)){
				result.put(entry.getKey().substring(prefix.length()),
						entry.getValue());
			}
		}
		return result;
	}
}
