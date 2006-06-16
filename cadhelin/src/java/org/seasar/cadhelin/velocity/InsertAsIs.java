/*
 * Created on 2004/10/28
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.seasar.cadhelin.velocity;

/**
 * @author Administrator
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class InsertAsIs {
    private Object object;
    
    public InsertAsIs(Object object) {
    	this.object = object;
    }
    /**
	 * @return Returns the object.
	 */
	public Object getObject() {
		return object;
	}
    public String toString() {
   		return object == null ? null : object.toString();
    }

}
