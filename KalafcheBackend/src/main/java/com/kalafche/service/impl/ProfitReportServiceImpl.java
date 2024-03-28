package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.model.DataReport;
import com.kalafche.model.ProfitReport;
import com.kalafche.model.StoreDto;
import com.kalafche.model.expense.ExpenseReport;
import com.kalafche.service.EntityService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.ProfitReportService;
import com.kalafche.service.SaleService;
import com.kalafche.service.TaxService;

@Service
public class ProfitReportServiceImpl implements ProfitReportService {

	@Autowired
	SaleService saleService;
	
	@Autowired
	ExpenseService expenseService;
	
	@Autowired
	EntityService storeService;
	
	@Autowired
	TaxService taxService;
	
	@Override
	public List<ProfitReport> generateProfitReport(Long startDateMilliseconds, Long endDateMilliseconds) {
		List<StoreDto> stores = storeService.getStores(false);

		List<ProfitReport> profitReportList = new ArrayList<ProfitReport>();
		for (StoreDto store : stores) {
			BigDecimal profit = BigDecimal.ZERO;
			ProfitReport profitReport = new ProfitReport();
			profitReport.setStoreId(store.getId());
			profitReport.setStoreName(store.getCity() + ", " + store.getName());
			DataReport incomeReport = saleService.getSaleItemTotalAndCountWithoutRefundByStoreId(startDateMilliseconds, endDateMilliseconds, store.getId());
			profitReport.setIncome(incomeReport.getTotalAmount());
			profit = incomeReport.getTotalAmount() != null ? incomeReport.getTotalAmount() : BigDecimal.ZERO;
			
			BigDecimal base = taxService.calculateBase(incomeReport.getTotalAmount());
			profitReport.setBase(base);
			
			ExpenseReport expenseReport = expenseService.getExpenseReport(startDateMilliseconds, endDateMilliseconds,
					store.getId().toString(), 0, List.of("COLLECTION", "REFUND"));
			profitReport.setExpense(expenseReport.getTotalAmount());
			
			BigDecimal dueVAT = taxService.calculateDueVAT(base);
			profitReport.setDueVAT(dueVAT);
			profit = profit.subtract(dueVAT);
			profit = profit.subtract(expenseReport.getTotalAmount());
			
			BigDecimal refundVAT = taxService.calculateRefundVAT(expenseReport.getExpenses());
			profitReport.setRefundVAT(refundVAT);
			profit = profit.add(refundVAT);
			
			BigDecimal flatTax = taxService.calculateFlatTax(profit);
			profitReport.setFlatTax(flatTax);
			profit = profit.subtract(flatTax);
			
			BigDecimal dividentTax = taxService.calculateDivident(profit);
			profitReport.setDividentTax(dividentTax);
			profit = profit.subtract(dividentTax);
			
			profitReport.setExpenses(expenseService.getExpensePriceGroupdByType(startDateMilliseconds, endDateMilliseconds, store.getId()));
			profitReport.setProfit(profit);
			profitReportList.add(profitReport);
		}
		
		return profitReportList;
	}

}
