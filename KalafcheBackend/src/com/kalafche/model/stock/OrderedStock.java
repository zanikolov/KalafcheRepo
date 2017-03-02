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

import com.kalafche.model.Item;
import com.kalafche.model.User;
import com.kalafche.model.device.DeviceBrand;

@Entity
@Table(name = "ordered_stock")
public class OrderedStock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "create_timestamp", nullable = false)
	private long createTimestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_model_id", nullable = false)
	private DeviceBrand deviceModel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stock_order_id", nullable = false)
	private DeviceBrand stockOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", nullable = false)
	private User createdBy;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public DeviceBrand getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceBrand deviceModel) {
		this.deviceModel = deviceModel;
	}

	public DeviceBrand getStockOrder() {
		return stockOrder;
	}

	public void setStockOrder(DeviceBrand stockOrder) {
		this.stockOrder = stockOrder;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public long getCreateTimestamp() {
		return createTimestamp;
	}

	public void setCreateTimestamp(long createTimestamp) {
		this.createTimestamp = createTimestamp;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
