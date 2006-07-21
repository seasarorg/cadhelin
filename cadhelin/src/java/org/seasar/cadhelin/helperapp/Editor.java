package org.seasar.cadhelin.helperapp;

import org.seasar.cadhelin.PropertyMetadata;

public interface Editor {
	public Object getEditor(PropertyMetadata pd,String name);
	public void setValue(PropertyMetadata pd,String[] strs);
}
