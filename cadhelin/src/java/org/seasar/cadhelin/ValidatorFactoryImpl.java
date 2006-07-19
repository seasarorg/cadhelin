package org.seasar.cadhelin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.seasar.cadhelin.util.ClassUtil;

public class ValidatorFactoryImpl implements ValidatorFactory {
	private Map<Class,List<ValidatorMetadata>> map = 
		new HashMap<Class,List<ValidatorMetadata>>();
	public ValidatorFactoryImpl() {
		System.out.println("test");
	}
	public void addValidators(Object[] objects){
		try{
			for (Object val : objects) {
				Validator validator = (Validator) val;
				Class keyClass = 
					ClassUtil.getParameterizedClass(validator.getClass(),Validator.class);
				List<ValidatorMetadata> vals = map.get(keyClass);
				if(vals==null){
					vals = new ArrayList<ValidatorMetadata>();
					map.put(keyClass,vals);
				}
				vals.add(new ValidatorMetadata(validator));
			}			
		}catch (Throwable e) {
			e.printStackTrace();
		}
	}
	public List<ValidatorMetadata> getValidatorsFor(Class clazz){
		List<ValidatorMetadata> vals = new ArrayList<ValidatorMetadata>();
		Class c = ClassUtil.convertPrimitiveToWrapper(clazz);
		while(!c.equals(Object.class)){
			if(map.containsKey(c)){
				vals.addAll(map.get(c));
			}
			c = c.getSuperclass();
		}
		return vals;
	}
}
