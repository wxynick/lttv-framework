package com.letv.mobile.core.api;


/** A property editor for {@link java.lang.Integer}.
*
* @version $Revision$
* @author Scott.Stark@jboss.org
*/
public class IntegerEditor extends PropertyEditorSupport
{
  /** Map the argument text into and Integer using Integer.decode.
   */
  public void setAsText(final String text)
  {
     if (PropertyEditors.isNull(text))
     {
        setValue(null);
        return;
     }
     Object newValue = Integer.decode(text);
     setValue(newValue);
  }
}