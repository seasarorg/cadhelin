package org.seasar.cadhelin.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class ClassUtil {
	private static Map<Class,Class> classMap
		= new HashMap<Class,Class>();
	static{
		classMap.put(boolean.class,Boolean.class);
		classMap.put(char.class,Character.class);
		classMap.put(short.class,Short.class);
		classMap.put(int.class,Integer.class);
		classMap.put(long.class,Long.class);
		classMap.put(float.class,Float.class);
		classMap.put(double.class,Double.class);
	}
	private static Class[] classes = new Class[]{
		Boolean.class,
		Character.class,
		Integer.class,
		Long.class,
		Short.class,
		Float.class,
		Double.class,
		String.class
	};
	public static Class convertPrimitiveToWrapper(Class clazz){
		if(classMap.containsKey(clazz)){
			return classMap.get(clazz);
		}
		return clazz;
	}
	public static boolean isPrimitiveOrWrapper(Class clazz){
		if(clazz.isPrimitive()){
			return true;
		}
		if(classMap.containsValue(clazz)){
			return true;
		}
		return false;
	}
	public static Class getParameterizedClass(Class clazz,Class interfaceClass){
		for (Type type : clazz.getGenericInterfaces()) {
			if (type instanceof ParameterizedType) {
				ParameterizedType ptype = (ParameterizedType) type;
				if(ptype.getRawType().equals(interfaceClass)){
					for (Type type2 : ptype.getActualTypeArguments()) {
						if (type2 instanceof Class) {
							return (Class) type2;
						}
					}					
				}
			}
		}
		return null;
	}
}
