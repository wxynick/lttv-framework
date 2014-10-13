/**
 * 
 */
package com.letv.mobile.core.log.spi;


/**
 * @author  
 *
 */
public class DummyLoggerFactory implements ILoggerFactory {

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.log.spi.ILoggerFactory#createLogger(java.lang.String)
	 */
	@Override
	public Log createLogger(String category) {
		return new DummyLogger(category);
	}
}
