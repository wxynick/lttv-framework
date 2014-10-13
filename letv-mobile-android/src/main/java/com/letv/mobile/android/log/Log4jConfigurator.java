package com.letv.mobile.android.log;

import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;

import com.letv.mobile.core.util.BasicLog4jConfigurator;

public class Log4jConfigurator extends BasicLog4jConfigurator {
	private String logCatPattern = "%m%n";
	
	public Log4jConfigurator() {
		super();
	}

	public Log4jConfigurator(String fileName, int maxBackupSize,
			long maxFileSize, String filePattern, Level rootLevel) {
		super(fileName, maxBackupSize, maxFileSize, filePattern, rootLevel);
	}

	public Log4jConfigurator(String fileName, Level rootLevel,
			String filePattern) {
		super(fileName, rootLevel, filePattern);
	}

	public Log4jConfigurator(String fileName, Level rootLevel) {
		super(fileName, rootLevel);
	}

	public Log4jConfigurator(String fileName) {
		super(fileName);
	}

	
	public void configureLogCatAppender(String category,Priority threshold) {
		LogCatAppender appender = new LogCatAppender();
		appender.setLayout(new PatternLayout(getLogCatPattern()));
		addAppender(category,appender,threshold);
	}
	
	public String getLogCatPattern() {
		return logCatPattern;
	}

	public void setLogCatPattern(final String logCatPattern) {
		this.logCatPattern = logCatPattern;
	}

}