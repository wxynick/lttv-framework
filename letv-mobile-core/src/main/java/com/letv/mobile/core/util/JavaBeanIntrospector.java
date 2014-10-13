/**
 * 
 */
package com.letv.mobile.core.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author  
 *
 */
public class JavaBeanIntrospector {
	private static final String GET_PREFIX = "get";
	private static final String SET_PREFIX = "set";
	private static final String IS_PREFIX = "is";

	/*
	 * Internal method to return *public* methods within a class.
	 */
	private static Method[] getPublicDeclaredMethods(Class clz) {
		Method[] result = clz.getMethods();
		for (int i = 0; i < result.length; i++) {
			Method method = result[i];
			if (!method.getDeclaringClass().equals(clz)) {

				result[i] = null; 
			} 
		}
		return result;
	}


	/**
	 * Internal support for finding a target methodName with a given
	 * parameter list on a given class.
	 */
	private static Method internalFindMethod(Class start, String methodName,
			int argCount, Class args[]) {
		// For overriden methods we need to find the most derived version.
		// So we start with the given class and walk up the superclass chain.

		Method method = null;

		for (Class cl = start; cl != null; cl = cl.getSuperclass()) {
			Method methods[] = getPublicDeclaredMethods(cl);
			for (int i = 0; i < methods.length; i++) {
				method = methods[i];
				if (method == null) {
					continue;
				}

				// make sure method signature matches.
				Class params[] = method.getParameterTypes();
				if (method.getName().equals(methodName) && 
						params.length == argCount) {
					if (args != null) {
						boolean different = false;
						if (argCount > 0) {
							for (int j = 0; j < argCount; j++) {
								if (params[j] != args[j]) {
									different = true;
									continue;
								}
							}
							if (different) {
								continue;
							}
						}
					}
					return method;
				}
			}
		}
		method = null;

		// Now check any inherited interfaces.  This is necessary both when
		// the argument class is itself an interface, and when the argument
		// class is an abstract class.
		Class ifcs[] = start.getInterfaces();
		for (int i = 0 ; i < ifcs.length; i++) {
			// Note: The original implementation had both methods calling
			// the 3 arg method. This is preserved but perhaps it should
			// pass the args array instead of null.
			method = internalFindMethod(ifcs[i], methodName, argCount, null);
			if (method != null) {
				break;
			}
		}
		return method;
	}

	/**
	 * Find a target methodName on a given class.
	 */
	public static Method findMethod(Class cls, String methodName, int argCount) {
		return findMethod(cls, methodName, argCount, null);
	}

	/**
	 * Utility method to take a string and convert it to normal Java variable
	 * name capitalization.  This normally means converting the first
	 * character from upper case to lower case, but in the (unusual) special
	 * case when there is more than one character and both the first and
	 * second characters are upper case, we leave it alone.
	 * <p>
	 * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
	 * as "URL".
	 *
	 * @param  name The string to be decapitalized.
	 * @return  The decapitalized version of the string.
	 */
	public static String decapitalize(String name) {
		if (name == null || name.length() == 0) {
			return name;
		}
		if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
				Character.isUpperCase(name.charAt(0))){
			return name;
		}
		char chars[] = name.toCharArray();
		chars[0] = Character.toLowerCase(chars[0]);
		return new String(chars);
	}

	/**
	 * Find a target methodName with specific parameter list on a given class.
	 * <p>
	 * Used in the contructors of the EventSetDescriptor, 
	 * PropertyDescriptor and the IndexedPropertyDescriptor.
	 * <p>
	 * @param cls The Class object on which to retrieve the method.
	 * @param methodName Name of the method.
	 * @param argCount Number of arguments for the desired method.
	 * @param args Array of argument types for the method.
	 * @return the method or null if not found
	 */
	public static Method findMethod(Class cls, String methodName, int argCount, 
			Class args[]) {
		if (methodName == null) {
			return null;
		}
		return internalFindMethod(cls, methodName, argCount, args);
	}

	/**
	 * @return An array of PropertyDescriptors describing the editable
	 * properties supported by the target bean.
	 */

	private PropertyDescriptor[] getTargetPropertyInfo() {
		
		// Check if the bean has its own BeanInfo that will provide
		// explicit information.
	        PropertyDescriptor[] explicitProperties = null;

		if (superBeanInfo != null) {
		    // We have no explicit BeanInfo properties.  Check with our parent.
		    PropertyDescriptor supers[] = superBeanInfo.getPropertyDescriptors();
		    for (int i = 0 ; i < supers.length; i++) {
			addPropertyDescriptor(supers[i]);
		    }
//		    int ix = superBeanInfo.getDefaultPropertyIndex();
//		    if (ix >= 0 && ix < supers.length) {
//			defaultPropertyName = supers[ix].getName();
//		    }
		}

		for (int i = 0; i < additionalBeanInfo.length; i++) {
		    PropertyDescriptor additional[] = additionalBeanInfo[i].getPropertyDescriptors();
		    if (additional != null) {
		        for (int j = 0 ; j < additional.length; j++) {
			    addPropertyDescriptor(additional[j]);
		        }
		    }
		}


		// Apply some reflection to the current class.

		// First get an array of all the public methods at this level
		Method methodList[] = getPublicDeclaredMethods(beanClass);

		// Now analyze each method.
		for (int i = 0; i < methodList.length; i++) {
			Method method = methodList[i];
			if (method == null) {
				continue;
			}
			// skip static methods.
			int mods = method.getModifiers();
			if (Modifier.isStatic(mods)) {
				continue;
			}
			String name = method.getName();
			Class argTypes[] = method.getParameterTypes();
			Class resultType = method.getReturnType();
			int argCount = argTypes.length;
			PropertyDescriptor pd = null;

			if (name.length() <= 3 && !name.startsWith(IS_PREFIX)) {
				// Optimization. Don't bother with invalid propertyNames.
				continue;
			}

			try {


	            if (argCount == 0) {
		        if (name.startsWith(GET_PREFIX)) {
		            // Simple getter
	                    pd = new PropertyDescriptor(decapitalize(name.substring(3)),
						method, null);
	                } else if (resultType == boolean.class && name.startsWith(IS_PREFIX)) {
		            // Boolean getter
	                    pd = new PropertyDescriptor(decapitalize(name.substring(2)),
						method, null);
		        }
	            } else if (argCount == 1) {
		        if (argTypes[0] == int.class && name.startsWith(GET_PREFIX)) {
		            pd = new IndexedPropertyDescriptor(
						decapitalize(name.substring(3)),
						null, null,
						method,	null);
		        } else if (resultType == void.class && name.startsWith(SET_PREFIX)) {
		            // Simple setter
	                    pd = new PropertyDescriptor(decapitalize(name.substring(3)),
						null, method);
		        }
	            } else if (argCount == 2) {
			    if (argTypes[0] == int.class && name.startsWith(SET_PREFIX)) {
	                    pd = new IndexedPropertyDescriptor(
						decapitalize(name.substring(3)),
						null, null,
						null, method);
			}
		    }
			} catch (IntrospectionException ex) {
				// This happens if a PropertyDescriptor or IndexedPropertyDescriptor
				// constructor fins that the method violates details of the deisgn
				// pattern, e.g. by having an empty name, or a getter returning
				// void , or whatever.
				pd = null;
			}

			if (pd != null) {
				addPropertyDescriptor(pd);
			}
		}
		processPropertyDescriptors();

		// Allocate and populate the result array.
		PropertyDescriptor result[] = new PropertyDescriptor[properties.size()];
		result = (PropertyDescriptor[])properties.values().toArray(result);

		return result;
	}


	public static BeanInfo getBeanInfo(Class<?> beanClass)
	{
		return new JavaBeanIntrospector(beanClass, null).getBeanInfo();
	}

	public static BeanInfo getBeanInfo(Class<?> beanClass,Class<?> stopClass)
	{
		return new JavaBeanIntrospector(beanClass, stopClass).getBeanInfo();
	}

	private Class beanClass;
	private BeanInfo superBeanInfo;
	private BeanInfo[] additionalBeanInfo;

	private JavaBeanIntrospector(Class beanClass, Class stopClass) {
		this.beanClass = beanClass;

//		// Check stopClass is a superClass of startClass.
//		if (stopClass != null) {
//			boolean isSuper = false;
//			for (Class c = beanClass.getSuperclass(); c != null; c = c.getSuperclass()) {
//				if (c == stopClass) {
//					isSuper = true;
//				}
//			}
//			if (!isSuper) {
//				throw new IntrospectionException(stopClass.getName() + " not superclass of " + 
//						beanClass.getName());
//			}
//		}
//		if(stopClass == null){
//			stopClass = Object.class;
//		}
		Class superClass = beanClass.getSuperclass();
		if (superClass != stopClass) {
			superBeanInfo = new JavaBeanIntrospector(superClass, stopClass).getBeanInfo();
		}
		if (additionalBeanInfo == null) {
			additionalBeanInfo = new BeanInfo[0];
		}
	}
	private HashMap pdStore = new HashMap();
	private Map properties;

	/**
	 * Adds the property descriptor to the list store.
	 */
	private void addPropertyDescriptor(PropertyDescriptor pd) {
		String propName = pd.getName();
		List list = (List)pdStore.get(propName);
		if (list == null) {
			list = new ArrayList();
			pdStore.put(propName, list);
		}
		list.add(pd);
	}

	/**
	 * Populates the property descriptor table by merging the 
	 * lists of Property descriptors.
	 */ 
	private void processPropertyDescriptors() {
		if (properties == null) {
			properties = new TreeMap();
		}

		List list;

		PropertyDescriptor pd, gpd, spd;

		Iterator it = pdStore.values().iterator();
		while (it.hasNext()) {
			pd = null; gpd = null; spd = null; 

			list = (List)it.next();

			// First pass. Find the latest getter method. Merge properties
			// of previous getter methods.
			for (int i = 0; i < list.size(); i++) {
				pd = (PropertyDescriptor)list.get(i);
				if (pd.getReadMethod() != null) {
					if (gpd != null) {
						// Don't replace the existing read
						// method if it starts with "is"
						Method method = gpd.getReadMethod();
						if (!method.getName().startsWith(IS_PREFIX)) {
							gpd = new PropertyDescriptor(gpd, pd);
						}
					} else {
						gpd = pd;
					}
				}
			}

			// Second pass. Find the latest setter method which
			// has the same type as the getter method.
			for (int i = 0; i < list.size(); i++) {
				pd = (PropertyDescriptor)list.get(i);
				if (pd.getWriteMethod() != null) {
					if (gpd != null) {
						if (gpd.getPropertyType() == pd.getPropertyType()) {
							if (spd != null) {
								spd = new PropertyDescriptor(spd, pd);
							} else {
								spd = pd;
							}
						}
					} else {
						if (spd != null) {
							spd = new PropertyDescriptor(spd, pd);
						} else {
							spd = pd;
						}
					}
				}
			}

			// At this stage we should have either PDs or IPDs for the
			// representative getters and setters. The order at which the 
			// property descriptors are determined represent the 
			// precedence of the property ordering.
			pd = null;
			if (gpd != null && spd != null) {
				// Complete simple properties set
				if (gpd == spd) {
					pd = gpd;
				} else {
					pd = mergePropertyDescriptor(gpd, spd);
				}
			} else if (spd != null) {
				// simple setter
				pd = spd;
			} else if (gpd != null) {
				// simple getter
				pd = gpd;
			}


			// Find the first property descriptor
			// which does not have getter and setter methods.
			// See regression bug 4984912.
			if ( (pd == null) && (list.size() > 0) ) {
				pd = (PropertyDescriptor) list.get(0);
			}

			if (pd != null) {
				properties.put(pd.getName(), pd);
			}
		}
	}


	// Handle regular pd merge
	private PropertyDescriptor mergePropertyDescriptor(PropertyDescriptor pd1,
			PropertyDescriptor pd2) {
		if (pd1.getClass0().isAssignableFrom(pd2.getClass0())) {
			return new PropertyDescriptor(pd1, pd2);
		} else {
			return new PropertyDescriptor(pd2, pd1);
		}
	}

	/**
	 * Constructs a GenericBeanInfo class from the state of the Introspector
	 */
	private BeanInfo getBeanInfo() {

		PropertyDescriptor pds[] = getTargetPropertyInfo();
		return new SimpleBeanInfo(pds);

	}

}
