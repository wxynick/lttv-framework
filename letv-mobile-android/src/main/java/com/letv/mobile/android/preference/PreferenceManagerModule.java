/**
 * 
 */
package com.letv.mobile.android.preference;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.letv.mobile.android.app.IAndroidAppContext;
import com.letv.mobile.core.log.api.Trace;
import com.letv.mobile.core.microkernel.api.AbstractModule;
import com.letv.mobile.core.util.StringUtils;
import com.letv.mobile.preference.api.IPreferenceChangedListener;
import com.letv.mobile.preference.api.IPreferenceManager;

/**
 * @author  
 *
 */
public class PreferenceManagerModule<T extends IAndroidAppContext> extends AbstractModule<T> implements
		IPreferenceManager {
	private static final Trace log = Trace.register(PreferenceManagerModule.class);
	
	private static final String PREFS_FILE_NAME = "prefs.ini";
	
	private LinkedList<IPreferenceChangedListener> listeners = new LinkedList<IPreferenceChangedListener>();

	private ConcurrentHashMap<String,PreferenceDictionary> prefs = new ConcurrentHashMap<String,PreferenceDictionary>();

	private PreferenceDictionary.PreferenceManagerContext prefCtx = new PreferenceDictionary.PreferenceManagerContext() {
		
		@Override
		public SharedPreferences getSharedPreferences(String pid) {
			return getPreferences(pid);
		}

		@Override
		public void notifyPreferenceChanged(String pid,
				PreferenceDictionary config) {
			fireConfigurationModifiedEvent(pid,config);
		}
	};
	
	private SharedPreferences getPreferences(String pid) {
		return this.context.getApplication().getAndroidApplication().getSharedPreferences(pid, 0);
	}
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#getPreferences()
	 */
	@Override
	public Set<String> getPreferences() {
		return prefs.keySet();
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#hasPreference(java.lang.String)
	 */
	@Override
	public boolean hasPreference(String pid) {
			return this.prefs.containsKey(pid);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#getPreference(java.lang.String)
	 */
	@Override
	public Dictionary<String, String> getPreference(String pid) {
		return this.prefs.get(pid);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#putPreference(java.lang.String, java.util.Dictionary)
	 */
	@Override
	public Dictionary<String, String> putPreference(String pid,
			Dictionary<String, String> config) {
		PreferenceDictionary d = this.prefs.get(pid);
		if(d == null){
			throw new IllegalArgumentException("Preference of :"+pid+" not found !");
		}
		SharedPreferences pref = d.getPreference();
		Set<String> keys = new HashSet<String>(pref.getAll().keySet());
		Editor editor = pref.edit();
		for(Enumeration<String> enu = config.keys();enu.hasMoreElements();){
			String key = enu.nextElement();
			String val = config.get(key);
			editor.putString(key, val);
			keys.remove(key);
		}
		if(!keys.isEmpty()){
			for (String key : keys) {
				editor.remove(key);
			}
		}
		editor.commit();
		return d;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#removePreference(java.lang.String)
	 */
	@Override
	public Dictionary<String, String> removePreference(String pid) {
		return this.prefs.remove(pid);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#addListener(com.wxxr.mobile.preference.api.IPreferenceChangedListener)
	 */
	@Override
	public void addListener(IPreferenceChangedListener listener) {
		synchronized(this.listeners){
			if(!this.listeners.contains(listener)){
				this.listeners.add(listener);
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.preference.api.IPreferenceManager#removeListener(com.wxxr.mobile.preference.api.IPreferenceChangedListener)
	 */
	@Override
	public void removeListener(IPreferenceChangedListener listener) {
		synchronized(this.listeners){
			this.listeners.remove(listener);
		}
	}
	
	protected void fireConfigurationModifiedEvent(String id, Dictionary<String, String> config) {
		IPreferenceChangedListener[] list = null;
		synchronized(this.listeners){
			list = this.listeners.toArray(new IPreferenceChangedListener[this.listeners.size()]);
		}
		if(list != null){
			for (IPreferenceChangedListener listener : list) {
				if(listener.getPIDs().contains(id)){
					listener.preferenceChanged(id, config);
				}
			}
		}
	}


	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#initServiceDependency()
	 */
	@Override
	protected void initServiceDependency() {
		
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#startService()
	 */
	@Override
	protected void startService() {
		loadFromFile();
		this.context.registerService(IPreferenceManager.class, this);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.microkernel.api.AbstractModule#stopService()
	 */
	@Override
	protected void stopService() {
		this.context.unregisterService(IPreferenceManager.class, this);
		if(this.prefs.size() > 0){
			String[] keys = this.prefs.keySet().toArray(new String[this.prefs.size()]);
			for (String key : keys) {
				PreferenceDictionary d = this.prefs.remove(key);
				d.destroy();
			}
		}
	}
	
	@Override
	public void newPreference(String pid, Dictionary<String, String> config) {
		if(this.prefs.containsKey(pid)){
			return;
		}
		PreferenceDictionary d = new PreferenceDictionary(pid, prefCtx);
		PreferenceDictionary old = this.prefs.putIfAbsent(pid, d);
		if(old == null){
			save2File();
		}
		if((config != null)&&(config.size() > 0)){
			putPreference(pid, config);
		}
	}
	
	private void save2File(){
		try {
			FileOutputStream fos = this.context.getApplication().getAndroidApplication().openFileOutput(PREFS_FILE_NAME, Context.MODE_PRIVATE);
			fos.write(StringUtils.join(this.prefs.keySet().iterator(),',').getBytes("UTF-8"));
			fos.close();
		}catch(IOException e){
			log.error("Failed to save preference pids to file", e);
		}
	}
	
	private void loadFromFile() {
		try {
			FileInputStream fis = this.context.getApplication().getAndroidApplication().openFileInput(PREFS_FILE_NAME);
			ByteArrayOutputStream aos = new ByteArrayOutputStream();
			byte[] data = new byte[512];
			int len = 0;
			while((len = fis.read(data)) != -1){
				aos.write(data, 0, len);
			}
			data = aos.toByteArray();
			String s = new String(data,"UTF-8");
			String[] keys = StringUtils.split(s, ',');
			for (String key : keys) {
				PreferenceDictionary d = new PreferenceDictionary(key, prefCtx);
				this.prefs.put(key, d);
			}
		} catch (FileNotFoundException e) {
		} catch(IOException e){
			log.error("Failed to load preference pids from file", e);
		}
	}
	
	@Override
	public String getPreference(String pid, String name) {
		PreferenceDictionary d = this.prefs.get(pid);
		if(d == null){
			return null;
		}
		SharedPreferences pref = d.getPreference();
		return getPreferenceValue(name, pref);
	}
	/**
	 * 
	 * for backward compatible
	 * @param name
	 * @param pref
	 * @return
	 */
	protected String getPreferenceValue(String name, SharedPreferences pref) {
		String val = null;
		try {
			val = pref.getString(name, null);
			return val;
		}catch(ClassCastException e){
		}
		
		try {
			int ival = pref.getInt(name, 0);
			val = String.valueOf(ival);
			pref.edit().putString(name, val).commit();
			return val;
		}catch(ClassCastException e){
		}
		
		try {
			long ival = pref.getLong(name, 0);
			val = String.valueOf(ival);
			pref.edit().putString(name, val).commit();
			return val;
		}catch(ClassCastException e){
			
		}
		
		try {
			boolean ival = pref.getBoolean(name, false);
			val = String.valueOf(ival);
			pref.edit().putString(name, val).commit();
			return val;
		}catch(ClassCastException e){
		}
		
		return null;
	}
	
	@Override
	public void updatePreference(String pid, String name, String value) {
		PreferenceDictionary d = this.prefs.get(pid);
		if(d == null){
			throw new IllegalArgumentException("Preference of :"+pid+" not found !");
		}
		SharedPreferences pref = d.getPreference();
		pref.edit().putString(name, value).commit();
	}
	
	@Override
	public String removePreference(String pid, String name) {
		PreferenceDictionary d = this.prefs.get(pid);
		if(d == null){
			return null;
		}
		SharedPreferences pref = d.getPreference();
		String val = getPreferenceValue(name, pref);
		if(val != null){
			pref.edit().remove(name).commit();
		}
		return val;
	}
	
	
	@Override
	public boolean hasPreference(String pid, String name) {
		PreferenceDictionary d = this.prefs.get(pid);
		if(d == null){
			return false;
		}
		SharedPreferences pref = d.getPreference();
		return getPreferenceValue(name, pref) != null;
	}

}
