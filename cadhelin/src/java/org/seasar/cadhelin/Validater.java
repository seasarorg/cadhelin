package org.seasar.cadhelin;

import java.util.Map;

public interface Validater {
	String getValidaterKey();
	public Validater createValidater(Validate validate);
	boolean validate(String name,Object value,Map<String,Message> errors);
}
