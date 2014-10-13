/*
 * @(#)IKernelService.java	 Sep 21, 2010
 *
 * Copyright 2004-2010 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */

package com.letv.mobile.core.microkernel.api;

/**
 * @class desc A IKernelService.
 * 
 * @author  
 * @version v1.0 
 * @created time Sep 21, 2010  5:09:23 PM
 */
public interface IKernelModule<T extends IKernelContext> {
   void start(T ctx);
   void stop();
   ModuleStatus getStatus();
   String getModuleName();
}
