package com.letv.mobile.core.log.api.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Signify that an interface is a typed logger interface.  A message logger interface may optionally extend other message logger
 * interfaces and message bundle interfaces (see {@link MessageBundle}, as well as the {@link org.jboss.logging.BasicLogger} interface.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Retention(RUNTIME)
@Target(TYPE)
@Documented
public @interface MessageLogger {

    /**
     * Get the project code for messages that have an associated code.  If no project code is associated
     * with this logger, specify {@code ""} (the empty string).
     *
     * @return the project code
     */
    String projectCode();
}
