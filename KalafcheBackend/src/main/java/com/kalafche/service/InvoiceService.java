package com.kalafche.service;

import java.sql.SQLException;

import com.kalafche.model.PdfDocumentResponse;

public interface InvoiceService {

	PdfDocumentResponse generateInternalInvoice(Integer companyId, Long startDateMilliseconds, Long endDateMilliseconds) throws SQLException;

}
