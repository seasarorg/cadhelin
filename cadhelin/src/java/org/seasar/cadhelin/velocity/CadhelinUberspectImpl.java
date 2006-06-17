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
package org.seasar.cadhelin.velocity;

import org.apache.velocity.util.introspection.Info;
import org.apache.velocity.util.introspection.UberspectImpl;
import org.apache.velocity.util.introspection.VelMethod;

public class CadhelinUberspectImpl extends UberspectImpl {
	public CadhelinUberspectImpl() {
		System.out.println("cadhelin");
	}
	@Override
	public VelMethod getMethod(Object obj, String methodName, Object[] args, Info info) throws Exception {
		if (obj instanceof LinkMetadata) {
			LinkMetadata linkMetadata = (LinkMetadata) obj;
			return new CadMethodImpl(linkMetadata,methodName);
		}else{
			return super.getMethod(obj, methodName, args, info);			
		}
	}
}
class CadMethodImpl implements VelMethod{
	LinkMetadata linkMetadata;
	String methodName;
    public CadMethodImpl(LinkMetadata linkMetadata,String methodName){
    	this.linkMetadata = linkMetadata;
    	this.methodName = methodName;
    }

    public Object invoke(Object o, Object[] params)
        throws Exception
    {
        return linkMetadata.getLink(methodName,params);
    }

    public boolean isCacheable()
    {
        return true;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public Class getReturnType()
    {
        return String.class;
    }
}
