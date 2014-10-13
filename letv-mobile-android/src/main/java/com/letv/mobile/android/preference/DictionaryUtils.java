package com.letv.mobile.android.preference;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

public class DictionaryUtils {
	
	public static Dictionary<String, String> clone(Dictionary<String, String> data){
		Hashtable<String, String> t = new Hashtable<String, String>();
		if(data.size() > 0){
			for(Enumeration<String> enu = data.keys();enu.hasMoreElements();){
				String key = enu.nextElement();
				t.put(key, data.get(key));
			}
		}
		return t;
	}

	public static Dictionary<String, String> clone(Properties data){
		Hashtable<String, String> t = new Hashtable<String, String>();
		if(data.size() > 0){
			for(Enumeration<?> enu = data.propertyNames();enu.hasMoreElements();){
				String key = (String)enu.nextElement();
				t.put(key, data.getProperty(key));
			}
		}
		return t;
	}

}
