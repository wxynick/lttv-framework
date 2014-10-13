/**
 * 
 */
package com.letv.mobile.android.log;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;

import com.letv.mobile.core.log.api.SeverityLevel;
import com.letv.mobile.core.log.spi.Log;

/**
 * @author 
 *
 */
public class AndroidLogger implements Log {
	
	private static ConcurrentHashMap<String, WeakReference<AndroidLogger>> loggers = new ConcurrentHashMap<String, WeakReference<AndroidLogger>>();

	public static AndroidLogger getLogger(String category){
		WeakReference<AndroidLogger> ref = loggers.get(category);
		AndroidLogger logger = null;
		if(ref == null){
			logger = new AndroidLogger(category);
			ref = loggers.putIfAbsent(category, new WeakReference<AndroidLogger>(logger));
			if(ref == null){
				ref = loggers.get(category);
			}
		}
		synchronized(ref){
			logger = ref.get();
			if(logger == null){
				logger = new AndroidLogger(category);
				loggers.put(category, new WeakReference<AndroidLogger>(logger));
			}
			return logger;
		}
	}
	
	private static int translate(final SeverityLevel level) {
		if (level != null) switch (level) {
		case FATAL: return android.util.Log.ASSERT;
		case ERROR: return android.util.Log.ERROR;
		case WARN:  return android.util.Log.WARN;
		case INFO:  return android.util.Log.INFO;
		case DEBUG: return android.util.Log.DEBUG;
		case TRACE: return android.util.Log.VERBOSE;
		}
		return android.util.Log.VERBOSE;
	}

	
	private final String loggerName;
	private String tag;
	
	private String getTag() {
		if(tag == null){
			int idx = loggerName.lastIndexOf('.');
			if(idx > 0){
				tag = loggerName.substring(idx+1);
			}else{
				tag = loggerName;
			}
			int len = tag.length();
			if(len > 23) {		// limitted by logCat
				tag = tag.substring(len-23);
			}
		}
		return tag;
	}
	
	private AndroidLogger(String name){
		this.loggerName = name;
	}
	/* (non-Javadoc)
	 * @see com.wxxr.core.log.spi.Log#isEnabled(com.wxxr.core.log.api.SeverityLevel)
	 */
	public boolean isEnabled(SeverityLevel level) {
		return android.util.Log.isLoggable(getTag(),translate(level));
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
            switch(level){
            case DEBUG:
            	if(t != null){
            		android.util.Log.d(getTag(), msg,t);
            	}else{
            		android.util.Log.d(getTag(), msg);
            	}
            	break;
            case ERROR:
            	if(t != null){
            		android.util.Log.e(getTag(), msg,t);
            	}else{
            		android.util.Log.e(getTag(), msg);
            	}
            	break;
            case FATAL:
            	if(t != null){
            		android.util.Log.wtf(getTag(), msg,t);
            	}else{
            		android.util.Log.wtf(getTag(), msg);
            	}
            	break;
            case INFO:
            	if(t != null){
            		android.util.Log.i(getTag(), msg,t);
            	}else{
            		android.util.Log.i(getTag(), msg);
            	}
            	break;
            case TRACE:
            	if(t != null){
            		android.util.Log.v(getTag(), msg,t);
            	}else{
            		android.util.Log.v(getTag(), msg);
            	}
            	break;
            case WARN:
            	if(t != null){
            		android.util.Log.w(getTag(), msg,t);
            	}else{
            		android.util.Log.w(getTag(), msg);
            	}
            	break;
            }
         } catch (Throwable ignored) {}
	}

}
