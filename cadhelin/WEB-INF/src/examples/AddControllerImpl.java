package examples;

import org.seasar.cadhelin.Param;
import org.seasar.cadhelin.annotation.Default;
import org.seasar.cadhelin.annotation.Validate;

public class AddControllerImpl {
	@Default
	public int showAdd(
			@Param(
				defaultVal="0",
				validate=@Validate(name="intRange",args="min=0"))int lhs,
			@Param(
				defaultVal="0",
				validate=@Validate(name="intRange",args="min=0"))int rhs){
		return lhs + rhs;
	}
}
