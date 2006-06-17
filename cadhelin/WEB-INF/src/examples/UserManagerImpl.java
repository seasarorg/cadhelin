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

import java.util.HashMap;
import java.util.Map;

public class UserManagerImpl implements UserManager {
	private Map<String,Pair> users =
		new HashMap<String,Pair>();
	public UserManagerImpl() {
		users.put(
				"administrator",
				new Pair(new User("administrator","admin"),"adadadmin"));
		users.put(
				"demo",
				new Pair(new User("demo","user"),"demodemo"));
	}
	/* (non-Javadoc)
	 * @see examples.UserManager#authenticate(java.lang.String, java.lang.String)
	 */
	public User authenticate(String name,String password){
		Pair pair = users.get(name);
		if(pair==null){
			return null;
		}
		if(pair.user.getRole().equals(password)){
			return pair.user;
		}
		return null;
	}
}
class Pair{
	public User user;
	public String password;
	public Pair(User user, String password) {
		this.user = user;
		this.password = password;
	}
	
}