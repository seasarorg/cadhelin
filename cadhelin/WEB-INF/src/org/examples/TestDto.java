package org.examples;

import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.annotation.Validate;
import org.seasar.cadhelin.validator.IntRangeValidator;

public class TestDto {
	private int lhs;
	private int rhs;
	@Param(required=true,validate=@Validate(value=IntRangeValidator.class,args={"max=10"}))
	public int getLhs() {
		return lhs;
	}
	public void setLhs(int lhs) {
		this.lhs = lhs;
	}
	public int getRhs() {
		return rhs;
	}
	@Param(required=true)
	public void setRhs(int rhs) {
		this.rhs = rhs;
	}
	
}
