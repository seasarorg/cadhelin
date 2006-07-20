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
package examples;

import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.annotation.ResultName;
import org.seasar.cadhelin.impl.ControllerContextImpl;

public class LogonControllerImpl {
	UserManager userManager;
	public LogonControllerImpl(UserManager userManager) {
		this.userManager = userManager;
	}
	public void logonForm(){
	}
	@ResultName("user")
	public User logon(SessionManager sessionManager,String userName,String password){
		User user = userManager.authenticate(userName,password);
		if(user==null){
			ControllerContext context = 
				ControllerContextImpl.getContext();
			context.addMessage("logon",new Message("error.logon"));
			logonForm();
			return null;
		}
		sessionManager.setUser(user);
		return user;
	}
}
