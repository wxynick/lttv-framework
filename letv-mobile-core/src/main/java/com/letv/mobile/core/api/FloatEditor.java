package com.letv.mobile.core.api;


/**
 * A property editor for {@link Integer}.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class FloatEditor extends PropertyEditorSupport
{
   /**
    * Map the argument text into and Integer using Integer.valueOf.
    */
   public void setAsText(final String text)
   {
      Object newValue = Float.valueOf(text);
      setValue(newValue);
   }
}
