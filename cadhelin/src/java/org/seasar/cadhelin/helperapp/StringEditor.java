package org.seasar.cadhelin.helperapp;

import org.seasar.cadhelin.PropertyMetadata;
import org.seasar.cadhelin.velocity.InsertAsIs;

public class StringEditor implements Editor {

	public Object getEditor(PropertyMetadata pd, String options) {
		
		return new InsertAsIs("<input type=\"text\" value=\""+pd.getValue()+"\" " + options +"/>");
	}

	public void setValue(PropertyMetadata pd,String[] strs) {
		if(strs==null || strs.length == 0){
			pd.setValue(null);
		}else{
			pd.setValue(strs[0]);			
		}
	}

}
