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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.seasar.framework.exception.SRuntimeException;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

public class RedirectSession implements Serializable{
	private static String firstKey = "CADHELIN_REDIRECT_SESSION_FIRST";
	private static String secondKey = "CADHELIN_REDIRECT_SESSION_SECOND";
	
	private Map<String,Object> values = 
		new HashMap<String,Object>();
	public static void move(HttpSession session){
		Object attribute = session.getAttribute(firstKey);
		if(disposed){
			try{
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(bos);
				oos.writeObject(attribute);
				ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bos.toByteArray()));
				attribute = ois.readObject();
			}catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		session.setAttribute(secondKey,attribute);
		session.setAttribute(firstKey,new RedirectSession());
		DisposableUtil.add(new Disposable(){
			public void dispose() {
				disposed = true;
			}
		});
	}
	private static boolean disposed = false;
	public void dispose() {
		disposed = true;
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
