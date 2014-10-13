/**
 * 
 */
package com.letv.mobile.android.preference;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import android.content.SharedPreferences;

import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.util.IteratorEnumeration;

/**
 * @author  
 *
 */
public class PreferenceDictionary extends Dictionary<String, String> {
	private static final Trace log = Trace.register(PreferenceDictionary.class);
	
	static interface PreferenceManagerContext {
		SharedPreferences getSharedPreferences(String pid);
		void notifyPreferenceChanged(String pid, PreferenceDictionary config);
	}
	
	private static class ChangedRecord {
		PreferenceDictionary pref;
		long changedTime;
	}
	private static Map<String,ChangedRecord> changedRecords = new HashMap<String,ChangedRecord>();
	
	private static void updatePrefChanged(PreferenceDictionary pref){
		synchronized(changedRecords){
			ChangedRecord recd = changedRecords.get(pref.getPid());
			if(recd == null){
				recd = new ChangedRecord();
				recd.pref = pref;
				changedRecords.put(pref.getPid(), recd);
			}
			recd.changedTime = System.currentTimeMillis();
		}
	}
	
	private static void doNotify(){
		synchronized(changedRecords){
			if(changedRecords.isEmpty()){
				return;
			}
			String[] keys = changedRecords.keySet().toArray(new String[changedRecords.size()]);
			for (String key : keys) {
				ChangedRecord recd = changedRecords.get(key);
				if(recd != null){
					if((System.currentTimeMillis() - recd.changedTime) >= 1000L){
						changedRecords.remove(key);
						try {
						recd.pref.ctx.notifyPreferenceChanged(key, recd.pref);
						}catch(Throwable t){
							log.error("Caught throwable when notify preference changed of :"+key, t);
						}
					}
				}
			}
		}
	}
	
	static {
		Thread t = new Thread("Preference changed notifier thread") {
			public void run() {
				try {
					Thread.sleep(200L);
				} catch (InterruptedException e) {
					return;
				}
				try {
					doNotify();
				}catch(Throwable t){
					log.error("Caught throwable when process preference changed notification", t);
				}
			}
		};
		t.setDaemon(true);
		t.start();
	}
	
	private final String pid;
	private final PreferenceManagerContext ctx;
	private SharedPreferences.OnSharedPreferenceChangeListener listener;
	
	public PreferenceDictionary(String pid, PreferenceManagerContext context){
		this.ctx = context;
		this.pid = pid;
	}
	
	public  SharedPreferences  getPreference(){
		SharedPreferences pref = this.ctx.getSharedPreferences(pid);
		if(pref != null){
			synchronized(this){
				if(this.listener == null){
					this.listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
						
						@Override
						public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
								String key) {
							updatePrefChanged(PreferenceDictionary.this);
						}
					};
					pref.registerOnSharedPreferenceChangeListener(listener);
				}
			}
		}
		return pref;
	}
	
	public String getPid(){
		return this.pid;
	}
	
	private class PrefEnumeration implements Enumeration<String>{
		private SharedPreferences pref = getPreference();
		
		private final Iterator<String> keys = pref.getAll().keySet().iterator();
		@Override
		public String nextElement() {
			String key = keys.next();
			return key != null ? pref.getString(key, null) : null;
		}
		
		@Override
		public boolean hasMoreElements() {
			return keys.hasNext();
		}
	};
	
	@Override
	public int size() {
		return getPreference().getAll().size();
	}

	@Override
	public boolean isEmpty() {
		return size() == 0;
	}

	@Override
	public Enumeration<String> keys() {
		return new IteratorEnumeration<String>(getPreference().getAll().keySet().iterator());
	}

	@Override
	public Enumeration<String> elements() {
		return new PrefEnumeration();
	}

	@Override
	public String get(Object key) {
		return key != null ? getPreference().getString((String)key, null) : null;
	}

	@Override
	public String put(String key, String value) {
		throw new UnsupportedOperationException("This Dictionary is read only !");
	}

	@Override
	public String remove(Object key) {
		throw new UnsupportedOperationException("This Dictionary is read only !");
	}

	
	public void destroy() {
		synchronized(this){
			if(this.listener != null){
				ctx.getSharedPreferences(pid).unregisterOnSharedPreferenceChangeListener(listener);
				this.listener = null;
			}
		}
		synchronized(changedRecords){
			changedRecords.remove(pid);
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer('{');
		int cnt = 0;
		for(Enumeration<String> enu = keys() ; enu.hasMoreElements();){
			String key = enu.nextElement();
			String value = get(key);
			if(cnt > 0){
				buf.append(',');
			}
			buf.append(key).append(':').append(value);
			cnt++;
		}
		return buf.append('}').toString();
	}
}
