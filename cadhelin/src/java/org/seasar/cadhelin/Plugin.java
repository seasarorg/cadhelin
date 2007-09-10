package org.seasar.cadhelin;

import javax.servlet.ServletConfig;

public interface Plugin {
	public void start(ServletConfig config);
	public void stop();
}
