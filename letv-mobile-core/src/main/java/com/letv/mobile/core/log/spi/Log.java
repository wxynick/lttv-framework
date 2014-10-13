package com.letv.mobile.core.log.spi;

import com.letv.mobile.core.log.api.SeverityLevel;

public interface Log {

	boolean isEnabled(SeverityLevel level);
	
	void log(SeverityLevel level,Object message);

	void log(SeverityLevel level,Object message, Throwable t);
	
	void log(SeverityLevel level,String message,Object[]args, Throwable t);

}