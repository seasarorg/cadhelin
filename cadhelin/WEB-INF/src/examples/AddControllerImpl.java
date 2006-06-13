package examples;

import org.seasar.cadhelin.Action;
import org.seasar.cadhelin.Validater;

public class AddControllerImpl {
	@Action({"lhs","rhs"})
	public int add(
			@Validater(arg="min=0")int lhs,
			@Validater(arg="max=1000")int rhs){
		return lhs + rhs;
	}
}
