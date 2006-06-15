package org.seasar.cadhelin;

public interface AuthorizationManager {
	public boolean authorized(Object sessionManager,String role);
}
