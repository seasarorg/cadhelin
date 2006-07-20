package org.seasar.cadhelin.helperapp;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ControllerMetadata;
import org.seasar.cadhelin.ControllerMetadataFactory;
import org.seasar.cadhelin.ControllerServlet;
import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.PropertyMetadata;
import org.seasar.cadhelin.ValidatorFactory;
import org.seasar.cadhelin.ValidatorMetadata;
import org.seasar.cadhelin.annotation.Default;
import org.seasar.cadhelin.impl.ControllerContextImpl;
import org.seasar.cadhelin.util.StringUtil;

public class HelperControllerImpl {
	private String sourcePrefix = "WEB-INF/src";
	private ValidatorFactory validatorFactory;
	
	public HelperControllerImpl(ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
	}
	private ControllerMetadataFactory getControllerMetadataFactory(){
		HttpServletRequest request = ControllerContextImpl.getContext().getRequest();
		ServletContext context = request.getSession().getServletContext();
		return (ControllerMetadataFactory) context.getAttribute(ControllerServlet.CONTROLLER_METADATA_NAME);		
	}
	@Default
	public Collection<ControllerMetadata> showControllers(){
		return getControllerMetadataFactory().getControllerMetadata();
	}
	public ControllerMetadata showController(String controllerName){
		return getControllerMetadataFactory().getControllerMetadata(controllerName);
	}
	@SuppressWarnings("deprecation")
	public void doSaveController(String controllerName) throws IOException{
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ServletContext context = ControllerContextImpl.getContext().getRequest().getSession().getServletContext();
		File rootPath = new File(context.getRealPath("/"),sourcePrefix);
		String name = controllerMetadata.getControllerClass().getName().replaceAll("\\.","/");
		File file = new File(rootPath,name+".converters");
		FileOutputStream os = null;
		try{
			os = new FileOutputStream(file);
			controllerMetadata.saveConverterSettings(os);
		}finally{
			if(os!=null){os.close();}
		}
		showController(controllerName);
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
	public void doParameterConvertor(String controllerName,String methodName,String actionName,int paramNum,String cmd,String validator){
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(actionName,methodName);
		if(!StringUtil.isNullOrEmpty(validator)){
			if("Add Validator".equals(cmd)){
				String[] strs = validator.split("_");
				Converter converter = action.getConverter(strs[0]);
				List<ValidatorMetadata> validators = validatorFactory.getValidatorsFor(converter.getParameterType());
				converter.addValidater(validators.get(Integer.parseInt(strs[1]) - 1).createValidator());				
			}else if("Delete Validator".equals(cmd)){
				String[] strs = validator.split("_");
				Converter converter = action.getConverter(strs[0]);
				converter.removeValidator(Integer.parseInt(strs[1]) - 1);
			}
		}
		Converter[] converters = action.getConverters();
		Converter converter = converters[paramNum-1];
		String prefix = converter.getParameterName()+"_";
		Map<String,String[]> request = getString(prefix);
		if(request.containsKey("required")){
			converter.setRequired(true);
		}else{
			converter.setRequired(false);			
		}
		for (ValidatorMetadata val : converter.getValidatorMetadata()) {
			String vp = prefix + val.getValidatorName() + "_";
			request = getString(vp);
			PropertyMetadata[] propertyMetadatas = val.getPropertyMetadatas();
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
	@SuppressWarnings("unchecked")
	public Map<String,String[]> getString(String prefix){
		Map<String,String[]> result = new HashMap<String,String[]>();
		HttpServletRequest request = ControllerContextImpl.getContext().getRequest();
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
