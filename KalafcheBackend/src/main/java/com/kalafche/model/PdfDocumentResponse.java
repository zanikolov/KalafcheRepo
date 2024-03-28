package com.kalafche.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfDocumentResponse {

	private byte[] pdfBytes;
	private String documentName;
}
