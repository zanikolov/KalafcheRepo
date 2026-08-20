package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kalafche.dao.RefundDao;
import com.kalafche.dao.SaleDao;
import com.kalafche.dao.StoreDao;
import com.kalafche.exceptions.DomainObjectNotFoundException;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.Refund;
import com.kalafche.model.employee.Employee;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.EntityService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.RefundService;
import com.kalafche.service.StockService;

@Service
public class RefundServiceImpl implements RefundService {

	private static final String PROTECT_PLUS_PRODUCT_CODE = "0500";

	@Autowired
	private RefundDao refundDao;
	
	@Autowired
	EntityService entityService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	StockService stockService;
	
	@Autowired
	SaleDao saleDao;
	
	@Autowired
	StoreDao storeDao;
	
	@Autowired
	ExpenseService expenseService;
	
	@Override
	public List<Refund> searchRefunds(Long startDateMilliseconds, Long endDateMilliseconds, String storeIds,
			String productCode, Integer deviceBrandId, Integer deviceModelId) {
		
		List<Refund> refunds = refundDao.searchRefunds(startDateMilliseconds,
				endDateMilliseconds, entityService.getConcatenatedStoreIdsForFiltering(storeIds), productCode, deviceBrandId, deviceModelId);
		
		return refunds;
	}

	@Transactional
	@Override
	public void submitRefund(Refund refund) {
		submitRefund(refund, true);
	}

	@Transactional
	@Override
	public void submitProtectPlusCancellationRefund(Refund refund) {
		submitRefund(refund, false);
	}

	private void submitRefund(Refund refund, boolean handleProtectPlusCertificate) {
		Employee loggedInEmployee = employeeService.getLoggedInEmployee();
		if (handleProtectPlusCertificate) {
			validateProtectPlusCertificateRefund(refund.getSaleItemId());
		}
		refund.setEmployeeId(loggedInEmployee.getId());
		refund.setTimestamp(dateService.getCurrentMillisBGTimezone());
		
		refundDao.insertRefund(refund);
		Integer refundStoreId = resolveRefundStoreId(loggedInEmployee, refund.getSaleItemId());
		stockService.updateTheQuantitiyOfRefundStock(refund.getSaleItemId(), refundStoreId);
		saleDao.updateRefundedSaleItem(refund.getSaleItemId());
		registerExpense(refund.getSaleItemId(), refundStoreId);
	}

	private void validateProtectPlusCertificateRefund(Integer saleItemId) {
		String productCode = saleDao.getSaleItemProductCode(saleItemId);
		if (PROTECT_PLUS_PRODUCT_CODE.equals(productCode)) {
			throw new IllegalStateTransferException("saleItemId",
					"Protect+ certificates can be refunded only from the Protect+ certificates screen.");
		}
	}

	private Integer resolveRefundStoreId(Employee loggedInEmployee, Integer saleItemId) {
		if (loggedInEmployee.getStoreId() != null) {
			return loggedInEmployee.getStoreId();
		}

		Integer saleItemStoreId = saleDao.getSaleItemStoreId(saleItemId);
		if (saleItemStoreId == null) {
			throw new DomainObjectNotFoundException("saleItemId", "Non-existing sale item.");
		}

		return saleItemStoreId;
	}

	private void registerExpense(Integer saleItemId, Integer storeId) {
		BigDecimal saleItemPrice = saleDao.getSaleItemPrice(saleItemId);
		expenseService.createExpense("REFUND", "Върнати пари на клиент", saleItemPrice, storeId, null);
	}

}
