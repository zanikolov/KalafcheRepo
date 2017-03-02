package com.kalafche.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.web.multipart.MultipartFile;

import com.kalafche.BaseModel;

@Entity
@Table(name="item")
public class Item extends BaseModel {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "name", nullable = false)	
	private String name;
	
	@Column(name = "online_name", nullable = false)
	private String onlineName;
	
	@Column(name = "product_code", nullable = false)
	private String productCode;
	
	@Column(name = "description", nullable = false)
	private String description;
	
	@Column(name = "price", nullable = false)
	private float price;
	
	@Column(name = "purchase_price", nullable = false)
	private float purchasePrice;
	
	
	private MultipartFile pic;

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public int getId() {
		return this.id;
	}

	public int setId(int id) {
		return this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getPurchasePrice() {
		if (userHasRole("ROLE_SUPERADMIN")) {
			return purchasePrice;
		} else {
			return 0;
		}
	}

	public void setPurchasePrice(float purchasePrice) {
		if (userHasRole("ROLE_SUPERADMIN")) {
			this.purchasePrice = purchasePrice;
		}
	}

	public MultipartFile getPic() {
		return pic;
	}

	public void setPic(MultipartFile pic) {
		this.pic = pic;
	}

	public String getOnlineName() {
		return onlineName;
	}

	public void setOnlineName(String onlineName) {
		this.onlineName = onlineName;
	}
}
