/**
 * 
 */
package com.letv.mobile.preference.api;

import java.util.Dictionary;
import java.util.Set;

/**
 * 
 *
 */
public interface IPreferenceManager {
	String SYSTEM_PREFERENCE_NAME = "SYSTEM";
	String SYSTEM_PREFERENCE_KEY_DEFAULT_SERVER_URL = "server";
    /**
     * Get the set of registered PIDs
     * @return The set of registered PIDs or an empty set.
     */
    Set<String> getPreferences();

    /**
     * True if therer is a configuration for the given PID.
     */
    boolean hasPreference(String pid);

    /**
     * Get the configuration dictionary for the given PID.
     * @return The configuration dictionary or <code>null</code>
     */
    Dictionary<String, String> getPreference(String pid);

    /**
     * Put or update the configuration for the given PID.
     * @return The previously registered configuration or <code>null</code>
     */
    Dictionary<String, String> putPreference(String pid, Dictionary<String, String> config);

    /**
     * create a new preference
     * @param pid
     * @param config
     */
    void newPreference(String pid, Dictionary<String, String> config);
    /**
     * Remove the configuration for the given PID.
     * @return The previously registered configuration or <code>null</code>
     */
    Dictionary<String, String> removePreference(String pid);

    /**
     * Add a configuration listener.
     */
    void addListener(IPreferenceChangedListener listener);

    /**
     * Remove a configuration listener.
     */
    void removeListener(IPreferenceChangedListener listener);
    
    /**
     * return the value of specific pid and preference name, return null if the preference doesn't exist.
     * @param pid
     * @param name
     * @return
     */
    String getPreference(String pid, String name);
    
    void updatePreference(String pid, String name, String value);
    
    String removePreference(String pid, String name);
    
    boolean hasPreference(String pid, String name);

}
