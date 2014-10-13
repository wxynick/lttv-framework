package com.letv.mobile.core.bean.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import com.letv.mobile.core.util.ObjectUtils;

/**
 * This is a utility class that can be used by beans that support bound
 * properties.  You can use an instance of this class as a member field
 * of your bean and delegate various work to it.
 *
 * This class is serializable.  When it is serialized it will save
 * (and restore) any listeners that are themselves serializable.  Any
 * non-serializable listeners will be skipped during serialization.
 *
 */
public class PropertyChangeSupport {

	private static class DesignatedPropertyChangeListener implements PropertyChangeListener {
		private final String propertyName;
		private final PropertyChangeListener listener;

		public DesignatedPropertyChangeListener(String propertyName, PropertyChangeListener l){
			this.propertyName = propertyName;
			this.listener = l;
		}
		/* (non-Javadoc)
		 * @see com.wxxr.mobile.core.bean.api.PropertyChangeListener#propertyChange(com.wxxr.mobile.core.bean.api.PropertyChangeEvent)
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if(this.propertyName.equals(evt.getPropertyName())){
				this.listener.propertyChange(evt);
			}
		}
		/**
		 * @return the propertyName
		 */
		protected String getPropertyName() {
			return propertyName;
		}
		/**
		 * @return the listener
		 */
		protected PropertyChangeListener getListener() {
			return listener;
		}



	}

	private List<PropertyChangeListener> listeners;

	/** 
	 * Hashtable for managing listeners for specific properties.
	 * Maps property names to PropertyChangeSupport objects.
	 * @serial 
	 * @since 1.2
	 */
	private Hashtable<String,PropertyChangeSupport> children;

	/** 
	 * The object to be provided as the "source" for any generated events.
	 * @serial
	 */
	private Object source;


	/**
	 * Constructs a <code>PropertyChangeSupport</code> object.
	 *
	 * @param sourceBean  The bean to be given as the source for any events.
	 */

	public PropertyChangeSupport(Object sourceBean) {
		if (sourceBean == null) {
			throw new NullPointerException();
		}
		source = sourceBean;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.bean.api.IBindableBean#addPropertyChangeListener(com.wxxr.mobile.core.bean.api.PropertyChangeListener)
	 */
	public synchronized void addPropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}

		if (listener instanceof DesignatedPropertyChangeListener) {
			DesignatedPropertyChangeListener proxy =
					(DesignatedPropertyChangeListener)listener;
			// Call two argument add method.
			addPropertyChangeListener(proxy.getPropertyName(),
					proxy.getListener());
		} else {
			if (listeners == null) {
				listeners = new ArrayList<PropertyChangeListener>();
			}
			listeners.add(listener);
		}
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.bean.api.IBindableBean#removePropertyChangeListener(com.wxxr.mobile.core.bean.api.PropertyChangeListener)
	 */
	public synchronized void removePropertyChangeListener(
			PropertyChangeListener listener) {
		if (listener == null) {
			return;
		}

		if (listener instanceof DesignatedPropertyChangeListener) {
			DesignatedPropertyChangeListener proxy =
					(DesignatedPropertyChangeListener)listener;
			// Call two argument remove method.
			removePropertyChangeListener(proxy.getPropertyName(),
					proxy.getListener());
		} else {
			if (listeners == null) {
				return;
			}
			listeners.remove(listener);
		}
	}

	/**
	 * Returns an array of all the listeners that were added to the
	 * PropertyChangeSupport object with addPropertyChangeListener().
	 * <p>
	 * If some listeners have been added with a named property, then
	 * the returned array will be a mixture of PropertyChangeListeners
	 * and <code>PropertyChangeListenerProxy</code>s. If the calling
	 * method is interested in distinguishing the listeners then it must
	 * test each element to see if it's a
	 * <code>PropertyChangeListenerProxy</code>, perform the cast, and examine
	 * the parameter.
	 * 
	 * <pre>
	 * PropertyChangeListener[] listeners = bean.getPropertyChangeListeners();
	 * for (int i = 0; i < listeners.length; i++) {
	 *	 if (listeners[i] instanceof PropertyChangeListenerProxy) {
	 *     PropertyChangeListenerProxy proxy = 
	 *                    (PropertyChangeListenerProxy)listeners[i];
	 *     if (proxy.getPropertyName().equals("foo")) {
	 *       // proxy is a PropertyChangeListener which was associated
	 *       // with the property named "foo"
	 *     }
	 *   }
	 * }
	 *</pre>
	 *
	 * @see PropertyChangeListenerProxy
	 * @return all of the <code>PropertyChangeListeners</code> added or an 
	 *         empty array if no listeners have been added
	 * @since 1.4
	 */
	public synchronized PropertyChangeListener[] getPropertyChangeListeners() {
		List<PropertyChangeListener> returnList = new ArrayList<PropertyChangeListener>();

		// Add all the PropertyChangeListeners 
		if (listeners != null) {
			returnList.addAll(this.listeners);
		}

		// Add all the PropertyChangeListenerProxys
		if (children != null) {
			Iterator<String> iterator = children.keySet().iterator();
			while (iterator.hasNext()) {
				String key = (String)iterator.next();
				PropertyChangeSupport child =
						(PropertyChangeSupport)children.get(key);
				PropertyChangeListener[] childListeners =
						child.getPropertyChangeListeners();
				for (int index = childListeners.length - 1; index >= 0;
						index--) {
					returnList.add(new DesignatedPropertyChangeListener(key, childListeners[index]));
				}
			}
		}
		return (PropertyChangeListener[])(returnList.toArray(
				new PropertyChangeListener[0]));
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.bean.api.IBindableBean#addPropertyChangeListener(java.lang.String, com.wxxr.mobile.core.bean.api.PropertyChangeListener)
	 */

	public synchronized void addPropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		if (listener == null || propertyName == null) {
			return;
		}
		if (children == null) {
			children = new Hashtable<String, PropertyChangeSupport>();
		}
		PropertyChangeSupport child = (PropertyChangeSupport)children.get(propertyName);
		if (child == null) {
			child = new PropertyChangeSupport(source);
			children.put(propertyName, child);
		}
		child.addPropertyChangeListener(listener);
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.bean.api.IBindableBean#removePropertyChangeListener(java.lang.String, com.wxxr.mobile.core.bean.api.PropertyChangeListener)
	 */

	public synchronized void removePropertyChangeListener(
			String propertyName,
			PropertyChangeListener listener) {
		if (listener == null || propertyName == null) {
			return;
		}
		if (children == null) {
			return;
		}
		PropertyChangeSupport child = (PropertyChangeSupport)children.get(propertyName);
		if (child == null) {
			return;
		}
		child.removePropertyChangeListener(listener);
	}

	/**
	 * Returns an array of all the listeners which have been associated 
	 * with the named property.
	 *
	 * @param propertyName  The name of the property being listened to
	 * @return all of the <code>PropertyChangeListeners</code> associated with
	 *         the named property.  If no such listeners have been added,
	 *         or if <code>propertyName</code> is null, an empty array is
	 *         returned.
	 * @since 1.4
	 */
	public synchronized PropertyChangeListener[] getPropertyChangeListeners(
			String propertyName) {
		ArrayList<PropertyChangeListener> returnList = new ArrayList<PropertyChangeListener>();

		if (children != null && propertyName != null) {
			PropertyChangeSupport support =
					(PropertyChangeSupport)children.get(propertyName);
			if (support != null) {
				returnList.addAll(
						Arrays.asList(support.getPropertyChangeListeners()));
			}
		}
		return (PropertyChangeListener[])(returnList.toArray(
				new PropertyChangeListener[0]));
	}

	/**
	 * Report a bound property update to any registered listeners.
	 * No event is fired if old and new are equal and non-null.
	 *
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes {@code
	 * PropertyChangeEvent} value.
	 *
	 * @param propertyName  The programmatic name of the property
	 *		that was changed.
	 * @param oldValue  The old value of the property.
	 * @param newValue  The new value of the property.
	 */
	public void firePropertyChange(String propertyName, 
			Object oldValue, Object newValue) {
		if (ObjectUtils.isEquals(oldValue, newValue)) {
			return;
		}
		firePropertyChange(new PropertyChangeEvent(source,propertyName,oldValue,newValue));
	}


	/**
	 * Report a boolean bound property update to any registered listeners.
	 * No event is fired if old and new are equal.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes Object values.
	 *
	 * @param propertyName  The programmatic name of the property
	 *		that was changed.
	 * @param oldValue  The old value of the property.
	 * @param newValue  The new value of the property.
	 */
	public void firePropertyChange(String propertyName, 
			boolean oldValue, boolean newValue) {
		if (oldValue == newValue) {
			return;
		}
		firePropertyChange(propertyName, Boolean.valueOf(oldValue), Boolean.valueOf(newValue));
	}

	/**
	 * Fire an existing PropertyChangeEvent to any registered listeners.
	 * No event is fired if the given event's old and new values are
	 * equal and non-null.
	 * @param evt  The PropertyChangeEvent object.
	 */
	public void firePropertyChange(PropertyChangeEvent evt) {
		Object oldValue = evt.getOldValue();
		Object newValue = evt.getNewValue();
		String propertyName = evt.getPropertyName();
		if (oldValue != null && newValue != null && oldValue.equals(newValue)) {
			return;
		}

		if (listeners != null) {
			PropertyChangeListener[] list = listeners.toArray(new PropertyChangeListener[0]);
			for (int i = 0; i < list.length; i++) {
				PropertyChangeListener target = (PropertyChangeListener)list[i];
				target.propertyChange(evt);
			}
		}

		if (children != null && propertyName != null) {
			PropertyChangeSupport child = null;
			child = (PropertyChangeSupport)children.get(propertyName);
			if (child != null) {
				child.firePropertyChange(evt);
			}
		}
	}


	/**
	 * Report a bound indexed property update to any registered
	 * listeners. 
	 * <p>
	 * No event is fired if old and new values are equal
	 * and non-null.
	 *
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * firePropertyChange method that takes {@code PropertyChangeEvent} value.
	 *
	 * @param propertyName The programmatic name of the property that
	 *                     was changed.
	 * @param index        index of the property element that was changed.
	 * @param oldValue     The old value of the property.
	 * @param newValue     The new value of the property.
	 * @since 1.5
	 */
	public void fireIndexedPropertyChange(String propertyName, int index,
			Object oldValue, Object newValue) {
		firePropertyChange(new IndexedPropertyChangeEvent
				(source, propertyName, oldValue, newValue, index));
	}

	/**
	 * Report an <code>int</code> bound indexed property update to any registered 
	 * listeners. 
	 * <p>
	 * No event is fired if old and new values are equal.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * fireIndexedPropertyChange method which takes Object values.
	 *
	 * @param propertyName The programmatic name of the property that
	 *                     was changed.
	 * @param index        index of the property element that was changed.
	 * @param oldValue     The old value of the property.
	 * @param newValue     The new value of the property.
	 * @since 1.5
	 */
	public void fireIndexedPropertyChange(String propertyName, int index,
			int oldValue, int newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireIndexedPropertyChange(propertyName, index, 
				new Integer(oldValue), 
				new Integer(newValue));
	}

	/**
	 * Report a <code>boolean</code> bound indexed property update to any 
	 * registered listeners. 
	 * <p>
	 * No event is fired if old and new values are equal.
	 * <p>
	 * This is merely a convenience wrapper around the more general
	 * fireIndexedPropertyChange method which takes Object values.
	 *
	 * @param propertyName The programmatic name of the property that
	 *                     was changed.
	 * @param index        index of the property element that was changed.
	 * @param oldValue     The old value of the property.
	 * @param newValue     The new value of the property.
	 * @since 1.5
	 */
	public void fireIndexedPropertyChange(String propertyName, int index,
			boolean oldValue, boolean newValue) {
		if (oldValue == newValue) {
			return;
		}
		fireIndexedPropertyChange(propertyName, index, Boolean.valueOf(oldValue), 
				Boolean.valueOf(newValue));
	}

	/**
	 * Check if there are any listeners for a specific property, including
	 * those registered on all properties.  If <code>propertyName</code>
	 * is null, only check for listeners registered on all properties.
	 *
	 * @param propertyName  the property name.
	 * @return true if there are one or more listeners for the given property
	 */
	public synchronized boolean hasListeners(String propertyName) {
		if (listeners != null && !listeners.isEmpty()) {
			// there is a generic listener
			return true;
		}
		if (children != null && propertyName != null) {
			PropertyChangeSupport child = (PropertyChangeSupport)children.get(propertyName);
			if (child != null && child.listeners != null) {
				return !child.listeners.isEmpty();
			}
		}
		return false;
	}


}
