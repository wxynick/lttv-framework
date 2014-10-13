/*
 * @(#)IEventObject.java May 18, 2011 Copyright 2004-2011 WXXR Network Technology
 * Co. Ltd. All rights reserved. WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.letv.mobile.core.event.api;

public interface IEventObject {

   public Object getSource();
         
   public boolean needSyncProcessed();
   
   public Long getTimestamp();

}