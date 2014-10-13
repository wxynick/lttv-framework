/*
 * @(#)LRUMapEvictionListener.java	 2005-11-29
 *
 * Copyright 2004-2005 WXXR Network Technology Co. Ltd. 
 * All rights reserved.
 * 
 * WXXR PROPRIETARY/CONFIDENTIAL.
 */
package com.letv.mobile.core.util;

public interface LRUMapEvictionListener<K, V> {
    void objectEvicted(K key, V val);
}
