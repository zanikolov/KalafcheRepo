package com.kalafche.model;

public class StockOrder {

	private int id;
	private long createTimestamp;
	private long updateTimestamp;
	private int createdBy;
	private int updatedBy;
	private boolean completed;
	private boolean arrived;

	public StockOrder() {}
	
	public StockOrder(int createdBy, long createTimestamp, int updatedBy, long updateTimestamp, boolean completed, boolean arrived) {
		this.createdBy = createdBy;
		this.createTimestamp = createTimestamp;
		this.updatedBy = updatedBy;
		this.updateTimestamp = updateTimestamp;
		this.completed = completed;
		this.arrived = arrived;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public long getUpdateTimestamp() {
		return updateTimestamp;
	}

	public void setUpdateTimestamp(long updateTimestamp) {
		this.updateTimestamp = updateTimestamp;
	}

	public int getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(int createdBy) {
		this.createdBy = createdBy;
	}

	public int getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(int updatedBy) {
		this.updatedBy = updatedBy;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}

}
