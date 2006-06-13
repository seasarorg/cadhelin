package org.seasar.cadhelin;

public @interface Validater {
	String name() default "";
	String[] arg() default {};
	boolean required() default true;
}
