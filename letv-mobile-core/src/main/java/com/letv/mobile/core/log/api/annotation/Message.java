package com.letv.mobile.core.log.api.annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Assigns a message string to a resource method.  The method arguments are used to supply the positional parameter
 * values for the method.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
@Target(METHOD)
@Retention(RUNTIME)
@Documented
public @interface Message {

    /**
     * Indicates that this message has no ID.
     */
    int NONE = 0;
    /**
     * Indicates that this message should inherit the ID from another message with the same name.
     */
    int INHERIT = -1;

    /**
     * The message ID number.  Only one message with a given name may specify an ID other than {@link #INHERIT}.
     *
     * @return the message ID number
     */
    int id() default INHERIT;

    /**
     * The default format string of this message.
     *
     * @return the format string
     */
    String value();

    /**
     * The format type of this method (defaults to {@link Format#PRINTF}).
     *
     * @return the format type
     */
    Format format() default Format.PRINTF;

    /**
     * The possible format types.
     */
    enum Format {

        /**
         * A {@link java.util.Formatter}-type format string.
         */
        PRINTF,
        /**
         * A {@link java.text.MessageFormat}-type format string.
         */
        MESSAGE_FORMAT,
    }

}
