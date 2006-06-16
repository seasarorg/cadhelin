package examples;

import org.seasar.cadhelin.AuthorizationManager;

public class AuthorizationManagerImpl implements AuthorizationManager {

	public boolean authorized(Object sessionManager, String role) {
		if(role==null || role.length()==0){
			return true;
		}
		if (sessionManager instanceof SessionManager) {
			SessionManager sessionManager2 = (SessionManager) sessionManager;
			if("admin".equals(role)){
				User user = sessionManager2.getUser();
				if(user==null){
					return false;
				}
				return user.getRole().equals("admin");
			}
		}
		return false;
	}

}
