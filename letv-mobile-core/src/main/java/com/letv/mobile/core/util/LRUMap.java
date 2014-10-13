/*
 * $Id$
 * 
 * Copyright (C) 2003 Hygensoft Inc. All rights reserved.
 * 
 * Created on Nov 10, 2003
 */
package com.letv.mobile.core.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.KUtils;

/**
 * 
 * Description go here
 * 
 * @author   Lin
 * @version $Revision$
 *
 */
public class LRUMap<K,V> {
    
    private static Trace log = Trace.register(LRUMap.class);
    private static ExpirationProcessor exProcessor;
    
    private LinkedHashMap<K,V> map;
    private long expireTime;
    private int capacity;
    private ConcurrentLinkedQueue<LRUMapEvictionListener<K,V>> listeners;
//	private ExecutorService poolExecutor;
	private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private Map<K,Long> expirations;

    
	private static class ExpirationProcessor extends Thread {
        
        private List<WeakReference<LRUMap<?,?>>> refs ;
                
        public ExpirationProcessor(){
            super("LRU Expiration Processor");
            super.setDaemon(true);
            refs = Collections.synchronizedList(new ArrayList<WeakReference<LRUMap<?,?>>>());
        }
        
        public void run(){
            while(true){
                try {
                    Thread.sleep(10*1000);		// check for every 10 seconds
                } catch (InterruptedException e) {
                }
                try {
	                if(refs.isEmpty()){
	                	continue;
	                }
	                @SuppressWarnings("unchecked")
					WeakReference<LRUMap<?,?>>[] vals = refs.toArray(new WeakReference[refs.size()]);
	                for(WeakReference<LRUMap<?,?>> ref: vals) {
	                    LRUMap<?,?> map = ref.get();
	                    if(map != null){
	                    	map.checkExpiration();
	                    }else{
	                    	refs.remove(ref);
	                    }
					}
                }catch(Throwable t){
                	log.error("Caught throwable inside expiration processor of LRUMap", t);
                }
            }
        }
        
        public void addLRUMap(LRUMap<?, ?> map) {
        	WeakReference<LRUMap<?,?>> ref = new WeakReference<LRUMap<?,?>>(map);
        	this.refs.add(ref);
        }
    }


    /**
     * 
     */
    public LRUMap(int size) {
        this(size,0);
    }
    
    /**
     * 
     * @param size
     * @param expired , expiration time in seconds. member will be evicted if it has not been accessed
     * for longer time that expired. 0 : expiration will not apply in this map
     */
    public LRUMap(int size,int expired) {
    	this(size,expired,null);
    }
    
    /**
     * 
     * @param capacity
     * @param expired , expiration time in seconds. member will be evicted if it has not been accessed
     * for longer time that expired. 0 : expiration will not apply in this map
     */
    public LRUMap(int size,int expired,ExecutorService executor) {
        if(size <= 0){
            throw new IllegalArgumentException("Size should larger than 0");
        }
        map = new LinkedHashMap<K,V>(size);
//        keys = new ConcurrentLinkedQueue<K>();
        this.capacity = size;
        listeners = new ConcurrentLinkedQueue<LRUMapEvictionListener<K,V>>();  
//        if(executor == null){
////        	poolExecutor = KUtils.getApplication().getExecutor();
//        }else{
//        	poolExecutor = executor;
//        }
        if(expired > 0){
            this.expireTime = expired*1000L;
            startExpireProcessor();
            expirations = new ConcurrentHashMap<K,Long>(); 
            exProcessor.addLRUMap(this);
        }
    }

	/**
	 * 
	 */
	protected static synchronized void startExpireProcessor() {
		if(exProcessor == null){
			exProcessor = new ExpirationProcessor();
			exProcessor.start();
		}
	}
    

	/**
	 * @param map
	 */
	protected void checkExpiration() {
//		if(log.isDebugEnabled()){
//		    log.debug("Check for expiration of all memebers of LRUMap ");
//		}
		long currentTime = System.currentTimeMillis();
		Lock lock = readWriteLock.readLock();
		try {
			lock.lock();
			Iterator<Map.Entry<K,Long>> itr = expirations.entrySet().iterator();
			while((itr != null)&&(itr.hasNext())){
			    Map.Entry<K,Long> entry = itr.next();
			    if(entry == null){
			        continue;
			    }
			    Long lastAccess = entry.getValue();
			    if(lastAccess != null){
			        if((currentTime - lastAccess.longValue()) > expireTime){
			        	
			        	lock.unlock();
			        	Lock writelock = readWriteLock.writeLock();
			        	writelock.lock();
			            try {
			                K key = entry.getKey();
			                V value = map.remove(key);
			                expirations.remove(key);
			                notifyListeners(key,value);
			            } catch ( Throwable t){
			                log.warn("Failed to expire LRUMap memeber with key :"+entry.getKey(), t);
			            } finally{
			            	lock.lock();
			            	writelock.unlock();
			            }
			        }
			    }
			}
		} finally {
			if(lock != null){
				lock.unlock();
			}
		}
	}


    public boolean containsKey(K key){
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try{
	    	return map.containsKey(key);
    	}finally{
    		lock.unlock();
    	}
    }
    
    public V get(K key){
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try {
			if(map.containsKey(key)){
				Lock writeLock = readWriteLock.writeLock();
				lock.unlock();
				writeLock.lock();				
				try {
					map.put(key,map.remove(key));		// Most-Recent used is always in last
					if(expirations != null){
						expirations.put(key,System.currentTimeMillis());
					}
				}finally {
					lock.lock();
					writeLock.unlock();
				}
				return map.get(key);
			}
			return null;
		} finally {
			lock.unlock();
		}
    }
    
    public V peek(K key){
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try {
				return map.get(key);
		} finally {
			lock.unlock();
		}
    }

    public void put(K key, V value){
		if(key == null){
			throw new IllegalArgumentException("Key object cannot be null.");
		}
    	Lock lock = readWriteLock.writeLock();
    	lock.lock();
		try {
			if(map.containsKey(key)){
				map.remove(key);
			}else{
			    if(capacity == 0){
			        return;
			    }
				if(map.size()==capacity){	// reach the limit of key list
					K rmKey = map.keySet().iterator().next();	// remove the eldest(LRU) one
					V rmVal = map.remove(rmKey);
			        if(expirations != null){
			        	expirations.remove(rmKey);
			        }
			        notifyListeners(rmKey,rmVal);
				}
			    if(expirations != null){
			    	expirations.put(key,System.currentTimeMillis());
			    }
			}
			map.put(key,value);
		} finally {
			lock.unlock();
		}
    }
    
    public int size(){
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try{
	    	return map.size();
    	}finally{
    		lock.unlock();
    	}
    }
    
    public void setCapacity(int newSize){
    	Lock lock = readWriteLock.writeLock();
    	lock.lock();
        try {
			this.capacity = newSize;
			if(map.size() > capacity){
				int count = map.size()-capacity;
				for(Iterator<K> itr = map.keySet().iterator();itr.hasNext()&&(count > 0);count--){  // shrink the map
				    K rmKey = itr.next();   // remove the eldest(LRU) one
				    V rmVal = map.get(rmKey);
				    itr.remove();
				    if(expirations != null){
				    	expirations.remove(rmKey);
				    }
				    notifyListeners(rmKey,rmVal);
				}
			}
		} finally {
			lock.unlock();
		}
    }

    
    public V remove(K key) {
    	Lock lock = readWriteLock.writeLock();
    	lock.lock();
        try {
	        if(expirations != null){
	        	expirations.remove(key);
	        }
	        return map.remove(key);
        }finally{
        	lock.unlock();
        }
    }
    
    public V evict(K key) {
    	Lock lock = readWriteLock.writeLock();
    	lock.lock();
        try {
	        if(expirations != null){
	        	expirations.remove(key);
	        }
	        V val = map.remove(key);
	        if(val != null){
	        	notifyListeners((K)key,val);
	        }
	        return val;
        }finally{
        	lock.unlock();
        }
    }
    
    public List<K> keys(){
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try{
            return new LinkedList<K>(map.keySet());
       	}finally{
    		lock.unlock();
    	}
   }
   
    public List<K> getMRUKeys(int num){
    	LinkedList<K> keys = null;
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try{
            keys = new LinkedList<K>(map.keySet());
       	}finally{
    		lock.unlock();
    	}
       	if(keys.isEmpty()){
       		return null;
       	}
       	LinkedList<K> results = new LinkedList<K>();
       	for(int i=0 ; i < num ; i++){
	       	try {
				K key = keys.removeLast();
				results.add(key);
			} catch (NoSuchElementException e) {
				break;
			}
       	}
       	return results;
   }

    public void addListener(LRUMapEvictionListener<K,V> listener){
        if(listener == null){
            throw new IllegalArgumentException();
        }
        synchronized(listeners){
            listeners.add(listener);
        }
    }
    
    public  boolean removeListener(LRUMapEvictionListener<K,V> listener){
        if(listener == null){
            throw new IllegalArgumentException();
        }
        synchronized(listeners){
            return listeners.remove(listener); 
        }
    }
    
    private void notifyListeners(final K rmKey,final V rmVal) {
        if(listeners.isEmpty()){
            return;
        }
        KUtils.invokeLater(new Runnable() {		
			public void run() {
				doNotifyListeners(rmKey,rmVal);
			}
		
		});
    }
    
    private void doNotifyListeners(final K rmKey,final V rmVal) {
        if(listeners.isEmpty()){
            return;
        }
        for (LRUMapEvictionListener<K,V> l : listeners) {
            l.objectEvicted(rmKey,rmVal);
        }
    }
    

    /**
     * @return Returns the capacity.
     */
    public int getCapacity() {
    	Lock lock = readWriteLock.readLock();
    	lock.lock();
    	try{
            return capacity;
       	}finally{
    		lock.unlock();
    	}
    }
}
