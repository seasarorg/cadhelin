package org.seasar.cadhelin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface FilterContext {
	public void doFilter(
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;

	public ActionMetadata getActionMetadata();

	public void sendRedirect(String redirectUrl) throws IOException;
}
