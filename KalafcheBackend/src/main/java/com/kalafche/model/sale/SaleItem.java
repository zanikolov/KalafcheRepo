package com.kalafche.model.sale;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItem {
	
	private Integer id;
	private Integer saleId;
	private Long saleTimestamp;
	private String saleDescription;
	private Integer itemId;
	private Integer productId;
	private String productCode;
	private String productName;
	private Integer productTypeId;
	private String productTypeName;
	private Integer productMasterTypeId;
	private String productMasterTypeName;
	private Integer deviceModelId;
	private String deviceModelName;
	private Integer soldForDeviceModelId;
	private String soldForDeviceModelName;
	private Boolean soldForDeviceModelUnknown;
	private Boolean soldForDeviceModelRequired;
	private Integer deviceBrandId;
	private String deviceBrandName;
	private BigDecimal itemPrice;
	private BigDecimal salePrice;
	private BigDecimal discountAmount;
	private BigDecimal discountPercent;
	private Integer storeId;
	private String storeName;
	private Integer employeeId;
	private String employeeName;
	private Boolean isRefunded;
	private String discountCampaignCode;
	private Integer discountCodeId;
	private Integer discountCode;
	private String discountType;
	private String discountValue;
	private Boolean protectPlusApplied;
	private Integer bonusPts;

}
