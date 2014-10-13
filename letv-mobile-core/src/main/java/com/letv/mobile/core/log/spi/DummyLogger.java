/**
 * 
 */
package com.letv.mobile.core.log.spi;

import com.letv.mobile.core.log.api.SeverityLevel;

/**
 * @author  
 *
 */
public class DummyLogger implements Log {
	
	private String loggerName;
	
	public DummyLogger(String name) {
		this.loggerName = name;
	}

	public String getLoggerName() {
		return loggerName;
	}

	@Override
	public boolean isEnabled(SeverityLevel level) {
		return false;
	}

	@Override
	public void log(SeverityLevel level, Object message) {
		
	}

	@Override
	public void log(SeverityLevel level, Object message, Throwable t) {
		
	}

	@Override
	public void log(SeverityLevel level, String message, Object[] args, Throwable t) {
		
	}

}
