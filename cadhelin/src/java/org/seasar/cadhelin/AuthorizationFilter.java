package org.seasar.cadhelin;

public class AuthorizationFilter implements ActionFilter {
	private String redirectUrl;
	private AuthorizationManager authorizationManager;
	public void doFilter(ControllerContext context,
			ActionMetadata actionMetadata) {
		Object sessionManager = context.getRequest().getSession().getAttribute("sessionManager");
		boolean authorized = authorizationManager.authorized(sessionManager,actionMetadata.getRole());
		if(authorized){
			context.doFilter();
		}else{
			context.setRedirect(redirectUrl);
		}
	}

}
