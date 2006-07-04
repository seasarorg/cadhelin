package org.seasar.cadhelin;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

public class CookieTool {
	String encoding = "UTF-8";
	private HttpServletRequest request;
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	public String getCookie(String key){
		Cookie[] cookies = request.getCookies();
		for (int i = 0; i < cookies.length; i++) {
			if(cookies[i].getName().equals(key)){
				try {
					return URLDecoder.decode(cookies[i].getValue(),encoding);
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return null;
	}
}
