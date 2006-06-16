package org.seasar.cadhelin;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface ActionFilter {
	public void doFilter(
			FilterContext context,
			HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException;
}
