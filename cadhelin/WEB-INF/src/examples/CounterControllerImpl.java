package examples;


public class CounterControllerImpl {
	public int add(SessionManager sessionManager){
		int count = sessionManager.getCount();
		sessionManager.setCount(count+1);
		return count;
	}
}
