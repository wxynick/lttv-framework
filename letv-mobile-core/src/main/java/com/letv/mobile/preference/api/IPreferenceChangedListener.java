/**
 * 
 */
package com.letv.mobile.preference.api;

import java.util.Dictionary;
import java.util.Set;

/**
 * A configuration listener for the {@link IPreferenceManager}.
 *
 * When a preference changed listener is first registered with the {@link IPreferenceManager}
 * its preferenceChanged method is invoked for every PID the listener registers with.
 *
 * A <code>null</code> dictionary indicates that there is currently no preference for the associated PID.
 * @author  
 *
 */
public interface IPreferenceChangedListener {
    /**
     * Called when the {@code IModuleConfigureService} receives an update for
     * a PID that the listener has registered with.
     */
    void preferenceChanged(String pid, Dictionary<String, String> props);

    /**
     * Return the set of PIDs that this listener is interested in.
     * A <code>null</code> return value denotes any PID.
     */
    Set<String> getPIDs();

}
