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
public class InsertUtils {
    public InsertAsIs insertAsIs(Object object) {
        return object == null ? null : new InsertAsIs(object);
    }
}
