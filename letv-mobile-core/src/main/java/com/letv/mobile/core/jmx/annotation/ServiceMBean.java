package com.letv.mobile.core.jmx.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ServiceMBean {
	
	String name() default "";

	String description() default "";

	String persistPolicy() default "";
	
	int persistPeriod() default -1;

}
