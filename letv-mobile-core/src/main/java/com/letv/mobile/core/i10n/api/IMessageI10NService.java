/**
 * 
 */
package com.letv.mobile.core.i10n.api;

import java.util.Map;

/**
 * @author  
 *
 */
public interface IMessageI10NService {
	
	String getMessageTemplate(String key);
	
	String getMessage(String key,I10NContext ctx);
	
	public interface I10NContext {
		Map<String, Object> getParameters();
	}
}
