package org.examples.controller;

import org.seasar.cadhelin.annotation.ParamNames;

public interface AddController {
	@ParamNames({"lhs","rhs"})
	public abstract int showAdd(int lhs, int rhs);

}