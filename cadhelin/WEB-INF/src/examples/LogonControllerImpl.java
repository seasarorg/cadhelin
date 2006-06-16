package examples;

import org.seasar.cadhelin.Action;
import org.seasar.cadhelin.ControllerContext;
import org.seasar.cadhelin.Message;
import org.seasar.cadhelin.ResultName;

public class LogonControllerImpl {
	UserManager userManager;
	public LogonControllerImpl(UserManager userManager) {
		this.userManager = userManager;
	}
	@Action
	public void logonForm(){
	}
	@Action({"sessinoManager","userName","password"})
	@ResultName("user")
	public User logon(SessionManager sessionManager,String userName,String password){
		User user = userManager.authenticate(userName,password);
		if(user==null){
			ControllerContext context = 
				ControllerContext.getContext();
			context.addMessage("logon",new Message("error.logon"));
			logonForm();
			return null;
		}
		sessionManager.setUser(user);
		return user;
	}
}
