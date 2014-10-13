package com.letv.mobile.android.log;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.spi.LoggingEvent;

import android.util.Log;

/**
 * Appender for {@link Log}
 * @author Rolf Kulemann, Pascal Bockhorn
 */
public class LogCatAppender extends AppenderSkeleton {
	private Map<String, String> map = new ConcurrentHashMap<String, String>();

	public LogCatAppender(final Layout messageLayout, final Layout tagLayout) {
		setLayout(messageLayout); 
	}

	public LogCatAppender(final Layout messageLayout) {
		this(messageLayout, new PatternLayout("%c"));
	}

	public LogCatAppender() {
		this(new PatternLayout("%m%n"));
	}

	private String getTag(String loggerName) {
		String tag = map.get(loggerName);
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
			map.put(loggerName, tag);
		}
		return tag;
	}

//	private static int translate(final int level) {
//		switch (level) {
//			case Level.FATAL_INT: return android.util.Log.ASSERT;
//			case Level.ERROR_INT: return android.util.Log.ERROR;
//			case Level.WARN_INT:  return android.util.Log.WARN;
//			case Level.INFO_INT:  return android.util.Log.INFO;
//			case Level.DEBUG_INT: return android.util.Log.DEBUG;
//			case Level.TRACE_INT: return android.util.Log.VERBOSE;
//		}
//		return android.util.Log.VERBOSE;
//	}

	@Override
	protected void append(final LoggingEvent le) {
		String tag = getTag(le.getLoggerName());
//		if(!Log.isLoggable(tag, translate(le.getLevel().toInt()))){
//			return;
//		}
		switch (le.getLevel().toInt()) {
		case Level.TRACE_INT:
			if (le.getThrowableInformation() != null) {
				Log.v(tag, getLayout().format(le), le.getThrowableInformation().getThrowable());
			}
			else {
				Log.v(tag, getLayout().format(le));
			}
			break;
		case Level.DEBUG_INT:
			if (le.getThrowableInformation() != null) {
				Log.d(tag, getLayout().format(le), le.getThrowableInformation().getThrowable());
			}
			else {
				Log.d(tag, getLayout().format(le));
			}
			break;
		case Level.INFO_INT:
			if (le.getThrowableInformation() != null) {
				Log.i(tag, getLayout().format(le), le.getThrowableInformation().getThrowable());
			}
			else {
				Log.i(tag, getLayout().format(le));
			}
			break;
		case Level.WARN_INT:
			if (le.getThrowableInformation() != null) {
				Log.w(tag, getLayout().format(le), le.getThrowableInformation().getThrowable());
			}
			else {
				Log.w(tag, getLayout().format(le));
			}
			break;
		case Level.ERROR_INT:
			if (le.getThrowableInformation() != null) {
				Log.e(tag, getLayout().format(le), le.getThrowableInformation().getThrowable());
			}
			else {
				Log.e(tag, getLayout().format(le));
			}
			break;
		case Level.FATAL_INT:
			if (le.getThrowableInformation() != null) {
				Log.wtf(tag, getLayout().format(le), le.getThrowableInformation().getThrowable());
			}
			else {
				Log.wtf(tag, getLayout().format(le));
			}
			break;
		}
	}

	@Override
	public void close() {
	}

	@Override
	public boolean requiresLayout() {
		return true;
	}
	
}
