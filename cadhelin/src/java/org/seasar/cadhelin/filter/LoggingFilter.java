package org.seasar.cadhelin.filter;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.seasar.cadhelin.ActionFilter;
import org.seasar.cadhelin.FilterContext;

public class LoggingFilter implements ActionFilter {

	public void doFilter(FilterContext context, HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		context.doFilter(request,response);
		long end = System.currentTimeMillis();
		System.out.println("takes " + (end - start));
	}

}
