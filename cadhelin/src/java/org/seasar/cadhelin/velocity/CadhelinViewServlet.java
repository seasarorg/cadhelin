package org.seasar.cadhelin.velocity;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.runtime.RuntimeConstants;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class CadhelinViewServlet extends VelocityViewServlet {
	private S2Container container = 
		SingletonS2ContainerFactory.getContainer();
	protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
		Context context = super.createContext(request, response);
		return new SeasarContext(container,context);
	}
	@Override
	protected void initVelocity(ServletConfig config) throws ServletException {
		VelocityEngine velocity = new VelocityEngine();
        setVelocityEngine(velocity);

        // register this engine to be the default handler of log messages
        // if the user points commons-logging to the LogSystemCommonsLog
        LogSystemCommonsLog.setVelocityEngine(velocity);

        velocity.setApplicationAttribute(SERVLET_CONTEXT_KEY, getServletContext());

        velocity.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, 
                "org.seasar.cadhelin.velocity.CadhelinUberspectImpl");

        // default to servletlogger, which logs to the servlet engines log
        velocity.setProperty(RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS, 
                             ServletLogger.class.getName());

        // by default, load resources with webapp resource loader
        velocity.setProperty(RuntimeConstants.RESOURCE_LOADER, "webapp");
        velocity.setProperty("webapp.resource.loader.class", 
                             WebappLoader.class.getName());

        // Try reading an overriding Velocity configuration
        try
        {
            ExtendedProperties p = loadConfiguration(config);
            velocity.setExtendedProperties(p);
        }
        catch(Exception e)
        {
            getServletContext().log("VelocityViewServlet: Unable to read Velocity configuration file: "+e);
            getServletContext().log("VelocityViewServlet: Using default Velocity configuration.");
        }   

        // now all is ready - init Velocity
        try
        {
            velocity.init();
            setVelocityEngine(velocity);
        }
        catch(Exception e)
        {
            getServletContext().log("VelocityViewServlet: PANIC! unable to init() - "+e);
            throw new ServletException(e);
        }
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
