package org.seasar.cadhelin;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface Converter extends Cloneable{
	public Converter createInstance(
			String parameterName,
			Class targetClass,
			Validater validater);
	/**
	 * Converter��Key
	 * @return
	 */
	public Object[] getConverterKey();
	/**
	 * @param name		�p�����[�^��
	 * @param request
	 * @param message
	 * @return
	 */
	public Object convert(
			HttpServletRequest request,
			Map<String,Message> message);
}
