package org.seasar.cadhelin.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.ActionFilter;
import org.seasar.cadhelin.ActionMetadata;
import org.seasar.cadhelin.AuthorizationManager;
import org.seasar.cadhelin.FilterContext;

public class AuthorizationFilter implements ActionFilter {
	private String redirectUrl;
	private AuthorizationManager authorizationManager;
	
	public AuthorizationFilter(AuthorizationManager authorizationManager) {
		this.authorizationManager = authorizationManager;
	}
	public void setRedirectUrl(String redirectUrl) {
		this.redirectUrl = redirectUrl;
	}
	public void doFilter(
			FilterContext context, 
			HttpServletRequest request, 
			HttpServletResponse response) throws ServletException, IOException {
		Object sessionManager = request.getSession().getAttribute("sessionManager");
		ActionMetadata actionMetadata = context.getActionMetadata();
		boolean authorized = authorizationManager.authorized(sessionManager,actionMetadata.getRole());
		if(authorized){
			context.doFilter(request,response);
		}else{
			context.sendRedirect(redirectUrl);
		}
	}

}
