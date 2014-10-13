package com.letv.mobile.core.api;



/** A property editor for {@link java.lang.Short}.
 *
 * @author Scott.Stark@jboss.org
 * @version $Revision$
 */
public class ShortEditor extends PropertyEditorSupport
{
   /** Map the argument text into and Short using Short.decode.
    */
   public void setAsText(final String text)
   {
      if (PropertyEditors.isNull(text))
      {
         setValue(null);
         return;
      }
      Object newValue = Short.decode(text);
      setValue(newValue);
   }

}
