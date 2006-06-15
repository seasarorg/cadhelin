package org.seasar.cadhelin;


public interface ActionFilter {
	public void doFilter(ControllerContext context,ActionMetadata actionMetadata);
}
