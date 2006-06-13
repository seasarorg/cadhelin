package org.seasar.cadhelin;

public class Message {
	private String key;
	public Message(String key){
		this.key = key;
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
