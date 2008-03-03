package org.examples.controller.impl;

import org.examples.controller.AddController;
import org.seasar.cadhelin.annotation.ParamNames;

public class AddControllerImpl implements AddController {
	@ParamNames({"lhs","rhs"})
	public int showAdd(int lhs,int rhs){
		return lhs + rhs;
	}
}
