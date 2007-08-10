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
package org.seasar.cadhelin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Message implements Serializable{
	private String key;
	private Map<String,Object> arguments;
	
	public Message(String key){
		this.key = key;
	}
	public Message(String key,Object arg1){
		this.key = key;
		arguments = new HashMap<String,Object>();
		arguments.put("0",arg1);
	}
	public Message(String key,Object arg1,Object arg2){
		this.key = key;
		arguments = new HashMap<String,Object>();
		arguments.put("0",arg1);
		arguments.put("1",arg2);
	}
	public Message(String key,Object arg1,Object arg2,Object arg3){
		this.key = key;
		arguments = new HashMap<String,Object>();
		arguments.put("0",arg1);
		arguments.put("1",arg2);
		arguments.put("2",arg3);
	}
	public Message(String key,Object arg1,Object arg2,Object arg3,Object arg4){
		this.key = key;
		arguments = new HashMap<String,Object>();
		arguments.put("0",arg1);
		arguments.put("1",arg2);
		arguments.put("2",arg3);
		arguments.put("3",arg4);
	}
	public Message(String key,Map<String,Object> arguments){
		this.key = key;
		this.arguments = arguments;
	}
	public Map<String, Object> getArguments() {
		return arguments;
	}
	public String getMessage(){
		return key;
	}
	public String toString() {
		return key;
	}
	public String getKey() {
		return key;
	}
}
