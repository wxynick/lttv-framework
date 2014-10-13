package com.letv.mobile.core.log.api.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.letv.mobile.core.log.api.SeverityLevel;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A typed logger method.  Indicates that this method will log the associated {@link Message} to the logger system, as
 * opposed to being a simple message lookup.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(RUNTIME)
@Target(METHOD)
@Documented
public @interface LogMessage {

    /**
     * The log level at which this message should be logged.  Defaults to {@code INFO}.
     *
     * @return the log level
     */
    SeverityLevel level() default SeverityLevel.INFO;
}
