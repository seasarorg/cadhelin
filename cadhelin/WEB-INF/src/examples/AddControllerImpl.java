package examples;

import org.seasar.cadhelin.Action;
import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.Validate;

public class AddControllerImpl {
	@Action({"lhs","rhs"})
	public int add(
			@Param(validate=@Validate(name="intRange",args="min=0"))int lhs,
			@Param(validate=@Validate(name="intRange",args="min=0"))int rhs){
		return lhs + rhs;
	}
}
