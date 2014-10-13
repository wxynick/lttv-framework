package com.letv.mobile.core.api;


/**
 * A property editor for {@link java.lang.Double}.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class DoubleEditor extends PropertyEditorSupport
{
   /**
    * Map the argument text into and Integer using Integer.valueOf.
    */
   public void setAsText(final String text)
   {
      if (PropertyEditors.isNull(text))
      {
         setValue(null);
         return;
      }
      Object newValue = Double.valueOf(text);
      setValue(newValue);
   }
}

