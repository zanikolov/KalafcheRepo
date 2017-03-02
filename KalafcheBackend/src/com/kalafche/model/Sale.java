package com.kalafche.model;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.kalafche.model.partner.Partner;
import com.kalafche.model.stock.Stock;

public class Sale {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "stock_id", nullable = false)
	private Stock stock;

	@Column(name = "sale_timestamp", nullable = false)
	private long saleTimestamp;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partner_id", nullable = true)
	private Partner partner;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "employee_id", nullable = true)
	private Employee employee;

	@Column(name = "cost", nullable = false)
	private float cost;

	@Column(name = "discounted_cost", nullable = true)
	private float discountedCost;

	@Column(name = "description", nullable = true)
	private float description;

	public int getId() {
		return this.id;
	}

	public int setId(int id) {
		return this.id = id;
	}

	public long getSaleTimestamp() {
		return this.saleTimestamp;
	}

	public void setSaleTimestamp(long saleTimestamp) {
		this.saleTimestamp = saleTimestamp;
	}

	public float getCost() {
		return cost;
	}

	public void setCost(float cost) {
		this.cost = cost;
	}

	public Stock getStock() {
		return stock;
	}

	public void setStock(Stock stock) {
		this.stock = stock;
	}

	public Partner getPartner() {
		return partner;
	}

	public void setPartner(Partner partner) {
		this.partner = partner;
	}

	public Employee getEmployee() {
		return employee;
	}

	public void setEmployee(Employee employee) {
		this.employee = employee;
	}

	public float getDiscountedCost() {
		return discountedCost;
	}

	public void setDiscountedCost(float discountedCost) {
		this.discountedCost = discountedCost;
	}

	public float getDescription() {
		return description;
	}

	public void setDescription(float description) {
		this.description = description;
	}

}
