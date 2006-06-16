package examples;

import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.Validate;

public class AddControllerImpl {
	public int getAdd(
			@Param(
				defaultVal="0",
				validate=@Validate(name="intRange",args="min=0"))int lhs,
			@Param(
				defaultVal="0",
				validate=@Validate(name="intRange",args="min=0"))int rhs){
		return lhs + rhs;
	}
}
