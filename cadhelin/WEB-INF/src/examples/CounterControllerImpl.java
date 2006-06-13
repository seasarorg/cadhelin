package examples;

import org.seasar.cadhelin.Action;

public class CounterControllerImpl {
	@Action("sessionManager")
	public int add(SessionManager sessionManager){
		int count = sessionManager.getCount();
		sessionManager.setCount(count+1);
		return count;
	}
}
