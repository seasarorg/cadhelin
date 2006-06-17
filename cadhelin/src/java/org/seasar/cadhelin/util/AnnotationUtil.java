/*
 * Copyright 2004-2006 the Seasar Foundation and the Others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, 
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */
package org.seasar.cadhelin.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.seasar.framework.beans.PropertyDesc;
import org.seasar.framework.util.ClassUtil;

public class AnnotationUtil {
	public static <T extends Annotation> T getPropertyAnnotation(PropertyDesc pd,Class<T > clazz){
		if(pd.hasWriteMethod()){
			Method method = pd.getWriteMethod();
			T annotation = method.getAnnotation(clazz);
			if(annotation!=null){
				return annotation;
			}
		}
		if(pd.hasReadMethod()){
			Method method = pd.getReadMethod();
			T annotation = method.getAnnotation(clazz);
			if(annotation!=null){
				return annotation;
			}
		}
		return null;
	}
	@SuppressWarnings("unchecked")
	public static Annotation getAnnotation(Class clazz,Class annotationClass,Method method){
		Class c =clazz;
		Method m = method;
		try {
			while(m!=null){
				Annotation a = m.getAnnotation(annotationClass);
				if(a!=null){
					return a;
				}
				c = c.getSuperclass();
				m = ClassUtil.getMethod(c,method.getName(),method.getParameterTypes());
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}
	public static Annotation[][] getParameterActionMethod(Class clazz,Method method){
		Class c =clazz;
		Method m = method;
		Annotation[][] a = null;
		try {
			while(m!=null){
				a = m.getParameterAnnotations();
				if(hasParameterAnnotation(a)){
					return a;
				}
				c = c.getSuperclass();
				m = ClassUtil.getMethod(c,method.getName(),method.getParameterTypes());
			}
			return a;
		} catch (Exception e) {
			return a;
		}
	}
	private static boolean hasParameterAnnotation(Annotation[][] a){
		if(a==null){
			return false;
		}
		for (Annotation[] annotations : a) {
			if(annotations.length>0){
				return true;
			}
		}
		return false;
	}
}
