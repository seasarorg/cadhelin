package org.seasar.cadhelin.annotation;

public @interface Action {
	String[] mimeTyle() default "text/html";
	String suffix() default "\0";
}
