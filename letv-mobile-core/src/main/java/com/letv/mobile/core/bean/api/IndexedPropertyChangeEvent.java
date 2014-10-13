package com.letv.mobile.core.bean.api;
/**
 * A type of {@link PropertyChangeEvent} that indicates that an indexed property
 * has changed.
 */
public class IndexedPropertyChangeEvent extends PropertyChangeEvent {

    private static final long serialVersionUID = -320227448495806870L;

    private final int index;

    /**
     * Creates a new property changed event with an indication of the property
     * index.
     *
     * @param source
     *            the changed bean.
     * @param propertyName
     *            the changed property, or <code>null</code> to indicate an
     *            unspecified set of the properties has changed.
     * @param oldValue
     *            the previous value of the property, or <code>null</code> if
     *            the <code>propertyName</code> is <code>null</code> or the
     *            previous value is unknown.
     * @param newValue
     *            the new value of the property, or <code>null</code> if the
     *            <code>propertyName</code> is <code>null</code> or the new
     *            value is unknown..
     * @param index
     *            the index of the property.
     */
    public IndexedPropertyChangeEvent(Object source, String propertyName,
            Object oldValue, Object newValue, int index) {
        super(source, propertyName, oldValue, newValue);
        this.index = index;
    }

    /**
     * Returns the index of the property that was changed in this event.
     */
    public int getIndex() {
        return index;
    }
}
