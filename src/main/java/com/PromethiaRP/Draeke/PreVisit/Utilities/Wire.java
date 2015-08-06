package com.PromethiaRP.Draeke.PreVisit.Utilities;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface Wire {

	boolean injectInherited() default false;
	
	boolean failOnNull() default true;
	
	String name() default "";
}
