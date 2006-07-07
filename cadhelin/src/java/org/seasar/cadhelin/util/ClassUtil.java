package org.seasar.cadhelin.util;

public class ClassUtil {
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
	public static boolean isPrimitiveOrWrapper(Class clazz){
		if(clazz.isPrimitive()){
			return true;
		}
		for (Class c : classes) {
			if(c.equals(clazz)){
				return true;
			}
		}
		return false;
	}
}
