package org.seasar.cadhelin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.seasar.cadhelin.converter.AbstractConverter;
import org.seasar.framework.container.S2Container;

public class SessionManagerConverter extends AbstractConverter {
	private S2Container container;
	public SessionManagerConverter(S2Container container,Object[] key) {
		super(key);
		this.container = container;
	}
	public Converter createInstance(String parameterName, Class targetClass,
			Validater validater) {
		try {
			return (Converter) clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException(e);
		}
	}

	public Object convert(HttpServletRequest request,
			Map<String, Message> message) {
		Object component = container.getComponent("sessionManager");
		return request.getSession().getAttribute("sessionManager");
	}

}
