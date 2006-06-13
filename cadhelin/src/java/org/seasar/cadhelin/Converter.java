package org.seasar.cadhelin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Converter extends Cloneable{
	public Converter createInstance(
			String parameterName,
			Class targetClass,
			Validater validater);
	/**
	 * ConverterのKey
	 * @return
	 */
	public Object[] getConverterKey();
	/**
	 * @param name		パラメータ名
	 * @param request
	 * @param message
	 * @return
	 */
	public Object convert(
			HttpServletRequest request,
			Map<String,Message> message);
}
