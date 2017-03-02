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

import com.kalafche.BaseModel;
import com.kalafche.model.Employee;
import com.kalafche.model.KalafcheStore;

@Entity
@Table(name = "stock_relocation")
public class StockRelocation extends BaseModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "quantity", nullable = false)
	private int quantity;

	@Column(name = "price", nullable = false)
	private float price;

	@Column(name = "approved", nullable = true)
	private boolean approved;

	@Column(name = "rejected", nullable = true)
	private boolean rejected;

	@Column(name = "arrived", nullable = true)
	private boolean arrived;

	@Column(name = "archived", nullable = true)
	private boolean archived;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "from_kalafche_store_id", nullable = false)
	private KalafcheStore fromkalafcheStore;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "to_kalafche_store_id", nullable = false)
	private KalafcheStore tokalafcheStore;

	@Column(name = "relocation_request_timestamp", nullable = false)
	private long relocationRequestTimestamp;

	@Column(name = "relocation_complete_timestamp", nullable = true)
	private long relocationCompleteTimestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = false)
	private Employee employee;

	public int getId() {
		return this.id;
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

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public boolean isApproved() {
		return approved;
	}

	public void setApproved(boolean approved) {
		this.approved = approved;
	}

	public boolean isRejected() {
		return rejected;
	}

	public void setRejected(boolean rejected) {
		this.rejected = rejected;
	}

	public boolean isArrived() {
		return arrived;
	}

	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public KalafcheStore getFromkalafcheStore() {
		return fromkalafcheStore;
	}

	public void setFromkalafcheStore(KalafcheStore fromkalafcheStore) {
		this.fromkalafcheStore = fromkalafcheStore;
	}

	public KalafcheStore getTokalafcheStore() {
		return tokalafcheStore;
	}

	public void setTokalafcheStore(KalafcheStore tokalafcheStore) {
		this.tokalafcheStore = tokalafcheStore;
	}

	public long getRelocationRequestTimestamp() {
		return relocationRequestTimestamp;
	}

	public void setRelocationRequestTimestamp(long relocationRequestTimestamp) {
		this.relocationRequestTimestamp = relocationRequestTimestamp;
	}

	public long getRelocationCompleteTimestamp() {
		return relocationCompleteTimestamp;
	}

	public void setRelocationCompleteTimestamp(long relocationCompleteTimestamp) {
		this.relocationCompleteTimestamp = relocationCompleteTimestamp;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

}
