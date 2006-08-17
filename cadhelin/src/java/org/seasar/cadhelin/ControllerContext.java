package org.seasar.cadhelin;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class ControllerContext {
	
	private static ThreadLocal<ControllerContext> context = 
		new ThreadLocal<ControllerContext>();
	
	public static void setContext(ControllerContext ctx){
		context.set(ctx);
	}
	public static ControllerContext getContext(){
		return context.get();
	}

	public abstract void setViewName(String viewName);

	public abstract String getActionName();

	public abstract String getControllerName();

	public abstract String getCookie(String key);

	public abstract void setCookie(String key, String value);


	public abstract void setResponseHeader(String key, String value);

	public abstract boolean isFirstAction();

	public abstract void sendObject(String key, Object obj);

	public abstract void sendMessage(String key, Message message);

	public abstract void addMessage(String key, Message message);

	public abstract void addMessage(Map<String, Message> messages);

	public abstract int getErrorCount();

	public abstract void addError(String key, Message message);

	public abstract void addError(Map<String, Message> messages);

	public abstract HttpServletRequest getRequest();

	public abstract HttpServletResponse getResponse();

	public abstract Object getSessionManager();

	public abstract Writer createWriter(String contentType) throws IOException;

	public abstract Writer createWriter(String fileName, String contentType)
			throws IOException;

	public abstract OutputStream createOutputStream(String contentType)
			throws IOException;

	public abstract OutputStream createOutputStream(String fileName,
			String contentType) throws IOException;

	public abstract void invalidateSession();


}