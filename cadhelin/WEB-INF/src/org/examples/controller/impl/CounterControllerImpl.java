/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.examples.controller.impl;

import org.examples.SessionManager;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.annotation.Dispatch;




public class CounterControllerImpl {
	public void showCount(){
	}
	@Dispatch(actionName="count",key="inc")
	public void doInc(){
		SessionManager sessionManager = 
			(SessionManager) ControllerContext.getContext().getSessionManager();
		int count = sessionManager.getCount();
		sessionManager.setCount(count+1);
		showCount();
	}
	@Dispatch(actionName="count",key="dec")
	public void doDec(){
		SessionManager sessionManager = 
			(SessionManager) ControllerContext.getContext().getSessionManager();
		int count = sessionManager.getCount();
		sessionManager.setCount(count-1);
		showCount();
	}
}
