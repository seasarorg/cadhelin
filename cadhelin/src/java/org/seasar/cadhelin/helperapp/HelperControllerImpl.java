package org.seasar.cadhelin.helperapp;

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
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerMetadata;
import org.seasar.cadhelin.ControllerMetadataFactory;
import org.seasar.cadhelin.ControllerServlet;
import org.seasar.cadhelin.Converter;
import org.seasar.cadhelin.ConverterMetadata;
import org.seasar.cadhelin.PropertyMetadata;
import org.seasar.cadhelin.ValidatorFactory;
import org.seasar.cadhelin.ValidatorMetadata;
import org.seasar.cadhelin.annotation.Default;
import org.seasar.cadhelin.annotation.Dispatch;
import org.seasar.cadhelin.util.StringUtil;

public class HelperControllerImpl {
	private String sourcePrefix = "WEB-INF/src";
	private ValidatorFactory validatorFactory;
	private Map<Class,Editor> editors = new HashMap<Class,Editor>();
	public HelperControllerImpl(ValidatorFactory validatorFactory) {
		this.validatorFactory = validatorFactory;
		editors.put(Integer.class,new IntegerEditor());
		editors.put(int.class,new IntegerEditor());
		editors.put(String.class,new StringEditor());
		editors.put(Boolean.class,new BooleanEditor());
		editors.put(boolean.class,new BooleanEditor());
	}
	private ControllerMetadataFactory getControllerMetadataFactory(){
		HttpServletRequest request = ControllerContext.getContext().getRequest();
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
	public void doSaveController(String controllerName) throws IOException{
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ServletContext context = ControllerContext.getContext().getRequest().getSession().getServletContext();
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
	public ConverterMetadata showParameterConvertor(String controllerName,String methodName,int paramNum){
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(methodName);
		ConverterMetadata metadata = action.getConverterMetadata()[paramNum-1];
		return metadata;
	}
	@Dispatch(actionName="parameterConvertor",key="addValidator")
	public void doAddValidator(String controllerName,String methodName,int paramNum,String validator){
		if(!StringUtil.isNullOrEmpty(validator)){
			String[] strs = validator.split("_");
			if(strs.length==2){
				ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
				ActionMetadata action = controllerMetadata.getAction(methodName);
				Converter converter = action.getConverter(strs[0]);
				List<ValidatorMetadata> validators = validatorFactory.getValidatorsFor(converter.getParameterType());
				converter.addValidater(validators.get(Integer.parseInt(strs[1]) - 1).createValidator());				
			}
		}
		showParameterConvertor(controllerName,methodName,paramNum);
	}
	@Dispatch(actionName="parameterConvertor",key="deleteValidator")
	public void doDeleteValidator(String controllerName,String methodName,String actionName,int paramNum,String validator){
		if(!StringUtil.isNullOrEmpty(validator)){
			String[] strs = validator.split("_");
			if(strs.length==2){
				ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
				ActionMetadata action = controllerMetadata.getAction(methodName);
				Converter converter = action.getConverter(strs[0]);
				converter.removeValidator(Integer.parseInt(strs[1]) - 1);				
			}
		}
		showParameterConvertor(controllerName,methodName,paramNum);
	}
	public void doParameterConvertor(String controllerName,String methodName,String actionName,int paramNum){
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(methodName);
		ConverterMetadata[] converters = action.getConverterMetadata();
		ConverterMetadata converter = converters[paramNum-1];
		setConverterProperties(converter);
		showParameterConvertor(controllerName,methodName,paramNum);
	}
	public Object getEditor(PropertyMetadata pm,String options){
		Editor editor = editors.get(pm.getPropertyType());
		return editor.getEditor(pm,options);
	}
	private void setConverterProperties(ConverterMetadata converter){
		String prefix = converter.getParameterName()+"_";
		Map<String,String[]> request = getString(prefix);
		setProterties(converter.getPropertyMetadatas(),request);
		for (ValidatorMetadata val : converter.getValidatorMetadata()) {
			String vp = prefix + val.getValidatorName() + "_";
			request = getString(vp);
			setProterties(val.getPropertyMetadatas(),request);
		}
		for (ConverterMetadata child : converter.getChildConverterMetadata()) {
			setConverterProperties(child);
		}
		
	}
	private void setProterties(PropertyMetadata[] propertyMetadatas,Map<String,String[]> request){
		for (PropertyMetadata pm : propertyMetadatas) {
			Editor editor = editors.get(pm.getPropertyType());
			if(editor!=null){
				editor.setValue(pm,request.get(pm.getName()));
			}
		}
		
	}
	@SuppressWarnings("unchecked")
	private Map<String,String[]> getString(String prefix){
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
