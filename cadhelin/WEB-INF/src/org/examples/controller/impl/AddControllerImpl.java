package org.examples.controller.impl;

import org.examples.controller.AddController;
import org.seasar.cadhelin.annotation.Default;

public class AddControllerImpl implements AddController {
	@Default
	public int showAdd(int lhs,int rhs){
		return lhs + rhs;
	}
}
