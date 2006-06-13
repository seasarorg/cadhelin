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
