/**
 * 
 */
package com.letv.mobile.core.log.spi;


/**
 * @author  
 *
 */
public class Log4jLoggerFactory extends DummyLoggerFactory {

	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.ILoggerFactory#createLogger(java.lang.String)
	 */
	public Log createLogger(String category) {
		return Log4jLogger.getLogger(category);
	}

}
