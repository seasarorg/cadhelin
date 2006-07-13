package org.seasar.cadhelin;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValidatorFactoryImpl {
	private static Map <Class,Class> primitiveConverter = new HashMap<Class, Class>();
	static{
		primitiveConverter.put(int.class, Integer.class);
		primitiveConverter.put(long.class, Long.class);
		primitiveConverter.put(char.class, Character.class);
		primitiveConverter.put(short.class, Short.class);
		primitiveConverter.put(double.class, Double.class);
		primitiveConverter.put(float.class, Float.class);
		primitiveConverter.put(boolean.class, Boolean.class);
	}
	private Map<Class,List<Validator>> map = 
		new HashMap<Class,List<Validator>>();
	public void setValidators(Validator[] validators){
		for (Validator validator : validators) {
			Type[] types = validator.getClass().getGenericInterfaces();
			for (Type type : types) {
				List<Validator> list = map.get(type.getClass());
				if(list == null){
					list = new ArrayList<Validator>();
					map.put(type.getClass(), list);
				}
				list.add(validator);
			}
		}
	}
	public List<Validator> getValidators(Class clazz){
		if(clazz.isPrimitive()){
			clazz = primitiveConverter.get(clazz); 
		}
		while(!map.containsKey(clazz)){
			clazz = clazz.getSuperclass();
		}
		return map.get(clazz);
	}
}