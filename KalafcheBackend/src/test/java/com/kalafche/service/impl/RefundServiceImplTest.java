package com.kalafche.service.impl;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.kalafche.dao.RefundDao;
import com.kalafche.dao.SaleDao;
import com.kalafche.exceptions.IllegalStateTransferException;
import com.kalafche.model.Refund;
import com.kalafche.model.employee.Employee;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.ExpenseService;
import com.kalafche.service.StockService;

@RunWith(MockitoJUnitRunner.class)
public class RefundServiceImplTest {

	@Mock
	private RefundDao refundDao;

	@Mock
	private EmployeeService employeeService;

	@Mock
	private DateService dateService;

	@Mock
	private StockService stockService;

	@Mock
	private SaleDao saleDao;

	@Mock
	private ExpenseService expenseService;

	@InjectMocks
	private RefundServiceImpl refundService;

	@Test
	public void testSubmitRefundUsesSaleItemStoreWhenAdminHasNoStore() {
		Refund refund = new Refund(33, "manual refund");
		Employee admin = new Employee();
		admin.setId(7);
		admin.setStoreId(null);

		when(employeeService.getLoggedInEmployee()).thenReturn(admin);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1763654400000L);
		when(saleDao.getSaleItemStoreId(33)).thenReturn(19);
		when(saleDao.getSaleItemPrice(33)).thenReturn(new BigDecimal("12.78"));

		refundService.submitRefund(refund);

		verify(stockService).updateTheQuantitiyOfRefundStock(33, 19);
		verify(saleDao).updateRefundedSaleItem(33);
		verify(expenseService).createExpense(eq("REFUND"), eq("Върнати пари на клиент"),
				eq(new BigDecimal("12.78")), eq(19), isNull());
	}

	@Test
	public void testSubmitProtectPlusCancellationRefundRegistersExpenseForSaleItemStore() {
		Refund refund = new Refund(33, "Protect+ certificate cancellation: 12345");
		Employee admin = new Employee();
		admin.setId(7);
		admin.setStoreId(null);

		when(employeeService.getLoggedInEmployee()).thenReturn(admin);
		when(dateService.getCurrentMillisBGTimezone()).thenReturn(1763654400000L);
		when(saleDao.getSaleItemStoreId(33)).thenReturn(19);
		when(saleDao.getSaleItemPrice(33)).thenReturn(new BigDecimal("12.78"));

		refundService.submitProtectPlusCancellationRefund(refund);

		verify(stockService).updateTheQuantitiyOfRefundStock(33, 19);
		verify(saleDao).updateRefundedSaleItem(33);
		verify(expenseService).createExpense(eq("REFUND"), eq("Върнати пари на клиент"),
				eq(new BigDecimal("12.78")), eq(19), isNull());
	}

	@Test
	public void testSubmitRefundRejectsProtectPlusCertificateProduct() {
		Refund refund = new Refund(33, "manual refund");
		Employee admin = new Employee();
		admin.setId(7);

		when(employeeService.getLoggedInEmployee()).thenReturn(admin);
		when(saleDao.getSaleItemProductCode(33)).thenReturn("0500");

		try {
			refundService.submitRefund(refund);
		} catch (IllegalStateTransferException exception) {
			return;
		}
		throw new AssertionError("Protect+ certificate refund should be rejected.");
	}
}
