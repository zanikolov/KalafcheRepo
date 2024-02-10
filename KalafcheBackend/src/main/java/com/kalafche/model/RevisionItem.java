package com.kalafche.model;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class RevisionItem {

	private Integer id;
	private Integer revisionId;
	private Integer itemId;
	private Integer productId;
	private String productCode;
	private String productName;
	private String barcode;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer deviceBrandId;
	private BigDecimal productPrice;
	private Integer balance;
	private Integer expected;
	private Integer actual;
	private Boolean partOfTheCurrentRevision = true;
	private Boolean synced;
	private String shortageInfo;

	public RevisionItem() {
	}
	
	public RevisionItem(Integer revisionId, Item item, Integer expected, Integer actual) {
		this(revisionId, item.getId(), item.getProductId(), item.getProductCode(), item.getProductName(),
				item.getBarcode(), item.getDeviceModelId(), item.getDeviceModelName(), item.getDeviceBrandId(),
				item.getProductPrice(), 0, 0, false);
	}
	
	public RevisionItem(Integer revisionId, Integer itemId, Integer productId, String productCode, String productName,
			String barcode, Integer deviceModelId, String deviceModelName, Integer deviceBrandId,
			BigDecimal productPrice, Integer expected, Integer actual, Boolean synced) {
		this.revisionId = revisionId;
		this.itemId = itemId;
		this.productId = productId;
		this.productCode = productCode;
		this.productName = productName;
		this.barcode = barcode;
		this.deviceModelId = deviceModelId;
		this.deviceModelName = deviceModelName;
		this.deviceBrandId = deviceBrandId;
		this.productPrice = productPrice;
		this.expected = expected;
		this.actual = actual;
		this.synced = synced;
	}

}
