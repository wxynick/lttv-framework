/**
 * 
 */
package com.letv.mobile.core.bean.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.letv.mobile.core.util.StringUtils;

/**
 * @author  
 *
 */
public class BindableBeanPropertyChangedEvent {
	private final Object source;
	private List<String> propertyNames;
	
	public BindableBeanPropertyChangedEvent(Object source, String... propertyNames){
		if(source == null){
			throw new IllegalArgumentException("Invalid source : NULL !");
		}
		this.source = source;
		if(propertyNames != null){
			for (String name : propertyNames) {
				addChangedProperty(name);
			}
		}
	}
	
	public BindableBeanPropertyChangedEvent addChangedProperty(String name){
		if(StringUtils.isBlank(name)){
			throw new IllegalArgumentException("Invalid property name : NULL");
		}
		if(this.propertyNames == null){
			this.propertyNames = new ArrayList<String>();
		}
		if(!this.propertyNames.contains(name)){
			this.propertyNames.add(name);
		}
		return this;
	}
	
	public BindableBeanPropertyChangedEvent removeChangedProperty(String name){
		if(this.propertyNames != null){
			this.propertyNames.remove(name);
		}
		return this;
	}
	
	public List<String> getChangedPropertyNames() {
		return this.propertyNames != null ? Collections.unmodifiableList(this.propertyNames) : null;
	}
	
	public Object getSource() {
		return this.source;
	}
}
