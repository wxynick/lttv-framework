/**
 * 
 */
package com.letv.mobile.core.microkernel.api;

/**
 * @author  
 *
 */
public class ModuleStatus {
	private MStatus status;
	private String moduleName;
	private Long createdTime;
	private Long startTime;
	private Long startTimeSpent;
	private Long stopTime;
	private Throwable recentError;
	private Long recentErrorTime;
	private String[] pendingServices;
	private Long pendingTimeSpent;
	/**
	 * @return the status
	 */
	public MStatus getStatus() {
		return status;
	}
	/**
	 * @return the moduleName
	 */
	public String getModuleName() {
		return moduleName;
	}
	/**
	 * @return the createdTime
	 */
	public Long getCreatedTime() {
		return createdTime;
	}
	/**
	 * @return the startTime
	 */
	public Long getStartTime() {
		return startTime;
	}
	/**
	 * @return the stopTime
	 */
	public Long getStopTime() {
		return stopTime;
	}
	/**
	 * @return the recentError
	 */
	public Throwable getRecentError() {
		return recentError;
	}
	/**
	 * @return the recentErrorTime
	 */
	public Long getRecentErrorTime() {
		return recentErrorTime;
	}
	/**
	 * @return the pendingServices
	 */
	public String[] getPendingServices() {
		return pendingServices;
	}
	/**
	 * @param status the status to set
	 */
	void setStatus(MStatus status) {
		this.status = status;
	}
	/**
	 * @param moduleName the moduleName to set
	 */
	void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	/**
	 * @param createdTime the createdTime to set
	 */
	void setCreatedTime(Long createdTime) {
		this.createdTime = createdTime;
	}
	/**
	 * @param startTime the startTime to set
	 */
	void setStartTime(Long startTime) {
		this.startTime = startTime;
	}
	/**
	 * @param stopTime the stopTime to set
	 */
	void setStopTime(Long stopTime) {
		this.stopTime = stopTime;
	}
	/**
	 * @param recentError the recentError to set
	 */
	void setRecentError(Throwable recentError) {
		this.recentError = recentError;
	}
	/**
	 * @param recentErrorTime the recentErrorTime to set
	 */
	void setRecentErrorTime(Long recentErrorTime) {
		this.recentErrorTime = recentErrorTime;
	}
	/**
	 * @param pendingServices the pendingServices to set
	 */
	void setPendingServices(String[] pendingServices) {
		this.pendingServices = pendingServices;
	}
	/**
	 * @return the startTimeSpent
	 */
	public Long getStartTimeSpent() {
		return startTimeSpent;
	}
	/**
	 * @param startTimeSpent the startTimeSpent to set
	 */
	void setStartTimeSpent(Long startTimeSpent) {
		this.startTimeSpent = startTimeSpent;
	}
	/**
	 * @return the pendingTimeSpent
	 */
	public Long getPendingTimeSpent() {
		return pendingTimeSpent;
	}
	/**
	 * @param pendingTimeSpent the pendingTimeSpent to set
	 */
	void setPendingTimeSpent(Long pendingTimeSpent) {
		this.pendingTimeSpent = pendingTimeSpent;
	}
	
}
