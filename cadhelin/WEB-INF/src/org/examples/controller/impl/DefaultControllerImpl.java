package org.examples.controller.impl;

import java.util.Map;

import org.seasar.cadhelin.annotation.ParamNames;

public class DefaultControllerImpl {
	@ParamNames("parameters")
	public void showIndex(Map<String,String[]> parameters){
		System.out.println("showIndex invoked.");
	}
}
