package com.letv.mobile.core.log.api.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Mark a parameter as being the "exception cause" parameter rather than a positional format parameter.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(RUNTIME)
@Target(PARAMETER)
@Documented
public @interface Cause {
}
