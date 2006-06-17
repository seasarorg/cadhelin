package org.seasar.cadhelin;

import java.util.Map;

import org.seasar.cadhelin.annotation.Validate;

public interface Validator {
	String getValidaterKey();
	public Validator createValidater(Validate validate);
	boolean validate(String name,Object value,Map<String,Message> errors);
}
