package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.TaxDao;
import com.kalafche.model.Tax;
import com.kalafche.model.expense.Expense;
import com.kalafche.service.TaxService;

@Service
public class TaxServiceImpl implements TaxService {

	@Autowired
	TaxDao taxDao;

	private static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

	@Override
	public List<Tax> getTaxes(Boolean isApplicableOnExpenses) {
		return taxDao.selectTaxes(isApplicableOnExpenses);
	}

	@Override
	public BigDecimal calculateBase(BigDecimal amount) {
		Tax vat20 = taxDao.selectTaxByCode("VAT_20");
		BigDecimal divisor = BigDecimal.ONE.add(new BigDecimal(vat20.getRate()).divide(ONE_HUNDRED));
		return amount != null ? amount.divide(divisor, 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}
    
	@Override
	public BigDecimal calculateDueVAT(BigDecimal amount) {
		Tax vat20 = taxDao.selectTaxByCode("VAT_20");
		return amount != null
				? amount.multiply(new BigDecimal(vat20.getRate())).divide(ONE_HUNDRED).setScale(2, RoundingMode.HALF_UP)
				: BigDecimal.ZERO;
	}

	@Override
	public BigDecimal calculateRefundVAT(List<Expense> expenses) {
		BigDecimal refundVAT = BigDecimal.ZERO;
		for (Expense expense : expenses) {
			if (expense.getTaxId() != null && expense.getInvoice() != null) {
				BigDecimal divisor = BigDecimal.ONE.add(new BigDecimal(expense.getTaxRate()).divide(ONE_HUNDRED));
				BigDecimal originalPrice = expense.getPrice().divide(divisor, 2, RoundingMode.HALF_UP);
				BigDecimal vatAmount = expense.getPrice().subtract(originalPrice);

				refundVAT = refundVAT.add(vatAmount.setScale(2, RoundingMode.HALF_UP));
			}
		}
		return refundVAT;
	}

	@Override
	public BigDecimal calculateFlatTax(BigDecimal amount) {
		Tax flatTax = taxDao.selectTaxByCode("FLAT");
		return amount != null ? amount.multiply(new BigDecimal(flatTax.getRate())).divide(ONE_HUNDRED).setScale(2,
				RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

	@Override
	public BigDecimal calculateDivident(BigDecimal amount) {
		Tax dividentTax = taxDao.selectTaxByCode("DIVIDENT");
		return amount != null ? amount.multiply(new BigDecimal(dividentTax.getRate())).divide(ONE_HUNDRED).setScale(2,
				RoundingMode.HALF_UP) : BigDecimal.ZERO;
	}

}
