package com.letv.mobile.core.api;


/** A property editor for {@link java.lang.Byte}.
*
* @version $Revision$
*/
public class ByteEditor extends PropertyEditorSupport
{
  /** Map the argument text into and Byte using Byte.decode.
   */
  public void setAsText(final String text)
  {
     if (PropertyEditors.isNull(text))
     {
        setValue(null);
        return;
     }
     Object newValue = Byte.decode(text);
     setValue(newValue);
  }
}
