package examples;

import org.seasar.cadhelin.AuthorizationManager;

public class AuthorizationManagerImpl implements AuthorizationManager {

	public boolean authorized(Object sessionManager, String role) {
		if (sessionManager instanceof SessionManager) {
			SessionManager sessionManager2 = (SessionManager) sessionManager;
			if(role.equals("admin")){
				User user = sessionManager2.getUser();
				if(user!=null){
					return false;
				}
				return user.getRole().equals("admin");
			}
		}
		return true                                                                                                                                                                                                                                                                        ;
	}

}
