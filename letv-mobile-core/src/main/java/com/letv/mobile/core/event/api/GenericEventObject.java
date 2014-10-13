/**
 * 
 */
package com.letv.mobile.core.event.api;

/**
 * @author  
 *
 */
public class GenericEventObject implements IEventObject {

	private Object source;
	private boolean needSync = true;
	private long timestamp = System.currentTimeMillis();
	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.event.api.IEventObject#getSource()
	 */
	@Override
	public Object getSource() {
		return this.source;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.event.api.IEventObject#needSyncProcessed()
	 */
	@Override
	public boolean needSyncProcessed() {
		return this.needSync;
	}

	/* (non-Javadoc)
	 * @see com.wxxr.mobile.core.event.api.IEventObject#getTimestamp()
	 */
	@Override
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param source the source to set
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	/**
	 * @param needSync the needSync to set
	 */
	public void setNeedSyncProcessed(boolean needSync) {
		this.needSync = needSync;
	}

	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	
}
