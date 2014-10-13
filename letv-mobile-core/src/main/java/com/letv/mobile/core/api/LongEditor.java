package com.letv.mobile.core.api;



/**
 * A property editor for {@link Integer}.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class LongEditor extends PropertyEditorSupport
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
      Object newValue = Long.valueOf(text);
      setValue(newValue);
   }
}

