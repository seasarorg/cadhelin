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
package org.seasar.cadhelin;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.seasar.cadhelin.annotation.OnError;
import org.seasar.cadhelin.annotation.Validate;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
	String name() default "";
	String defaultVal() default "";
	Validate[] validate() default {};
	OnError onError() default OnError.ERROR;
	boolean required() default false;
}
