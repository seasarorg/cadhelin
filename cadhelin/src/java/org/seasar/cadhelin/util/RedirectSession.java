package org.seasar.cadhelin.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class RedirectSession {
	private static String firstKey = "CADHELIN_REDIRECT_SESSION_FIRST";
	private static String secondKey = "CADHELIN_REDIRECT_SESSION_SECOND";
	
	private Map<String,Object> values = 
		new HashMap<String,Object>();
	public static void move(HttpSession session){
		session.setAttribute(secondKey,session.getAttribute(firstKey));
		session.setAttribute(firstKey,new RedirectSession());
	}
	
	public static void setAttribute(HttpSession session,String key,Object value){
		RedirectSession first = 
			(RedirectSession) session.getAttribute(firstKey);
		first.values.put(key,value);
	}
	public static Object getAttribute(HttpSession session,String key){
		RedirectSession second = 
			(RedirectSession) session.getAttribute(secondKey);
		return second.values.get(key);
	}
	public static void copyToRequest(HttpServletRequest request){
		RedirectSession second = (RedirectSession) request.getSession().getAttribute(secondKey);
		if(second==null){
			return;
		}
		Set<Entry<String, Object>> set = second.values.entrySet();
		Iterator<Entry<String, Object>> iter = set.iterator();
		while(iter.hasNext()){
			Entry<String, Object> e = iter.next();
			request.setAttribute(e.getKey(),e.getValue());
		}
	}

	public static void setAttribute(HttpSession session, Map<String, Object> map) {
		RedirectSession first = 
			(RedirectSession) session.getAttribute(firstKey);
		first.values.putAll(map);		
	}
}
