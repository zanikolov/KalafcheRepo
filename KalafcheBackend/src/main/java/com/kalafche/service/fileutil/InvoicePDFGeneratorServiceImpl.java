package com.kalafche.service.fileutil;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.kalafche.model.Company;
import com.kalafche.model.invoice.Invoice;
import com.kalafche.model.invoice.InvoiceItem;
import com.kalafche.service.DateService;

@Service
public class InvoicePDFGeneratorServiceImpl implements InvoicePDFGeneratorService {

	public static final String FREE_SANS_FONT = "fonts/FreeSans.ttf";
	public static final String ARIAL_FONT = "fonts/arial.ttf";
	public static final String VERDANA_FONT = "fonts/verdana.ttf";
	public static final String VERDANA_BOLD_FONT = "fonts/verdana-bold.ttf";
	public static final Font BOLD_8 = FontFactory.getFont(VERDANA_FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 8, Font.BOLD);
	public static final Font BOLD_10 = FontFactory.getFont(VERDANA_FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 10, Font.BOLD);
	public static final Font BOLD_20 = FontFactory.getFont(VERDANA_FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 20, Font.BOLD);
	public static final Font NORMAL_8 = FontFactory.getFont(VERDANA_FONT, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED, 8);
	public static final GrayColor BG_LIGHT_GRAY = new GrayColor(0.95f);
	public static final GrayColor BG_GRAY = new GrayColor(0.9f);
	public static final GrayColor BORDER_GRAY = new GrayColor(0.8f);
	public static final GrayColor WHITE = new GrayColor(1.0f);
	
	@Autowired
	DateService dateService;
	
	@Override
	public byte[] generateInvoiceDocument(Invoice invoice) {
		Rectangle rect = new Rectangle(PageSize.A4);		
		
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] pdfBytes = null;
		
		Document document = new Document(rect, 50, 50, 20, 20);

	    try
	    {
	    	PdfWriter writer = PdfWriter.getInstance(document, byteArrayOutputStream);
	        document.open();
	 
	        PdfPTable detailsTable = createDetailsTable(invoice);
	        document.add(detailsTable);
	        
	        PdfPTable itemsTable = createInvoiceItemsTable(invoice.getInvoiceItems());
	        itemsTable.setSpacingBefore(8f);
	        document.add(itemsTable);
	        
	        PdfPTable paymentDetailsTable = createPaymentDetailsTable(invoice);
	        paymentDetailsTable.setSpacingBefore(8f);
	        document.add(paymentDetailsTable);
	        
	        PdfPTable paymentDetailsInWordsTable = createPaymentDetailsInWordsTable(invoice);
	        paymentDetailsInWordsTable.setSpacingBefore(8f);
	        document.add(paymentDetailsInWordsTable);
	        
	        PdfPTable signatureTable = createSignatureTable(invoice);
	        signatureTable.setSpacingBefore(8f);
	        document.add(signatureTable);
	 
	        document.close();
	        writer.close();
	        pdfBytes = byteArrayOutputStream.toByteArray();
	    } 
	    catch (Exception e) {
	        e.printStackTrace();
	    }
	    
	    return pdfBytes;	
	}

	private PdfPTable createSignatureTable(Invoice invoice) throws DocumentException {
		PdfPTable signatureTable = configureTable(2);
		signatureTable.addCell(configureSignatureCell("Получател: ", invoice.getRecipient().getAccountablePerson()));
		signatureTable.addCell(configureSignatureCell("Съставил: ", invoice.getIssuer().getAccountablePerson()));

		return signatureTable;
	}

	private PdfPCell configureSignatureCell(String label, String accountablePerson) {
		Phrase phrase = new Phrase();
		phrase.add(new Chunk(label, NORMAL_8));
		phrase.add(new Chunk(accountablePerson, BOLD_8));
		Paragraph paragraph = new Paragraph(phrase);
		
		PdfPCell cell = new PdfPCell(paragraph);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		cell.setBorder(Rectangle.NO_BORDER);
		
		return cell;
	}
	
	private PdfPTable createPaymentDetailsInWordsTable(Invoice invoice) throws DocumentException {
		PdfPTable paymentDetailsInWordsTable = configureTable(1);
		paymentDetailsInWordsTable.addCell(configurePaymentDetailsInWordsCell("Словом: ", numberToWords(invoice.getTotalAmount())));
		paymentDetailsInWordsTable.addCell(configurePaymentDetailsInWordsCell("Метод на плащане: ", "по банков път"));
		
		return paymentDetailsInWordsTable;
	}

	private PdfPCell configurePaymentDetailsInWordsCell(String label, String value) {
		Phrase phrase = new Phrase();
		phrase.add(new Chunk(label, NORMAL_8));
		phrase.add(new Chunk(value, BOLD_8));
		Paragraph paragraph = new Paragraph(phrase);
		
		PdfPCell cell = new PdfPCell(paragraph);
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setPadding(5);
		cell.setBorderColor(WHITE);
		cell.setBackgroundColor(BG_GRAY);

		return cell;
	}

	private PdfPTable createPaymentDetailsTable(Invoice invoice) throws DocumentException {
		PdfPTable paymentDetailsTable = configureTable(2);
		
		PdfPCell bankDetailsCell = new PdfPCell();
		bankDetailsCell.setBorder(Rectangle.NO_BORDER);
		bankDetailsCell.addElement(createBankDetailsTable(invoice.getIssuer()));		
		paymentDetailsTable.addCell(bankDetailsCell);
		
		PdfPCell totalAmountCell = new PdfPCell();
		totalAmountCell.setBorder(Rectangle.NO_BORDER);
		totalAmountCell.addElement(createTotalAmountTable(invoice));		
		paymentDetailsTable.addCell(totalAmountCell);
		
		return paymentDetailsTable;
	}

	private Element createTotalAmountTable(Invoice invoice) throws DocumentException {
		PdfPTable paymentDetailsTable = configureTable(2);
		
		paymentDetailsTable.addCell(configureCell("Данъчна основа:", NORMAL_8, null, null, false, null, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell(NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(invoice.getBase()), BOLD_8, null, BG_LIGHT_GRAY, true, WHITE, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell("Процент ДДС:", NORMAL_8, null, null, false, null, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell(invoice.getVatRate() + "%", BOLD_8, null, BG_LIGHT_GRAY, true, WHITE, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell("Начислен ДДС:", NORMAL_8, null, null, false, null, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell(NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(invoice.getDueVAT()), BOLD_8, null, BG_LIGHT_GRAY, true, WHITE, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell("Сума за плащане:", NORMAL_8, null, null, false, null, true, Element.ALIGN_RIGHT, 5));
		paymentDetailsTable.addCell(configureCell(NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(invoice.getTotalAmount()), BOLD_10, null, BG_GRAY, true, WHITE, true, Element.ALIGN_RIGHT, 5));
		
		return paymentDetailsTable;
	}

	private PdfPTable createBankDetailsTable(Company issuer) throws DocumentException {
		PdfPTable bankDetailsTable = configureTable(2);
		bankDetailsTable.setHeaderRows(1);
		bankDetailsTable.setWidths(new int[] {1, 3});
		
		bankDetailsTable.addCell(configureCell("Банкови детайли на доставчика", NORMAL_8, 2, BG_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		bankDetailsTable.addCell(configureCell("Банка:", NORMAL_8, null, BG_LIGHT_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		bankDetailsTable.addCell(configureCell(issuer.getBank(), NORMAL_8, null, null, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		bankDetailsTable.addCell(configureCell("IBAN:", NORMAL_8, null, BG_LIGHT_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		bankDetailsTable.addCell(configureCell(issuer.getIban(), NORMAL_8, null, null, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		bankDetailsTable.addCell(configureCell("BIC/SWIFT:", NORMAL_8, null, BG_LIGHT_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		bankDetailsTable.addCell(configureCell(issuer.getBic(), NORMAL_8, null, null, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 5));
		
		return bankDetailsTable;
	}

	private PdfPTable createInvoiceItemsTable(List<InvoiceItem> invoiceItems) throws DocumentException {
		PdfPTable invoiceItemsTable = configureTable(5);
		invoiceItemsTable.setHeaderRows(1);
		invoiceItemsTable.setWidths(new int[] {1, 18, 4, 3, 4});
		createInvoceItemsTableHeader(invoiceItemsTable);
		createInvoceItemsTableContent(invoiceItems, invoiceItemsTable);
		
		return invoiceItemsTable;
	}

	private void createInvoceItemsTableContent(List<InvoiceItem> invoiceItems, PdfPTable invoiceItemsTable) {
		for (int i = 0; i < invoiceItems.size(); i++) {
			InvoiceItem item = invoiceItems.get(i);
			invoiceItemsTable.addCell(configureCell(String.valueOf(i + 1), NORMAL_8, null, null, true, null, true, Element.ALIGN_CENTER, 5));
			invoiceItemsTable.addCell(configureCell(item.getName(), NORMAL_8, null, null, true, null, false, Element.ALIGN_LEFT, 5));
			invoiceItemsTable.addCell(configureCell(item.getQuantity() + " бр.", NORMAL_8, null, null, true, null, true, Element.ALIGN_RIGHT, 5));
			invoiceItemsTable.addCell(configureCell(NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(item.getItemPrice() != null ? item.getItemPrice() : BigDecimal.ZERO), NORMAL_8, null, null, true, null, true, Element.ALIGN_RIGHT, 5));
			invoiceItemsTable.addCell(configureCell(NumberFormat.getCurrencyInstance(new Locale("bg", "BG")).format(item.getAmount() != null ? item.getAmount() : BigDecimal.ZERO), NORMAL_8, null, null, true, null, true, Element.ALIGN_RIGHT, 5));
		}
	}

	private void createInvoceItemsTableHeader(PdfPTable invoiceItemsTable) {
		invoiceItemsTable.addCell(configureCell("№", BOLD_8, null, BG_GRAY, true, null, true, Element.ALIGN_CENTER, 5));
		invoiceItemsTable.addCell(configureCell("Артикул", BOLD_8, null, BG_GRAY, true, null, true, Element.ALIGN_LEFT, 5));
		invoiceItemsTable.addCell(configureCell("Количество", BOLD_8, null, BG_GRAY, true, null, true, Element.ALIGN_RIGHT, 5));
		invoiceItemsTable.addCell(configureCell("Ед. цена", BOLD_8, null, BG_GRAY, true, null, true, Element.ALIGN_RIGHT, 5));
		invoiceItemsTable.addCell(configureCell("Стойност", BOLD_8, null, BG_GRAY, true, null, true, Element.ALIGN_RIGHT, 5));
	}

	private PdfPTable createDetailsTable(Invoice invoice) throws DocumentException {
		PdfPTable detailsTable = configureTable(2);
		
		createCompanyHeaderTable("Получател",detailsTable);
		createCompanyHeaderTable("Доставчик",detailsTable);
        createCompanyTable(invoice.getRecipient(), detailsTable);
		createCompanyTable(invoice.getIssuer(), detailsTable);
		createDetailsFooterTable(invoice, detailsTable);
        
		return detailsTable;  
	}

	private void createDetailsFooterTable(Invoice invoice, PdfPTable parentTable) throws DocumentException {
		PdfPTable detailsFooterTable = configureTable(2);
		
		detailsFooterTable.addCell(configureCell(invoice.getTypeName(), BOLD_20, null, null, false, null, true, Element.ALIGN_LEFT, 6));	
		createInvoiceNumberTable(invoice.getNumber(), invoice.getIssueDate(), detailsFooterTable);
		
		PdfPCell detailsFooterCell = new PdfPCell();
		detailsFooterCell.setColspan(2);
		detailsFooterCell.addElement(detailsFooterTable);		
		parentTable.addCell(detailsFooterCell);
	}
	
	private void createInvoiceNumberTable(String number, Long issueDate, PdfPTable parentTable) throws DocumentException {
		PdfPTable invoiceDetailsTable = configureTable(2);
		invoiceDetailsTable.setWidths(new int[] {3, 1});
		invoiceDetailsTable.addCell(configureCell("ФАКТУРА", BOLD_20, 2, null, false, null, true, Element.ALIGN_RIGHT, 6));
		invoiceDetailsTable.addCell(configureCell("Номер:", BOLD_8, null, null, false, null, true, Element.ALIGN_RIGHT, 2));
		invoiceDetailsTable.addCell(configureCell(number, BOLD_8, null, BG_GRAY, false, null, true, Element.ALIGN_LEFT, 2));
		invoiceDetailsTable.addCell(configureCell("Дата на издаване:", BOLD_8, null, null, false, null, true, Element.ALIGN_RIGHT, 2));
		invoiceDetailsTable.addCell(configureCell(dateService.convertMillisToDateTimeString(issueDate, "dd.MM.yyyy", false), BOLD_8, null, BG_GRAY, false, null, true, Element.ALIGN_LEFT, 2));
		invoiceDetailsTable.addCell(configureCell("Дата на данъчно събитие:", BOLD_8, null, null, false, null, true, Element.ALIGN_RIGHT, 2));
		invoiceDetailsTable.addCell(configureCell(dateService.convertMillisToDateTimeString(issueDate, "dd.MM.yyyy", false), BOLD_8, null, BG_GRAY, false, null, true, Element.ALIGN_LEFT, 2));
		
		PdfPCell invoiceDetailsCell = new PdfPCell();
		invoiceDetailsCell.setBorder(Rectangle.NO_BORDER);
		invoiceDetailsCell.addElement(invoiceDetailsTable);		
		parentTable.addCell(invoiceDetailsCell);
	}

	private void createCompanyHeaderTable(String header, PdfPTable parentTable) throws DocumentException {
		PdfPTable companyHeaderTable = configureTable(2);
		companyHeaderTable.setWidths(new float[] {1f, 3.6f});
		companyHeaderTable.addCell(configureCell(header, BOLD_8, null, BG_GRAY, false, null, true, Element.ALIGN_LEFT, 2));
		companyHeaderTable.addCell(configureCell("", BOLD_8, null, null, false, null, true, null, null));
		
		PdfPCell companyHeaderCell = new PdfPCell();
		companyHeaderCell.setPadding(0);
		companyHeaderCell.setBorder(Rectangle.NO_BORDER);
		companyHeaderCell.addElement(companyHeaderTable);		
		parentTable.addCell(companyHeaderCell);
	}

	private void createCompanyTable(Company company, PdfPTable parentTable) throws DocumentException {
		PdfPTable backgroundTable = configureTable(1);
		
		PdfPTable companyTable = configureTable(2);
		companyTable.addCell(configureCell(company.getName(), BOLD_8, 2, null, false, null, true, Element.ALIGN_LEFT, 2));
		companyTable.addCell(configureCell(company.getAddress(), BOLD_8, 2, null, false, null, true, Element.ALIGN_LEFT, 2));
		companyTable.addCell(configureCell(company.getCity(), BOLD_8, 2, null, false, null, true, Element.ALIGN_LEFT, 2));
		companyTable.addCell(configureCell(company.getCountry(), BOLD_8, 2, null, false, null, true, Element.ALIGN_LEFT, 2));
		companyTable.addCell(configureCell("ЕИК/Булстат:", NORMAL_8, null, BG_LIGHT_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 2));
		companyTable.addCell(configureCell(company.getNumber(), NORMAL_8, null, null, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 2));
		if (company.getVatNumber() != null) {
			companyTable.addCell(configureCell("ДДС №:", NORMAL_8, null, BG_LIGHT_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 2));
			companyTable.addCell(configureCell(company.getVatNumber(), NORMAL_8, null, null, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 2));
		}
		companyTable.addCell(configureCell("МОЛ:", NORMAL_8, null, BG_LIGHT_GRAY, true, BORDER_GRAY, true, Element.ALIGN_LEFT, 2));
		companyTable.addCell(configureCell(company.getAccountablePerson(), NORMAL_8, null, null, true, BORDER_GRAY, false, Element.ALIGN_LEFT, 2));
		
		PdfPCell companyCell = new PdfPCell();
		companyCell.setPadding(0);
		companyCell.setBackgroundColor(WHITE);
		companyCell.setBorderColor(BORDER_GRAY);
		companyCell.addElement(companyTable);
		backgroundTable.addCell(companyCell);
		
		PdfPCell backgroundCell = new PdfPCell();
		backgroundCell.setPadding(10);
		backgroundCell.setBackgroundColor(BG_GRAY);
		backgroundCell.addElement(backgroundTable);
		parentTable.addCell(backgroundCell);
	}
	
	private PdfPCell configureCell(String text, Font font, Integer colspan, BaseColor backgroundColor,
			Boolean hasBorder, BaseColor borderColor, Boolean noWrap, Integer horizontalAlignment, Integer padding) {
		PdfPCell cell = new PdfPCell(new Paragraph(text, font));
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		
		if (horizontalAlignment != null) {
			cell.setHorizontalAlignment(horizontalAlignment);
		}
		
		if (padding != null) {
			cell.setPadding(padding);
		}

		if (hasBorder) {
			cell.setBorderColor(borderColor);
		} else {
			cell.setBorder(Rectangle.NO_BORDER);
		}

		if (noWrap) {
			cell.setNoWrap(true);
		}

		if (colspan != null) {
			cell.setColspan(colspan);
		}

		if (backgroundColor != null) {
			cell.setBackgroundColor(backgroundColor);
		}

		return cell;
	}

	private PdfPTable configureTable(int numColumns) throws DocumentException {
		PdfPTable table = new PdfPTable(numColumns);
		table.setWidthPercentage(100);

		return table;
	}

	private String numberToWords(BigDecimal number) {
		String numberInWords = new String();

		BigInteger wholePart = number.toBigInteger();
		BigDecimal fractionPart = number.subtract(new BigDecimal(wholePart));

		Locale locale = new Locale("bg", "BG");
		RuleBasedNumberFormat formatter = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);
		numberInWords = formatter.format(wholePart) + " лева";

		if (fractionPart.compareTo(BigDecimal.ZERO) > 0) {
			numberInWords += " и " + formatter.format(fractionPart.multiply(new BigDecimal(100))) + " стотинки";
		}

		return numberInWords;
	}

}
