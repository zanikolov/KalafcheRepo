package com.kalafche.model.sale;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProtectPlusKpiRow {

	private Integer storeId;
	private String storeCode;
	private String storeName;
	private Boolean isStore;
	private Integer month;
	private Integer year;
	private BigDecimal activeBase;
	private BigDecimal soldProtectPlusCount;
	private BigDecimal soldProtectorCount;
	private BigDecimal attachRate;
	private BigDecimal protectPlusTurnover;
	private BigDecimal protectPlusTurnoverForecast;
	private BigDecimal totalTurnover;
	private BigDecimal protectPlusShare;
	private BigDecimal revenuePer100ActiveBase;
	private BigDecimal revenuePer100ActiveBaseForecast;
	private BigDecimal utilityCount;
	private BigDecimal utilityRate;
	private BigDecimal utilityCount1;
	private BigDecimal utilityRate1;
	private BigDecimal utilityCount2;
	private BigDecimal retentionRate;
}
