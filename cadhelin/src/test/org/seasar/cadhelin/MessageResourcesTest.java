package org.seasar.cadhelin;

import junit.framework.TestCase;

public class MessageResourcesTest extends TestCase {

	/*
	 * Test method for 'org.seasar.cadhelin.MessageResources.getMessage(Locale, String, Object[])'
	 */
	public void testGetMessage() {
		System.out.println(MessageResources.getResourceBundle("resources.test"));
	}

}
