package org.seasar.cadhelin;

public @interface Validate {
	String name();
	String[] args() default {};
}
