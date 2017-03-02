package com.kalafche.model.stock;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.kalafche.model.User;

@Entity
@Table(name = "stock_order")
public class StockOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "create_timestamp", nullable = false)
	private long createTimestamp;

	@Column(name = "update_timestamp", nullable = true)
	private long updateTimestamp;

	@Column(name = "completed", nullable = true)
	private boolean completed;

	@Column(name = "arrived", nullable = true)
	private boolean arrived;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "updated_by", nullable = true)
	private User updatedBy;

	public StockOrder() {
	}

	public StockOrder(User createdBy, long createTimestamp, User updatedBy, long updateTimestamp, boolean completed,
			boolean arrived) {
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

	public boolean isCompleted() {
		return completed;
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public User getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(User updatedBy) {
		this.updatedBy = updatedBy;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}

}
