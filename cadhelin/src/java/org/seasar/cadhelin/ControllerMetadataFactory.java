package org.seasar.cadhelin;

import java.util.HashMap;
import java.util.Map;

import org.seasar.cadhelin.converter.ConverterFactoryImpl;
import org.seasar.framework.container.ComponentDef;
import org.seasar.framework.container.S2Container;

public class ControllerMetadataFactory {
	private Map<String,ControllerMetadata> controllers 
			= new HashMap<String,ControllerMetadata>();
	private Map<Class,ControllerMetadata> classMap
			= new HashMap<Class,ControllerMetadata>();
	private String defaultRole;
	public void setDefaultRole(String defaultRole) {
		this.defaultRole = defaultRole;
	}
	private ActionFilter[] filters = new ActionFilter[0];	
	private void setFilters(Object[] filters) {
		ActionFilter[] f = new ActionFilter[filters.length];
		System.arraycopy(filters,0,f,0,f.length);
		this.filters = f;
	}
	public ControllerMetadataFactory(S2Container container) {
		int size = container.getComponentDefSize();
		ConverterFactoryImpl converter = 
			(ConverterFactoryImpl) container.getComponent(ConverterFactoryImpl.class);
		if(container.hasComponentDef("sessionManager")){
			ComponentDef componentDef = container.getComponentDef("sessionManager");
			converter.addConverters(
					new SessionManagerConverter(
							container,
							new Object[]{componentDef.getComponentClass()}));
		}
		Object[] f = container.findComponents(ActionFilter.class);
		setFilters(f);
		for(int i=0;i<size;i++){
			ComponentDef def = container.getComponentDef(i);
			String componentName = def.getComponentName();
			if(componentName==null){
				componentName = def.getComponentClass().getSimpleName();
			}
			if(componentName.endsWith("Controller")){
				componentName = componentName.replace("Controller","");
				ControllerMetadata controllerMetadata = 
					new ControllerMetadata(componentName,defaultRole,def,converter,filters);
				controllers.put(
						componentName,
						controllerMetadata);
				classMap.put(def.getComponentClass(),controllerMetadata);
			}
		}
	}

	public ControllerMetadata getControllerMetadata(String controllerName) {
		return controllers.get(controllerName);
	}

	public ControllerMetadata getControllerMetadata(Class clazz) {
		while(clazz != null){
			ControllerMetadata controllerMetadata = classMap.get(clazz);
			if(controllerMetadata!=null){
				return controllerMetadata;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}
}
