package org.seasar.cadhelin;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.cadhelin.validator.IntRangeValidator;

public class ValidatorFactoryImpl {
	private Map<Class,List<Validator>> map = 
		new HashMap<Class,List<Validator>>();
	public void setValidators(Validator[] validators){
		for (Validator validator : validators) {
		}
	}
	public static void main(String args[]) throws Exception {
		
		Type[] type = IntRangeValidator.class.getGenericInterfaces();
		for (Type type2 : type) {
			System.out.println(type2); // java.util.ArrayList<java.lang.String>			
		}
	}
}
class IntValidator implements Validator<Integer>{
	public Class getTargetClass() {
		// TODO Auto-generated method stub
		return null;
	}
	public void setMessageArguments(Map<String, Object> messageArguments) {
		// TODO Auto-generated method stub
		
	}
	public boolean validate(String name, Integer value, Map<String, Message> errors) {
		// TODO Auto-generated method stub
		return false;
	}
}
class IntegerValidator extends IntValidator{
	
}
class StringList extends ArrayList<String> {
}
