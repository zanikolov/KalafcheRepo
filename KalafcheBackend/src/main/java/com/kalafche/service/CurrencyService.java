package com.kalafche.service;

import java.math.BigDecimal;

public interface CurrencyService {

	public abstract BigDecimal convertToEuro(BigDecimal totalSum);

	public abstract BigDecimal convertToBgn(BigDecimal change);
	
}
