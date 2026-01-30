package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.kalafche.service.CurrencyService;

@Service
public class CurrencyServiceImpl implements CurrencyService {

	public static final BigDecimal EUR_TO_BGN_RATE = new BigDecimal("1.95583");

	private static final int CALCULATION_SCALE = 6;

	public BigDecimal convertToEuro(BigDecimal amountBgn) {
		if (amountBgn != null) {
			BigDecimal eur = amountBgn.divide(EUR_TO_BGN_RATE, CALCULATION_SCALE, RoundingMode.HALF_UP);
			return eur.setScale(2, RoundingMode.HALF_UP);
		}

		return null;
	}

	public BigDecimal convertToBgn(BigDecimal amountEur) {
		if (amountEur != null) {
			BigDecimal bgn = amountEur.multiply(EUR_TO_BGN_RATE);
			return bgn.setScale(2, RoundingMode.HALF_UP);
		}

		return null;
	}

}
