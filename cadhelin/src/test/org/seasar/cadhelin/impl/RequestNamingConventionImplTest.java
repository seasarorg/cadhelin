package org.seasar.cadhelin.impl;

import org.seasar.cadhelin.RequestInfo;
import org.seasar.cadhelin.util.StringUtil;

import junit.framework.TestCase;

public class RequestNamingConventionImplTest extends TestCase {

	public void testGetRequestInfo() {
		String[] requestURL = new String[]{
				//0
				"/do/",
				//1
				"/do/action.html",
				//2
				"/do/cont1/",
				"/do/cont1/action.html",
				"/do/action/param1.html",
				//3				
				"/do/cont1/action-param1.html",
				"/do/subapp/cont1/action.html",
				//4
				"/do/subapp/cont1/action/param1.html",
		};
		String[] expected = new String[]{
			"controllerName=,actionName=,extention=",
			"controllerName=,actionName=action,extention=html",
			"controllerName=cont1,actionName=,extention=",
			"controllerName=cont1,actionName=action,extention=html",
			"controllerName=cont1_action,actionName=param1,extention=html]," +
				"[controllerName=cont1,actionName=action,extention=html,param=[param1]",
			"controllerName=subapp_cont1,actionName=action,extention=html]," +
				"[controllerName=subapp,actionName=cont1,extention=html,param=[action]",
			"controllerName=subapp_cont1,actionName=action,extention=html,param=[param1]]," +
			"	[controllerName=subapp,actionName=cont1,extention=html,param=[action,param1]",
		};
		RequestNamingConventionImpl convention = new RequestNamingConventionImpl();
		for (int i=0;i<requestURL.length;i++) {
			System.out.println(requestURL[i]);			
			String join = StringUtil.join(convention.getRequestInfo(requestURL[i]), "],[");
			System.out.println(join);
			assertEquals(expected[i], join);
		}
	}

}
