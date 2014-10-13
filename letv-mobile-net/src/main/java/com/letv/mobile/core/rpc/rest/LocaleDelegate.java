package com.letv.mobile.core.rpc.rest;

import java.util.Locale;

import com.letv.javax.ws.rs.ext.RuntimeDelegate;
import com.letv.mobile.core.util.LocaleHelper;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class LocaleDelegate implements RuntimeDelegate.HeaderDelegate<Locale>
{
   public Locale fromString(String value) throws IllegalArgumentException
   {
      if (value == null) throw new IllegalArgumentException("Locale value is null");
      return LocaleHelper.extractLocale(value);
   }

   public String toString(Locale value)
   {
      if (value == null) throw new IllegalArgumentException("param was null");
      return LocaleHelper.toLanguageString(value);
   }

}