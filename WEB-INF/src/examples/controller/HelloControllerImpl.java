package examples.controller;

import org.seasar.cadhelin.Action;

public class HelloControllerImpl {
	
	@Action
	public String hellow(){
		return "hello";
	}
	
}
