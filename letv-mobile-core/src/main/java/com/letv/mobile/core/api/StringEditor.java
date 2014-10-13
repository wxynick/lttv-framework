package com.letv.mobile.core.api;


/**
 * A property editor for {@link java.lang.String}.
 * 
 * It is really a no-op but it is hoped to provide
 * slightly better performance, by avoiding the continuous
 * lookup/failure of a property editor for plain Strings
 * within the org.jboss.util.propertyeditor package,
 * before falling back to the jdk provided String editor.
 *
 * @author <a href="dimitris@jboss.org">Dimitris Andreadis</a>
 * @version $Revision$
 */
public class StringEditor extends PropertyEditorSupport
{
   /**
    * Keep the provided String as is.
    */
   public void setAsText(String text)
   {
      setValue(text);
   }
}
