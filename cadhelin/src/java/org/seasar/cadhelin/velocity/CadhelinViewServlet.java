package org.seasar.cadhelin.velocity;

import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.app.event.EventCartridge;
import org.apache.velocity.app.event.ReferenceInsertionEventHandler;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.RuntimeConstants;
import org.seasar.framework.container.S2Container;
import org.seasar.framework.container.factory.SingletonS2ContainerFactory;

public class CadhelinViewServlet extends VelocityViewServlet {
	private S2Container container = 
		SingletonS2ContainerFactory.getContainer();
	
    protected EventCartridge eventCartridge;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
        initEventCartridge(config);
	}

	protected Context createContext(HttpServletRequest request, HttpServletResponse response) {
		SeasarContext context = new SeasarContext(getVelocityEngine(), request, response, getServletContext(),container);
    	eventCartridge.attachToContext(context);
		return context;
	}
    protected void initEventCartridge(ServletConfig config) throws ServletException {
        eventCartridge = new EventCartridge();
        eventCartridge.addEventHandler(new ReferenceInsertionEventHandler() {
                public Object referenceInsert(String reference, Object value) {
                    if (value == null || value instanceof InsertAsIs) {
                        return value;
                    } else {
                        return StringEscapeUtils.escapeHtml(value.toString());
                    }
                }
            });
    }
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
	protected void performMerge(Template template, Context context, Writer writer) 
	throws ResourceNotFoundException, ParseErrorException, MethodInvocationException, Exception {
		context.put("velocityWriter",writer);
		super.performMerge(template, context, writer);
	}
}
class SeasarContext extends ChainedContext{
	private S2Container container;
	
	public SeasarContext(
			VelocityEngine velocityEngine, 
			HttpServletRequest request, 
			HttpServletResponse response, 
			ServletContext application, 
			S2Container container) {
		super(velocityEngine,request,response,application);
		this.container = container;
	}
	public Object internalGet(String key) {
		Object object = super.internalGet(key);
		if(object!=null){
			return object;
		}
		if(container.hasComponentDef(key)){
			return container.getComponent(key);
		}
		return null;
	}
}
