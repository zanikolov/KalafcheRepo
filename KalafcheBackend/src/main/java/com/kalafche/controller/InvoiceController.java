package com.kalafche.controller;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kalafche.model.PdfDocumentResponse;
import com.kalafche.service.InvoiceService;

@CrossOrigin
@RestController
@RequestMapping({ "/invoice" })
public class InvoiceController {

	@Autowired
	private InvoiceService invoiceService;

	@GetMapping
	public ResponseEntity<byte[]> printStockStickersByStoreId(@RequestParam(value = "companyId") Integer companyId,
			@RequestParam(value = "startDateMilliseconds") Long startDateMilliseconds,
			@RequestParam(value = "endDateMilliseconds") Long endDateMilliseconds) throws SQLException {
		PdfDocumentResponse documentResponse = invoiceService.generateInternalInvoice(companyId, startDateMilliseconds, endDateMilliseconds);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/pdf"));
		String filename = documentResponse.getDocumentName();
		headers.setContentDispositionFormData(filename, filename);
		headers.setAccessControlExposeHeaders(List.of("Content-Disposition"));
		headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
		ResponseEntity<byte[]> response = new ResponseEntity<>(documentResponse.getPdfBytes(), headers, HttpStatus.OK);

		return response;
	}

}
