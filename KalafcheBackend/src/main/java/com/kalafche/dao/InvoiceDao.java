package com.kalafche.dao;

import java.sql.SQLException;

import com.kalafche.model.invoice.Invoice;
import com.kalafche.model.invoice.InvoiceType;

public interface InvoiceDao {

	public abstract Integer insertInvoice(Invoice invoice) throws SQLException;
	
	public abstract Invoice getInvoice(Long startDate, Long endDate, Integer issuerCompanyId, Integer recipientCompanyId);

	public abstract InvoiceType getInvoiceTypeByCode(String code);
}
