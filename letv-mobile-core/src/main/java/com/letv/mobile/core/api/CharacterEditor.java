package com.letv.mobile.core.api;


/**
 * A property editor for {@link java.lang.Character}.
 *
 * @todo REVIEW: look at possibly parsing escape sequences?
 * @version $Revision$
 * @author adrian@jboss.org
 */
public class CharacterEditor extends PropertyEditorSupport
{
   public void setAsText(final String text)
   {
      if (PropertyEditors.isNull(text))
      {
         setValue(null);
         return;
      }
      if (text.length() != 1)
         throw new IllegalArgumentException("Too many (" + text.length() + ") characters: '" + text + "'"); 
      Object newValue = new Character(text.charAt(0));
      setValue(newValue);
   }
}
