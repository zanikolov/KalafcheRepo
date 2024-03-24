package com.kalafche.model.revision;

import java.math.BigDecimal;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RevisionReport {

	List<Revision> revisions;
	private Long startDate;
	private Long endDate;
	private BigDecimal totalAbsoluteAmountBalance;
	private Integer totalAbsoluteCountBalance;
	private Integer totalShortageCount;
	private BigDecimal totalShortageAmount;
	private Integer totalSurplusCount;
	private BigDecimal totalSurplusAmount;
	private BigDecimal totalAmount;
	
}
