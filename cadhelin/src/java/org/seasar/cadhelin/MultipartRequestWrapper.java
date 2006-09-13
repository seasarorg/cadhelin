/*
 * $Id: MultipartRequestWrapper.java 164684 2005-04-25 23:19:11Z niallp $ 
 *
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.seasar.cadhelin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItem;

/**
 * This class functions as a wrapper around HttpServletRequest to
 * provide working getParameter methods for multipart requests.  Once
 * Struts requires Servlet 2.3, this class will definately be changed to
 * extend javax.servlet.http.HttpServletRequestWrapper instead of
 * implementing HttpServletRequest.  Servlet 2.3 methods are implemented
 * to return <code>null</code> or do nothing if called on.  Use
 * {@link #getRequest() getRequest} to retrieve the underlying HttpServletRequest
 * object and call on the 2.3 method there, the empty methods are here only
 * so that this will compile with the Servlet 2.3 jar.  This class exists temporarily
 * in the process() method of ActionServlet, just before the ActionForward is processed
 * and just after the Action is performed, the request is set back to the original
 * HttpServletRequest object.
 */
public class MultipartRequestWrapper extends HttpServletRequestWrapper {
    
    /**
     * The parameters for this multipart request
     */
    protected Map<String,String[]> parameters = new HashMap<String,String[]>();
    /**
     * The parameters for this multipart request
     */
    protected List<FileItem> fileItems = new ArrayList<FileItem>();
    
    /**
     * The underlying HttpServletRequest
     */
    protected HttpServletRequest request;
    
    public MultipartRequestWrapper(HttpServletRequest request) {
    	super(request);
        this.request = request;
    }
    /**
     * Sets a parameter for this request.  The parameter is actually
     * separate from the request parameters, but calling on the
     * getParameter() methods of this class will work as if they weren't.
     */
    public void setParameter(String name, String value) {
        String[] mValue = parameters.get(name);
        if (mValue == null) {
            mValue = new String[0];
        }
        String[] newValue = new String[mValue.length + 1];
        System.arraycopy(mValue, 0, newValue, 0, mValue.length);
        newValue[mValue.length] = value;
        
        parameters.put(name, newValue);
    }
    
    /**
     * Attempts to get a parameter for this request.  It first looks in the
     * underlying HttpServletRequest object for the parameter, and if that
     * doesn't exist it looks for the parameters retrieved from the multipart
     * request
     */
    public String getParameter(String name) {
        String value = request.getParameter(name);
        if (value == null) {
            String[] mValue = parameters.get(name);
            if ((mValue != null) && (mValue.length > 0)) {
                value = mValue[0];
            }
        }
        return value;
    }
    
    /**
     * Returns the names of the parameters for this request.
     * The enumeration consists of the normal request parameter
     * names plus the parameters read from the multipart request
     */
    public Enumeration getParameterNames() {
        Enumeration baseParams = request.getParameterNames();
        Vector<String> list = new Vector<String>();
        while (baseParams.hasMoreElements()) {
            list.add((String) baseParams.nextElement());
        }
        Collection multipartParams = parameters.keySet();
        Iterator iterator = multipartParams.iterator();
        while (iterator.hasNext()) {
            list.add((String) iterator.next());
        }
        return Collections.enumeration(list);
    }
    
    public String[] getParameterValues(String name) {
        String[] value = request.getParameterValues(name);
        if (value == null) {
            value = parameters.get(name);
        }
        return value;
    }
    public void setFileItems(List items){
    	Iterator iter = items.iterator();
    	while(iter.hasNext()){
    		FileItem item = (FileItem) iter.next();
    		fileItems.add(item);
    		if(item.isFormField()){
    			setParameter(item.getFieldName(),item.getString());
    		}
    	}
    }
    public Collection<FileItem> getFileItems(){
    	return fileItems;
    }
    public FileItem getFileItem(String name){
    	for (FileItem fileItem : fileItems) {
    		if(fileItem.getFieldName().equals(name)){
    			return fileItem;
    		}
		}
    	return null;
    }
    public void closeFileItems(){
    	for (FileItem fileItem : fileItems) {
    		fileItem.delete();
		}
    }
}
