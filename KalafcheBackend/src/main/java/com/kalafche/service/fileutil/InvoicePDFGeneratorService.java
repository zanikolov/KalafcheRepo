package com.kalafche.service.fileutil;

import com.kalafche.model.invoice.Invoice;

public interface InvoicePDFGeneratorService {

	public byte[] generateInvoiceDocument(Invoice invoice);
}
