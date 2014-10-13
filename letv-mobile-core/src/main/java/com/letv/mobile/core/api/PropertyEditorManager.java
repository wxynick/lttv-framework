/**
 * 
 */
package com.letv.mobile.core.api;



/**
 * @author wangyan
 *
 */
public class PropertyEditorManager {
    /**
     * Registers an editor class to edit values of the given target class.
     * If the editor class is {@code null},
     * then any existing definition will be removed.
     * Thus this method can be used to cancel the registration.
     * The registration is canceled automatically
     * if either the target or editor class is unloaded.
     * <p>
     * If there is a security manager, its {@code checkPropertiesAccess}
     * method is called. This could result in a {@linkplain SecurityException}.
     *
     * @param targetType   the class object of the type to be edited
     * @param editorClass  the class object of the editor class
     * @throws SecurityException  if a security manager exists and
     *                            its {@code checkPropertiesAccess} method
     *                            doesn't allow setting of system properties
     *
     * @see SecurityManager#checkPropertiesAccess
     */
    public static void registerEditor(Class<?> targetType, Class<PropertyEditor> editorClass) {
        PropertyEditors.registerEditor(targetType, editorClass);
    }
	
    /**
     * Locate a value editor for a given target type.
     *
     * @param targetType  The Class object for the type to be edited
     * @return An editor object for the given target class.
     * The result is null if no suitable editor can be found.
     */
    public static PropertyEditor findEditor(Class<?> targetType) {
    	 	return PropertyEditors.findEditor(targetType);
    }
}
