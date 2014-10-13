/**
 * 
 */
package com.letv.mobile.core.command.annotation;

/**
 * @author  
 *
 */
public enum NetworkConnectionType {
	GSM(3),
	G3(2),
	WIFI(1),
	G4(4);
	
	private int id;
	
	NetworkConnectionType(int id){
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	
	public static NetworkConnectionType byId(int id){
		for (NetworkConnectionType type : values()) {
			if(type.id == id){
				return type;
			}
		}
		return null;
	}
}
