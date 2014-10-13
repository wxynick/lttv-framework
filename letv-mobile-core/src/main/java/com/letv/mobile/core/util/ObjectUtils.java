package com.letv.mobile.core.util;


public abstract class ObjectUtils {
	public static boolean isEquals(Object obj1, Object obj2){
		if(obj1 == obj2){
			return true;
		}
		if((obj1 == null)&&(obj2 == null)){
			return true;
		}
		if((obj1 != null)&&(obj1.equals(obj2))){
			return true;
		}
		return false;
	}
}
