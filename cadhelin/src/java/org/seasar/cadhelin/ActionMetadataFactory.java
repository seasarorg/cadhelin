package org.seasar.cadhelin;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

public interface ActionMetadataFactory {
	/**
	 * Requestから対応するActionMetadataを取得します。
	 * @param request
	 * @return
	 */
	public ActionMetadata getActionMetadata(HttpServletRequest request);

	/**
	 * methodから対応するActionMetadataを取得します。
	 * @param method
	 * @return
	 */
	public ActionMetadata getActionMetadata(Method method);
	/**
	 * Controller名とメソッド名から対応するActionMetadataを取得します。
	 * @param controllerName
	 * @param methodName
	 * @return
	 */
	public ActionMetadata getActionMetadata(String controllerName, String methodName);

	/**
	 * Controller名とAction名とHTTPリクエストのメソッドから対応するActionMetadataを取得します。
	 * @param controllerName
	 * @param actionName
	 * @param method
	 * @return
	 */
	public ActionMetadata getActionMetadata(String controllerName, String actionName, HttpMethod method);


}