package examples.url;

import org.seasar.cadhelin.annotation.Default;
import org.seasar.cadhelin.annotation.Url;

public class UrlControllerImpl {
	@Url("/urltest/action1")
	public String showAction1(){
		return "this is action1";
	}
	@Default
	@Url("action1")
	public String showAction2(){
		return "this is action1";
	}
	@Url("/action1")
	public String showAction3(){
		return "this is action1";
	}
}
