package org.seasar.cadhelin.helperapp;

import org.seasar.cadhelin.PropertyMetadata;
import org.seasar.cadhelin.velocity.InsertAsIs;

public class IntegerEditor implements Editor {

	public Object getEditor(PropertyMetadata pd, String options) {
		
		return new InsertAsIs("<input type=\"text\" value=\""+pd.getValue()+"\" " + options +"/>");
	}

	public void setValue(PropertyMetadata pd,String[] strs) {
		if(strs==null || strs.length == 0 || strs[0].length() == 0){
			pd.setValue(null);
		}else{
			pd.setValue(Integer.parseInt(strs[0]));			
		}
	}

}
