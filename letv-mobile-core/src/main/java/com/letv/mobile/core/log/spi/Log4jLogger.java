/**
 * 
 */
package com.letv.mobile.core.log.spi;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.letv.mobile.core.log.api.SeverityLevel;

/**
 * @author  
 *
 */
public class Log4jLogger implements Log {
	
	private static ConcurrentHashMap<String, WeakReference<Log4jLogger>> loggers = new ConcurrentHashMap<String, WeakReference<Log4jLogger>>();

	public static Log4jLogger getLogger(String category){
		WeakReference<Log4jLogger> ref = loggers.get(category);
		Log4jLogger logger = null;
		if(ref == null){
			logger = new Log4jLogger(Logger.getLogger(category),category);
			ref = loggers.putIfAbsent(category, new WeakReference<Log4jLogger>(logger));
			if(ref == null){
				ref = loggers.get(category);
			}
		}
		synchronized(ref){
			logger = ref.get();
			if(logger == null){
				logger = new Log4jLogger(Logger.getLogger(category),category);
				loggers.put(category, new WeakReference<Log4jLogger>(logger));
			}
			return logger;
		}
	}
	
	private static Level translate(final SeverityLevel level) {
		if (level != null) switch (level) {
		case FATAL: return Level.FATAL;
		case ERROR: return Level.ERROR;
		case WARN:  return Level.WARN;
		case INFO:  return Level.INFO;
		case DEBUG: return Level.DEBUG;
		case TRACE: return Level.TRACE;
		}
		return Level.ALL;
	}

	
	private final Logger log;
	private final String loggerName;
	
	private Log4jLogger(Logger logger,String name){
		this.log = logger;
		this.loggerName = name;
	}
	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.Log#isEnabled(com.wxxr.core.log.api.SeverityLevel)
	 */
	public boolean isEnabled(SeverityLevel level) {
		return this.log.isEnabledFor(translate(level));
	}

	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.Log#log(com.wxxr.core.log.api.SeverityLevel, java.lang.Object)
	 */
	public void log(SeverityLevel level, Object message) {
		log(level, String.valueOf(message),null);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.Log#log(com.wxxr.core.log.api.SeverityLevel, java.lang.Object, java.lang.Throwable)
	 */
	public void log(SeverityLevel level, Object message, Throwable t) {
		log(level, String.valueOf(message),null,t);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.Log#log(com.wxxr.core.log.api.SeverityLevel, java.lang.String, java.lang.Object[], java.lang.Throwable)
	 */
	public void log(SeverityLevel level, String message, Object[] args,
			Throwable t) {
		
        if (isEnabled(level)) try {
            String msg = message;
            try {
				msg = (args == null)||(args.length == 0) ? message : String.format(message, args);
			} catch (Throwable e) {
			}
            if((args != null)&&(args.length > 0)&&(message.equals(msg))){
            	int cnt = 0;
            	StringBuffer buf = new StringBuffer(msg);
            	for (Object object : args) {
					if(cnt > 0){
						buf.append(',');
					}
					buf.append(object);
				}
            	msg = buf.toString();
            }
//            final MyLogRecord rec = new MyLogRecord(translate(level), msg);
//            if (t != null) rec.setThrown(t);
//            rec.setLoggerName(this.loggerName);
//            rec.setParameters(args);
//            rec.setResourceBundleName(logger.getResourceBundleName());
//            rec.setResourceBundle(logger.getResourceBundle());
            log.log(this.loggerName,translate(level),msg,t);
        } catch (Throwable ignored) {}
	}

}
