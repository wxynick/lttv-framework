package com.letv.mobile.core.api;


/** A property editor for {@link java.lang.Boolean}.
*
* @version $Revision$
* @author Scott.Stark@jboss.org
*/
public class BooleanEditor extends PropertyEditorSupport
{
  private static final String[] BOOLEAN_TAGS = {"true", "false"};

  /** Map the argument text into Boolean.TRUE or Boolean.FALSE
   using Boolean.valueOf.
   */
  public void setAsText(final String text)
  {
     if (PropertyEditors.isNull(text))
     {
        setValue(null);
        return;
     }
     Object newValue = Boolean.valueOf(text);
     setValue(newValue);
  }

  /**
   @return the values {"true", "false"}
   */
  public String[] getTags()
  {
     return BOOLEAN_TAGS;
  }
}
