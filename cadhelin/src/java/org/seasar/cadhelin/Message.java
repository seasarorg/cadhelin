package org.seasar.cadhelin;

import java.util.HashMap;
import java.util.Map;

public class Message {
	private String key;
	private Map<String,String> arguments = new HashMap<String,String>();
	
	public Message(String key){
		this.key = key;
	}
	public Message(String key,Map<String,String> arguments){
		this.key = key;
		this.arguments = arguments;
	}
	public Map<String, String> getArguments() {
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
