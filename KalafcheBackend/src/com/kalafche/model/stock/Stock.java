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
import com.kalafche.model.KalafcheStore;
import com.kalafche.model.User;
import com.kalafche.model.device.DeviceBrand;

@Entity
@Table(name = "stock")
public class Stock {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "approved", nullable = true)
	private boolean approved;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_model_id", nullable = false)
	private DeviceBrand deviceModel;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id", nullable = false)
	private Item item;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "kalafche_store_id", nullable = false)
	private KalafcheStore kalafcheStoreId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "approver", nullable = true)
	private User approver;

	// private int orderedQuantity;

	public int getId() {
		return id;
	}

	public int setId(int id) {
		return this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public DeviceBrand getDeviceModel() {
		return deviceModel;
	}

	public void setDeviceModel(DeviceBrand deviceModel) {
		this.deviceModel = deviceModel;
	}

	public Item getItem() {
		return item;
	}

	public void setItemId(Item item) {
		this.item = item;
	}

	public KalafcheStore getKalafcheStoreId() {
		return kalafcheStoreId;
	}

	public void setKalafcheStoreId(KalafcheStore kalafcheStoreId) {
		this.kalafcheStoreId = kalafcheStoreId;
	}

	public User getApprover() {
		return approver;
	}

	public void setApprover(User approver) {
		this.approver = approver;
	}

}
