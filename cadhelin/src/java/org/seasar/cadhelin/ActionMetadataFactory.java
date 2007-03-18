package org.seasar.cadhelin;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

public interface ActionMetadataFactory {
	/**
	 * Request����Ή�����ActionMetadata���擾���܂��B
	 * @param request
	 * @return
	 */
	public ActionMetadata getActionMetadata(HttpServletRequest request);

	/**
	 * method����Ή�����ActionMetadata���擾���܂��B
	 * @param method
	 * @return
	 */
	public ActionMetadata getActionMetadata(Method method);
	/**
	 * Controller���ƃ��\�b�h������Ή�����ActionMetadata���擾���܂��B
	 * @param controllerName
	 * @param methodName
	 * @return
	 */
	public ActionMetadata getActionMetadata(String controllerName, String methodName);

	/**
	 * Controller����Action����HTTP���N�G�X�g�̃��\�b�h����Ή�����ActionMetadata���擾���܂��B
	 * @param controllerName
	 * @param actionName
	 * @param method
	 * @return
	 */
	public ActionMetadata getActionMetadata(String controllerName, String actionName, HttpMethod method);


}