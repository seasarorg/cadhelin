/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
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
		if(first!=null){
			first.values.put(key,value);			
		}
	}
	public static Object getAttribute(HttpSession session,String key){
		RedirectSession second = 
			(RedirectSession) session.getAttribute(secondKey);
		return (second!=null)?second.values.get(key):null;			
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
