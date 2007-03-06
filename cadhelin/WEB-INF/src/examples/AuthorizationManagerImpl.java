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

import org.examples.SessionManager;
import org.seasar.cadhelin.AuthorizationManager;

public class AuthorizationManagerImpl implements AuthorizationManager {

	public boolean authorized(Object sessionManager, String role) {
		if(role==null || role.length()==0){
			return true;
		}
		if (sessionManager instanceof SessionManager) {
			SessionManager sessionManager2 = (SessionManager) sessionManager;
			if("admin".equals(role)){
				User user = sessionManager2.getUser();
				if(user==null){
					return false;
				}
				return user.getRole().equals("admin");
			}
		}
		return false;
	}

}
