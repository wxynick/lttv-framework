/**
 * 
 */
package com.letv.mobile.android.log;

import com.letv.mobile.core.log.spi.ILoggerFactory;
import com.letv.mobile.core.log.spi.Log;


/**
 * @author  
 *
 */
public class AndroidLoggerFactory implements ILoggerFactory {

	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.ILoggerFactory#createLogger(java.lang.String)
	 */
	public Log createLogger(String category) {
		return AndroidLogger.getLogger(category);
	}

}
