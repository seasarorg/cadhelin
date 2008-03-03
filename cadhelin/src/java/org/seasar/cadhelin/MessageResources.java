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
package org.seasar.cadhelin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.seasar.framework.util.Disposable;
import org.seasar.framework.util.DisposableUtil;

public class MessageResources implements Disposable{
	private static MessageResources instance = new MessageResources();
	public static Map<String,String> getResourceBundle(String encoding,String key){
		DisposableUtil.add(instance);
		return instance.getResourceBundles(encoding,key);
	}
    private Map<String,Map<String,String>> bundleMap =
    	new HashMap<String,Map<String,String>>();
    private MessageResources() {
	}
    public void dispose() {
    	bundleMap.clear();
    }
	public Map<String,String> getResourceBundles(String encoding,String key) {
		if(bundleMap.containsKey(key)){
			return bundleMap.get(key);
		}
		Map<String, String> resource = new HashMap<String, String>();
		try {
			resource = importResources(key,encoding);
		} catch (IOException e) {
		}
		bundleMap.put(key,resource);
		return resource;
    }
	private Map<String,String> importResources(String key, String encoding) throws IOException{
		HashMap<String,String> map = new HashMap<String,String>();
		InputStream is = null;
		BufferedReader reader = null;
		try{
			is = getClass().getClassLoader().getResourceAsStream(key);
			if(is==null){
				return map;
			}
			reader = new BufferedReader(new InputStreamReader(is,encoding));
			String line = null;
			while((line=reader.readLine())!=null){
				if(line.startsWith("#"))continue;
				int i = line.indexOf('=');
				if(0<i){
					map.put(line.substring(0, i), line.substring(i+1));
				}
			}			
		}finally{
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(reader);
		}
		return map;
	}
}
