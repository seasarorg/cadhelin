package examples.hellow;

import org.seasar.cadhelin.annotation.Default;


public class HelloControllerImpl {
	@Default
	public String showHellow(){
		return "hello";
	}
	
}
