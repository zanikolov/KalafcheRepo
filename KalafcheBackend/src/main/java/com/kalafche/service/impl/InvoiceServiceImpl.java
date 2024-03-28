package com.kalafche.service.impl;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kalafche.dao.InvoiceDao;
import com.kalafche.model.Company;
import com.kalafche.model.PdfDocumentResponse;
import com.kalafche.model.invoice.Invoice;
import com.kalafche.model.invoice.InvoiceItem;
import com.kalafche.model.invoice.InvoiceType;
import com.kalafche.service.CompanyService;
import com.kalafche.service.DateService;
import com.kalafche.service.EmployeeService;
import com.kalafche.service.InvoiceService;
import com.kalafche.service.RelocationService;
import com.kalafche.service.TaxService;
import com.kalafche.service.fileutil.InvoicePDFGeneratorService;

@Service
public class InvoiceServiceImpl implements InvoiceService {

	@Autowired
	InvoiceDao invoiceDao;
	
	@Autowired
	CompanyService companyService;
	
	@Autowired
	RelocationService relocationService;
	
	@Autowired
	DateService dateService;
	
	@Autowired
	TaxService taxService;
	
	@Autowired
	EmployeeService employeeService;
	
	@Autowired
	InvoicePDFGeneratorService invoicePDFGeneratorService;
	
	@Override
	public PdfDocumentResponse generateInternalInvoice(Integer companyId, Long startDateMilliseconds, Long endDateMilliseconds) throws SQLException {
		Company issuer = companyService.getCompanyByCode("CASEPARTNER");
		Company recipient = companyService.getCompanyById(companyId);
		
		return generateInvoice(issuer, recipient, startDateMilliseconds, endDateMilliseconds);
	}

	private PdfDocumentResponse generateInvoice(Company issuer, Company recipient, Long startDateMilliseconds,
			Long endDateMilliseconds) throws SQLException {
		Invoice invoice = invoiceDao.getInvoice(startDateMilliseconds, endDateMilliseconds, issuer.getId(),
				recipient.getId());
		if (invoice == null) {
			invoice = new Invoice();
			Long now = dateService.getCurrentMillisBGTimezone();
			invoice.setIssueDate(now);
			invoice.setCreateTimestamp(now);
			invoice.setCreatedByEmployeeId(1);
			InvoiceType invoiceType = invoiceDao.getInvoiceTypeByCode("ORIGINAL");
			invoice.setTypeId(invoiceType.getId());
			invoice.setTypeName(invoiceType.getName());
			invoice.setStartDateTimestamp(startDateMilliseconds);
			invoice.setEndDateTimestamp(endDateMilliseconds);
		} else {
			invoice.setNumber(String.format("%010d", invoice.getId()));
		}
		invoice.setIssuer(issuer);
		invoice.setRecipient(recipient);
		invoice.setInvoiceItems(relocationService.getInvoiceItems(recipient.getId(), issuer.getId(),
				startDateMilliseconds, endDateMilliseconds));
		calculateBaseAndTax(invoice);
		if (invoice.getId() == null) {
			Integer invoiceId = invoiceDao.insertInvoice(invoice);
			invoice.setId(invoiceId);
			invoice.setNumber(String.format("%010d", invoiceId));
		}

		PdfDocumentResponse response = new PdfDocumentResponse();
		response.setDocumentName(String.format("invoice-%s-%s-%s-%s.pdf", invoice.getNumber(), recipient.getCode(),
				dateService.convertMillisToDateTimeString(startDateMilliseconds, "ddMMyyyy", false),
				dateService.convertMillisToDateTimeString(endDateMilliseconds, "ddMMyyyy", false)));
		response.setPdfBytes(invoicePDFGeneratorService.generateInvoiceDocument(invoice));

		return response;
	}

	private void calculateBaseAndTax(Invoice invoice) {
		BigDecimal totalAmount = calculateInvoiceTotalAmount(invoice);
		BigDecimal base = taxService.calculateBase(totalAmount);
		invoice.setBase(base);
		BigDecimal dueVat = taxService.calculateDueVAT(base);
		invoice.setDueVAT(dueVat);
		invoice.setTotalAmount(base.add(dueVat));
		invoice.setVatRate(new BigDecimal(20));
	}

	private BigDecimal calculateInvoiceTotalAmount(Invoice invoice) {
		BigDecimal totalAmount = BigDecimal.ZERO;
		if (invoice.getInvoiceItems() != null && invoice.getInvoiceItems().size() > 0) {
			totalAmount = invoice
					.getInvoiceItems().stream().filter(item -> Optional.ofNullable(item).map(InvoiceItem::getAmount)
							.map(amount -> amount != null).orElse(false))
					.map(item -> item.getAmount()).reduce(BigDecimal.ZERO, BigDecimal::add);
		}
		return totalAmount;
	}

}
