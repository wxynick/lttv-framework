/**
 * 
 */
package org.apache.log4j.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.apache.log4j.IDateFormat;

/**
 * @author  
 *
 */
public class PatternDateFormat implements IDateFormat {

	private SimpleDateFormat fmt;
	
	public PatternDateFormat(String pattern){
		this.fmt = new SimpleDateFormat(pattern);
	}
	/* (non-Javadoc)
	 * @see org.apache.log4j.IDateFormat#setTimeZone(java.util.TimeZone)
	 */
	@Override
	public void setTimeZone(TimeZone timezone) {
		fmt.setTimeZone(timezone);
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.IDateFormat#format(java.util.Date)
	 */
	@Override
	public String format(Date date) {
		return fmt.format(date);
	}

}
