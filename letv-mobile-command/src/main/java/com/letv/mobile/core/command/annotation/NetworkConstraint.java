/**
 * 
 */
package com.letv.mobile.core.command.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author  
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE,ElementType.METHOD})
@Documented
@CommandConstraint(validatedBy={})
public @interface NetworkConstraint {
	NetworkConnectionType[] allowConnectionTypes() default {};	// default means any connection is allowed
}
