package org.seasar.cadhelin.helperapp;

import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Collection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.ControllerMetadata;
import org.seasar.cadhelin.ControllerMetadataFactory;
import org.seasar.cadhelin.ControllerServlet;
import org.seasar.cadhelin.Converter;
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
	public void doParameterConvertor(String controllerName,String methodName,String actionName,int paramNum,boolean required){
		ControllerMetadata controllerMetadata = getControllerMetadataFactory().getControllerMetadata(controllerName);
		ActionMetadata action = controllerMetadata.getAction(actionName,methodName);
		
		action.getConverters()[paramNum-1].setRequired(required);
		showParameterConvertor(controllerName,methodName,actionName,paramNum);
	}
}
