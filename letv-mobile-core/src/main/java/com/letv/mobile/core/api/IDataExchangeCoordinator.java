/**
 * 
 */
package com.letv.mobile.core.api;

/**
 * 
 * coordinate data exchange with server in network favor manner
 * @author  
 *
 */
public interface IDataExchangeCoordinator {
	int NETWORK_ID_WIFI = 1;
	int NETWORK_ID_3G = 2;
	int NETWORK_ID_GSM = 3;
	
	void registerHandler(IExchangeHandler handler);
	
	boolean unregisterHandler(IExchangeHandler handler);
	
	int checkAvailableNetwork();

}
