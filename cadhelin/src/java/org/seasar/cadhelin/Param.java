package org.seasar.cadhelin;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	String name() default "";
	Validate[] validate() default {};
	boolean required() default true;
}
