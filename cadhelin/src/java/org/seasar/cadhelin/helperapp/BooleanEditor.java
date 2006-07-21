package org.seasar.cadhelin.helperapp;

import org.seasar.cadhelin.PropertyMetadata;
import org.seasar.cadhelin.velocity.InsertAsIs;

public class BooleanEditor implements Editor {

	public Object getEditor(PropertyMetadata pd, String options) {
		Boolean bool = (Boolean) pd.getValue();
		boolean checked = (bool!=null)?bool.booleanValue():false;
		String str = "<input type=\"checkbox\" value=\"true\" " + options +" "+((checked)?"checked":"")+"/>";
		return new InsertAsIs(str);
	}

	public void setValue(PropertyMetadata pd,String[] strs) {
		if(strs==null || strs.length == 0){
			pd.setValue(Boolean.FALSE);
		}else{
			pd.setValue(Boolean.TRUE);			
		}
	}

}
