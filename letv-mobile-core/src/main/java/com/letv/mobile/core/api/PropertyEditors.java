package com.letv.mobile.core.api;

import java.util.concurrent.ConcurrentHashMap;

import com.letv.mobile.core.log.api.Trace;


public class PropertyEditors {

	/** The null string */
	private static final String NULL = "null";
	/** Whether we handle nulls */
	private static boolean disableIsNull = false;
	private  static ConcurrentHashMap<Class<?>, Class<?>> map = new ConcurrentHashMap<Class<?>, Class<?>>();

	static{
		registerEditor(Integer.TYPE, IntegerEditor.class);
		registerEditor(Short.TYPE, ShortEditor.class);
		registerEditor(Byte.TYPE, ByteEditor.class);
		registerEditor(Long.TYPE, LongEditor.class);
		registerEditor(Float.TYPE, FloatEditor.class);
		registerEditor(Double.TYPE, DoubleEditor.class);
		registerEditor(String.class, StringEditor.class);
	}
	private static Trace log =Trace.register(PropertyEditor.class);
	public  static   void registerEditor(Class<?> clazz, Class<?>  editorClazz) {
		boolean isEditor=false;
		Class<?>[] classes=clazz.getInterfaces();
		if(classes!=null && classes.length>0){
			for(Class<?> c:classes){
				if(c==PropertyEditor.class){
					isEditor=true;
					break;
				}
			}
		}
		if(isEditor){
			map.put(clazz, editorClazz);
		}else{
			throw new IllegalArgumentException("editor  is not a PropertyEditor");
		}

		
		
	}

	@SuppressWarnings("rawtypes")
	public  static boolean unregisterEditor(Class<?> clazz,
			Class<?> editorClazz) {
		Class<?> old = map.get(clazz);
		if (old == editorClazz) {
			map.remove(clazz);
			return true;
		}
		return false;
	}


	/**
	 * Whether a string is interpreted as the null value, including the empty
	 * string.
	 * 
	 * @param value
	 *            the value
	 * @return true when the string has the value null
	 */
	public static final boolean isNull(final String value) {
		return isNull(value, true, true);
	}

	/**
	 * Whether a string is interpreted as the null value
	 * 
	 * @param value
	 *            the value
	 * @param trim
	 *            whether to trim the string
	 * @param empty
	 *            whether to include the empty string as null
	 * @return true when the string has the value null
	 */
	public static final boolean isNull(final String value, final boolean trim,
			final boolean empty) {
		// For backwards compatibility
		if (disableIsNull)
			return false;
		// No value?
		if (value == null)
			return true;
		// Trim the text when requested
		String trimmed = trim ? value.trim() : value;
		// Is the empty string null?
		if (empty && trimmed.length() == 0)
			return true;
		// Just check it.
		return NULL.equalsIgnoreCase(trimmed);
	}
	   /**
	    * Locate a value editor for a given target type.
	    *
	    * @param type   The class of the object to be edited.
	    * @return       An editor for the given type or null if none was found.
	    */
	   public static PropertyEditor findEditor(final Class type){
	      Class<?> clazz= map.get(type);
	      if(clazz!=null){
	    	  	try {
					return (PropertyEditor) clazz.newInstance();
				} catch (InstantiationException e) {
					log.error("findEditor error",e);
				} catch (IllegalAccessException e) {
					log.error("findEditor error",e);
				}
	      }
	      return null;
	   }
}
