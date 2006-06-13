package org.seasar.cadhelin.velocity;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.velocity.context.Context;
import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class CadhelinViewServlet extends VelocityViewServlet {
	private S2Container container = 
		SingletonS2ContainerFactory.getContainer();
	protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
		Context context = super.createContext(request, response);
		return new SeasarContext(container,context);
	}
}
class SeasarContext implements Context{
	private S2Container container;
	private Context context;
	
	public SeasarContext(S2Container container,Context context) {
		this.container = container;
		this.context = context;
	}
	public boolean containsKey(Object key) {
		return context.containsKey(key);
	}
	public Object get(String key) {
		Object value = context.get(key);
		if(value!=null){
			return value;
		}
		if(container.hasComponentDef(key)){
			return container.getComponent(key);			
		}
		return null;
	}
	public Object[] getKeys() {
		return context.getKeys();
	}
	public Object put(String key, Object value) {
		return context.put(key,value);
	}
	
	public Object remove(Object arg0) {
		return context.remove(arg0);
	}
}
