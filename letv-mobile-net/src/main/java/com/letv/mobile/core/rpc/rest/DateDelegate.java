package com.letv.mobile.core.rpc.rest;

import java.util.Date;

import com.letv.javax.ws.rs.ext.RuntimeDelegate;
import com.letv.mobile.core.util.DateUtil;

/**
 * @author <a href="mailto:bill@burkecentral.com">Bill Burke</a>
 * @version $Revision$
 */
public class DateDelegate implements RuntimeDelegate.HeaderDelegate<Date>
{
   @Override
   public Date fromString(String value)
   {
      if (value == null) throw new IllegalArgumentException("param was null");
      return DateUtil.parseDate(value);
   }

   @Override
   public String toString(Date value)
   {
      if (value == null) throw new IllegalArgumentException("param was null");
      return DateUtil.formatDate(value);
   }
}