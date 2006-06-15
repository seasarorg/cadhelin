package org.seasar.cadhelin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Converter extends Cloneable{
	public void addValidater(Validator validater);
	public Converter createInstance(
			String parameterName,
			Class targetClass,
			Param validater);
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
